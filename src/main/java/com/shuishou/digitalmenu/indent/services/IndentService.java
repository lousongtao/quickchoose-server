package com.shuishou.digitalmenu.indent.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.controllers.AccountController;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.models.Configs;
import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.common.models.IConfigsDataAccessor;
import com.shuishou.digitalmenu.common.models.IDeskDataAccessor;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.IIndentDetailDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Category2Printer;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.ICategory2PrinterDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.printertool.PrintJob;
import com.shuishou.digitalmenu.printertool.PrintQueue;
import com.shuishou.digitalmenu.statistics.views.StatItem;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class IndentService implements IIndentService {
	
	private final static Logger logger = LoggerFactory.getLogger(IndentService.class);
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IConfigsDataAccessor configsDA;
	
	@Autowired
	private IUserDataAccessor userDA;

	@Autowired
	private IIndentDataAccessor indentDA;
	
	@Autowired
	private IIndentDetailDataAccessor indentDetailDA;
	
	@Autowired
	private IDeskDataAccessor deskDA;
	
	@Autowired
	private IDishDataAccessor dishDA;
	
	@Autowired
	private IPrinterDataAccessor printerDA;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ICategory2PrinterDataAccessor category2PrinterDA;
	
	private DecimalFormat doubleFormat = new DecimalFormat("0.00");
	
	@Override
	@Transactional
	public synchronized MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount, String comments) {
		Configs configs = configsDA.getConfigsByName(ConstantValue.CONFIGS_CONFIRMCODE);
		if (!confirmCode.equals(configs.getValue()))
			return new MakeOrderResult("The confirm code is wrong, cannot make order.", false, -1);
		Desk desk = deskDA.getDeskById(deskid);
		if (desk == null)
			return new MakeOrderResult("cannot find table by id "+ deskid, false, -1);
		if (desk.getMergeTo() != null){
			//if exist merge desk, first check if the main desk exist indent, if so, do add dish function
			List<Indent> indents = indentDA.getUnpaidIndent(desk.getMergeTo().getName());
			if (indents != null && !indents.isEmpty()){
				return addDishToIndent(desk.getMergeTo().getId(), jsonOrder);
			}
		}
		double totalprice = 0;
		Indent indent = new Indent();
		indent.setStartTime(Calendar.getInstance().getTime());
		indent.setCustomerAmount(customerAmount);
		int sequence = indentDA.getMaxSequenceToday() + 1;
		indent.setDailySequence(sequence);
		indent.setComments(comments);
		//if this table is already merged to other table, which means this is a sub-desk, then do ADDDISH operation to main-desk
		if (desk.getMergeTo() == null){
			indent.setDeskName(desk.getName());
		} else {
			indent.setDeskName(desk.getMergeTo().getName());
		}
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				return new MakeOrderResult("cannot find dish by id "+ dishid, false, -1);
			if (dish.isSoldOut()){
				return new MakeOrderResult("dish "+ dish.getSecondLanguageName() + " is Sold Out, cannot make order", false, -1);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalprice += detail.getAmount() * dish.getPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalprice += detail.getAmount() * dish.getPrice() * detail.getWeight();
			indent.addItem(detail);
//			indentDetailDA.save(detail);
		}
		indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
		indentDA.save(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json", true);
//		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		return new MakeOrderResult(Result.OK, true, sequence);
	}
	
	@Override
	@Transactional
	public synchronized ObjectResult splitIndent(int userId, String confirmCode, JSONArray jsonOrder, int originIndentId, 
			double paidPrice, String payWay, String memberCard) {
		
		Configs configs = configsDA.getConfigsByName(ConstantValue.CONFIGS_CONFIRMCODE);
		if (!confirmCode.equals(configs.getValue()))
			return new ObjectResult("The confirm code is wrong, cannot make order.", false, null);
		Indent originIndent = indentDA.getIndentById(originIndentId);
		if (originIndent == null)
			return new ObjectResult("cannot find order by id "+originIndentId, false, null);
		Desk desk = deskDA.getDeskByName(originIndent.getDeskName());
		if (desk == null)
			return new ObjectResult("cannot find table by name "+ originIndent.getDeskName(), false, null);
		
		double totalprice = 0;
		Indent indent = new Indent();
		indent.setStartTime(Calendar.getInstance().getTime());
		int sequence = indentDA.getMaxSequenceToday() + 1;
		indent.setDailySequence(sequence);
		indent.setComments(originIndent.getComments());
		indent.setDeskName(desk.getName());
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("dishid");
			double weight = 0;
			if (o.has("weight")){
				weight = o.getDouble("weight");
			}
			IndentDetail originDetail = null;
			//find origin IndentDetail object by dishId and weight
			for(IndentDetail d : originIndent.getItems()){
				if (d.getDishId() == dishid && d.getWeight() == weight){
					originDetail = d;
					break;
				}
			}
			if (originDetail == null)
				return new ObjectResult("cannot find IndentDetail by indentid " + originIndentId + ", dishid "+dishid + ", weight "+ weight, false, null);
			
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				return new ObjectResult("cannot find dish by id "+ dishid, false, null);
			int amount = o.getInt("amount");
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(amount);
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalprice += detail.getAmount() * dish.getPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalprice += detail.getAmount() * dish.getPrice() * detail.getWeight();
			indent.addItem(detail);
			//update originIndent and originIndentDetail
			originDetail.setAmount(originDetail.getAmount() - amount);
			if (originDetail.getAmount() == 0){
				originIndent.getItems().remove(originDetail);
				indentDetailDA.delete(originDetail);
			} else {
				indentDetailDA.update(originDetail);
			}
			double totalPrice_OriginIndent = originIndent.getTotalPrice();
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalPrice_OriginIndent -= detail.getAmount() * dish.getPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalPrice_OriginIndent -= detail.getAmount() * dish.getPrice() * detail.getWeight();
			originIndent.setTotalPrice(Double.parseDouble(doubleFormat.format(totalPrice_OriginIndent)));
		}
		indent.setTotalPrice(Double.parseDouble(doubleFormat.format(totalprice)));
		//start to pay the new indent
		indent.setStatus(ConstantValue.INDENT_STATUS_PAID);
		indent.setPaidPrice(Double.parseDouble(doubleFormat.format(paidPrice)));
		indent.setPayWay(payWay);
		indent.setMemberCard(memberCard);
		indent.setEndTime(new Date());
		indentDA.save(indent);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENT_SPLITANDPAY.toString(),
						"User " + selfUser + " operate indent, id =" + indent.getId() + ", from originIndent id = " + originIndentId + ".");
		
		indentDA.update(originIndent);
		//if originIndent is already null for items, then paid it
		//if there are merge desks, clear them status
		if (originIndent.getItems().isEmpty()){
			operateIndent(userId, originIndentId, ConstantValue.INDENT_OPERATIONTYPE_PAY, 0, ConstantValue.INDENT_PAYWAY_CASH, null);
			List<Desk> desks = deskDA.queryDesks();
			for(Desk d : desks){
				if (d.getMergeTo() != null && d.getMergeTo().getId() == desk.getId()){
					d.setMergeTo(null);
					deskDA.updateDesk(desk);
				}
			}
		}
		Hibernate.initialize(originIndent.getItems());
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		
		
		return new ObjectResult(Result.OK, true, originIndent);
	}
	
	
	//在总台打印的单子, 包括对账单, 结账单, 客用单
	@Transactional
	private void printTicket2Counter(Indent indent, String tempfile, String title){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		for(Printer p : printers){
			if (p.getType() != ConstantValue.PRINTER_TYPE_COUNTER)
				continue;
				Map<String,String> keys = new HashMap<String, String>();
				keys.put("sequence", indent.getDailySequence()+"");
				keys.put("customerAmount", indent.getCustomerAmount()+"");
				keys.put("tableNo", indent.getDeskName());
				keys.put("printType", title);
				keys.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
				keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
				keys.put("paidPrice", String.format("%.2f", indent.getPaidPrice()));
				keys.put("gst", String.format("%.2f",(double)(indent.getPaidPrice()/11)));
				keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));
				List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
				for(IndentDetail d : indent.getItems()){
					Dish dish = dishDA.getDishById(d.getDishId());
					Map<String, String> mg = new HashMap<String, String>();
					mg.put("name", d.getDishFirstLanguageName());
					mg.put("price", String.format("%.2f",d.getDishPrice()));
					mg.put("amount", d.getAmount()+"");
					
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += "\n" + d.getWeight();
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
						mg.put("totalPrice", String.format("%.2f",d.getWeight() * d.getDishPrice() * d.getAmount()));
					} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
						mg.put("totalPrice", String.format("%.2f",d.getDishPrice() * d.getAmount()));
					}
					mg.put("requirement", requirement);
					goods.add(mg);
					
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("keys", keys);
				params.put("goods", goods);
				PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
				PrintQueue.add(job);
		}
	}
	
	/**
	 * 创建订单时, 根据订单内的不同内容, 找到对应的打印机, 发送出去
	 * @param indent
	 * @param tempfile
	 * @param isAdd if the indentdetails are for adding, this is true; if these are for cancel, this is false
	 */
	@Transactional
	private void printCucaigoudan2Kitchen(Indent indent, String tempfile, boolean isAdd){
		//把indent下面的dish根据不同的打印输出, 分组
		Map<Printer, List<IndentDetail>> mapPrintDish = new HashMap<Printer, List<IndentDetail>>();
		
		int totalamount = 0;
		for (int i = 0; i < indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			totalamount += detail.getAmount();
			Dish dish = dishDA.getDishById(detail.getDishId());
			List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
			for(Category2Printer cp : cps){
				Printer printer = cp.getPrinter();
				if (mapPrintDish.get(printer) == null){
					mapPrintDish.put(printer, new ArrayList<IndentDetail>());
				}
				mapPrintDish.get(printer).add(detail);
			}
		}
		Iterator<Printer> keys = mapPrintDish.keySet().iterator();
		while(keys.hasNext()){
			Printer p = keys.next();
			Map<String,String> keyMap = new HashMap<String, String>();
			if (isAdd)
				keyMap.put("title", "出菜勾单");
			else 
				keyMap.put("title", "出菜勾单 - 取消单");
			keyMap.put("tableNumb", indent.getDeskName());
			keyMap.put("orderId", indent.getDailySequence()+"");
			keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
			keyMap.put("amountOnThisTable", totalamount + "");
			keyMap.put("indentcomments", indent.getComments() == null ? "" : indent.getComments());
			int amount = 0;
			List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
			List<IndentDetail> detailList = mapPrintDish.get(p);
			/**
			 * sort these dishes by sequence, firstly compare by category1's sequence, then category2's sequence,
			 * at last compare dish's sequence
			 */
			Collections.sort(detailList, new Comparator<IndentDetail>(){

				@Override
				public int compare(IndentDetail o1, IndentDetail o2) {
					Dish dish1 = dishDA.getDishById(o1.getDishId());
					Dish dish2 = dishDA.getDishById(o2.getDishId());
					if (dish1.getCategory2().getCategory1().getId() == dish2.getCategory2().getCategory1().getId()){
						if (dish1.getCategory2().getId() == dish2.getCategory2().getId()){
							return dish1.getSequence() - dish2.getSequence();
						} else {
							return dish1.getCategory2().getSequence() - dish2.getCategory2().getSequence();
						}
					} else {
						return dish1.getCategory2().getCategory1().getSequence() - dish2.getCategory2().getCategory1().getSequence();
					}	
				}});
			for (int ij = 0; ij < detailList.size(); ij++) {
				IndentDetail d = detailList.get(ij);
				Map<String, String> mg = new HashMap<String, String>();
				Dish dish = dishDA.getDishById(d.getDishId());
				for (int i = 0; i < d.getAmount(); i++) {// 每个菜品单独打印一行,重复的打印多行
					mg.put("name", d.getDishFirstLanguageName());
					mg.put("amount", "1");
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += " " + d.getWeight();
					mg.put("requirement", requirement);
					
					amount++;
					goods.add(mg);
				}
			}
			keyMap.put("amountOnThisTicket", amount + "");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("keys", keyMap);
			params.put("goods", goods);
			PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
			PrintQueue.add(job);
		}
	}
	
	/**
	 * print a Chucaigoudan for a special IndentDetail. it maybe for add dish, or delete dish, or change amount 
	 * @param indent
	 * @param tempfile
	 * @param changedAmount maybe negative or positive; if is negative, it means this dish is canceled or change amount
	 */
	@Transactional
	private void printCucaigoudan2Kitchen4ChangeAmount(IndentDetail detail, String tempfile, int changedAmount){
		Indent indent = detail.getIndent();
		Dish dish = dishDA.getDishById(detail.getDishId());
		
		int totalamount = 0;
		for (IndentDetail d : indent.getItems()){
			totalamount += d.getAmount();
		}
		
		Map<String,String> keyMap = new HashMap<String, String>();
		if (changedAmount > 0)
			keyMap.put("title", "出菜勾单");
		else 
			keyMap.put("title", "出菜勾单-取消单");
		keyMap.put("tableNumb", indent.getDeskName());
		keyMap.put("orderId", indent.getDailySequence()+"");
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
		keyMap.put("amountOnThisTable", totalamount + "");
		keyMap.put("indentcomments", indent.getComments() == null ? "" : indent.getComments());
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
		Map<String, String> mg = new HashMap<String, String>();
		for (int i = 0; i < Math.abs(changedAmount); i++) {//每个菜品单独打印一行, 重复的打印多行
			mg.put("name", detail.getDishFirstLanguageName());
			mg.put("amount", "1");
			String requirement = "";
			if (detail.getAdditionalRequirements() != null)
				requirement += detail.getAdditionalRequirements();
			//按重量卖的dish, 把重量加入requirement
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				requirement += "\n" + detail.getWeight();
			mg.put("requirement", requirement);
			goods.add(mg);
		}
		keyMap.put("amountOnThisTicket", Math.abs(changedAmount) + "");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		params.put("goods", goods);
		
		List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
		for(Category2Printer cp : cps){
			PrintJob job = new PrintJob(tempfile, params, cp.getPrinter().getPrinterName());
			PrintQueue.add(job);
		}
	}
	
	/**
	 * 加单时打印的出菜勾单, this function is used for add more than one dish
	 * @param details
	 * @param tempfile
	 */
	@Transactional
	private void printCucaigoudan2Kitchen(ArrayList<IndentDetail> details, String tempfile) {
		// 把indent下面的dish根据不同的打印输出, 分组
		Map<Printer, List<IndentDetail>> mapPrintDish = new HashMap<Printer, List<IndentDetail>>();

		int totalamount = 0;
		Indent indent = details.get(0).getIndent();
		for (int i = 0; i< indent.getItems().size(); i++){
			totalamount += indent.getItems().get(i).getAmount();
		}
		for (int i = 0; i < details.size(); i++) {
			Dish dish = dishDA.getDishById(details.get(i).getDishId());
			List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
			for(Category2Printer cp : cps){
				Printer printer = cp.getPrinter();
				if (mapPrintDish.get(printer) == null){
					mapPrintDish.put(printer, new ArrayList<IndentDetail>());
				}
				mapPrintDish.get(printer).add(details.get(i));
			}
		}
		
		String tableNo = details.get(0).getIndent().getDeskName();
		String sequence = String.valueOf(details.get(0).getIndent().getDailySequence());
		String datatime = ConstantValue.DFYMDHMS.format(details.get(0).getIndent().getStartTime());
		Iterator<Printer> keys = mapPrintDish.keySet().iterator();
		while (keys.hasNext()) {
			Printer p = keys.next();
			Map<String, String> keyMap = new HashMap<String, String>();
			keyMap.put("title", "出菜勾单");
			keyMap.put("tableNumb", tableNo);
			keyMap.put("orderId", sequence);
			keyMap.put("dateTime", datatime);
			keyMap.put("amountOnThisTable", totalamount + "");
			keyMap.put("indentcomments", indent.getComments() == null ? "" : indent.getComments());
			int amount = 0;
			List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
			List<IndentDetail> detailList = mapPrintDish.get(p);
			/**
			 * sort these dishes by sequence, firstly compare by category1's sequence, then category2's sequence,
			 * at last compare dish's sequence
			 */
			Collections.sort(detailList, new Comparator<IndentDetail>(){

				@Override
				public int compare(IndentDetail o1, IndentDetail o2) {
					Dish dish1 = dishDA.getDishById(o1.getDishId());
					Dish dish2 = dishDA.getDishById(o2.getDishId());
					if (dish1.getCategory2().getCategory1().getId() == dish2.getCategory2().getCategory1().getId()){
						if (dish1.getCategory2().getId() == dish2.getCategory2().getId()){
							return dish1.getSequence() - dish2.getSequence();
						} else {
							return dish1.getCategory2().getSequence() - dish2.getCategory2().getSequence();
						}
					} else {
						return dish1.getCategory2().getCategory1().getSequence() - dish2.getCategory2().getCategory1().getSequence();
					}	
				}});
			for (int ij = 0; ij < detailList.size(); ij++) {
				IndentDetail d = detailList.get(ij);
				Map<String, String> mg = new HashMap<String, String>();
				Dish dish = dishDA.getDishById(d.getDishId());
				for (int i = 0; i < d.getAmount(); i++) {// 每个菜品单独打印一行,重复的打印多行
					mg.put("name", d.getDishFirstLanguageName());
					mg.put("amount", "1");
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += " " + d.getWeight();
					mg.put("requirement", requirement);
					amount++;
					goods.add(mg);
				}
			}
			keyMap.put("amountOnThisTicket", amount + "");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("keys", keyMap);
			params.put("goods", goods);
			PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
			PrintQueue.add(job);
		}
	}

//	@Transactional
//	private void print(Indent indent){
//		List<Printer> printers = printerDA.queryPrinters();
//		if (printers == null || printers.isEmpty())
//			return;
//		for(Printer p : printers){
//				String tempfile = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE + "/";
//				if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_TOGETHER){
//					tempfile += "print_together.json";
//					Map<String,String> keys = new HashMap<String, String>();
//					keys.put("restaurantname", "restaurantname");
//					keys.put("desk", indent.getDeskName());
//					keys.put("sequence", indent.getDailySequence()+"");
//					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
//					keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
//					List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
//					for(IndentDetail d : indent.getItems()){
//						Map<String, String> mg = new HashMap<String, String>();
//						mg.put("name", d.getDishFirstLanguageName());
//						mg.put("price", d.getDishPrice()+"");
//						mg.put("amount", d.getAmount()+"");
//						mg.put("totalPrice", (d.getDishPrice() * d.getAmount()) + "");
//						mg.put("requirement", d.getAdditionalRequirements());
//						goods.add(mg);
//						
//					}
//					Map<String, Object> params = new HashMap<String, Object>();
//					params.put("keys", keys);
//					params.put("goods", goods);
//					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
//					PrintQueue.add(job);
//				}else if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_SEPARATELY){
//					tempfile += "print_separately.json";
//					for(IndentDetail d : indent.getItems()){
//						Map<String,String> keys = new HashMap<String, String>();
//						keys.put("restaurantname", "restaurantname");
//						keys.put("desk", indent.getDeskName());
//						keys.put("sequence", indent.getDailySequence()+"");
//						keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
//						keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
//						keys.put("dishname", d.getDishFirstLanguageName());
//						keys.put("amount", d.getAmount()+"");
//						keys.put("requirement", d.getAdditionalRequirements());
//						Map<String, Object> params = new HashMap<String, Object>();
//						params.put("keys", keys);
//						PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
//						PrintQueue.add(job);
//					}
//				}
//				
//		}
//	}
	
//	@Transactional
//	private void print(IndentDetail detail){
//		List<Printer> printers = printerDA.queryPrinters();
//		if (printers == null || printers.isEmpty())
//			return;
//		Indent indent = detail.getIndent();
//		
//		for(Printer p : printers){
//			int copy = p.getCopy();
//			for(int i = 0; i< copy; i++){
//				String tempfile = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE + "/";
//				if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_SEPARATELY){
//					tempfile += "print_separately.json";
//					Map<String,String> keys = new HashMap<String, String>();
//					keys.put("restaurantname", "restaurantname");
//					keys.put("desk", indent.getDeskName());
//					keys.put("sequence", indent.getDailySequence()+"");
//					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
//					keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
//					keys.put("dishname", detail.getDishFirstLanguageName());
//					keys.put("amount", detail.getAmount()+"");
//					keys.put("requirement", detail.getAdditionalRequirements());
//					Map<String, Object> params = new HashMap<String, Object>();
//					params.put("keys", keys);
//					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
//					PrintQueue.add(job);
//				}
//			}
//		}
//	}

	@Override
	@Transactional
	public ObjectListResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, String orderby, String orderbydesc) {
		if (orderby != null && orderby.length() > 0
				&& orderbydesc != null && orderbydesc.length() > 0){
			return new ObjectListResult("orderby and orderbydesc are conplicted", false);
		}
		Byte[] bStatus = null;//default as null. if param "status" is not null, then initial this array.
		if (status != null && status.length() > 0){
			bStatus = new Byte[4];// just initial this array while status param is not null
			if (status.indexOf("Paid") >= 0)
				bStatus[0] = ConstantValue.INDENT_STATUS_PAID;
			if (status.indexOf("Unpaid") >= 0)
				bStatus[1] = ConstantValue.INDENT_STATUS_OPEN;
			if (status.indexOf("Other") >= 0){
				bStatus[2] = ConstantValue.INDENT_STATUS_CANCELED;
				bStatus[3] = ConstantValue.INDENT_STATUS_CLOSED;
			}
		}
		List<String> orderbys = null;//default as null. if param "orderby" is not null, then initial this array.
		if (orderby != null && orderby.length() > 0){
			orderbys = new ArrayList<String>();
			if (orderby.indexOf("starTtime")>=0){
				orderbys.add("startTime");
			}
			if (orderby.indexOf("status") >= 0){
				orderbys.add("status");
			}
			if (orderby.indexOf("deskname")>= 0){
				orderbys.add("deskName");
			}
		} 
		
		List<String> orderbydescs = null;
		if (orderbydesc != null && orderbydesc.length() > 0){
			orderbydescs = new ArrayList<>();
			if (orderbydesc.indexOf("id") >= 0){
				orderbydescs.add("id");
			}	
			if (orderbydesc.indexOf("starTtime")>=0){
				orderbydescs.add("startTime");
			}
		}
		
		Date starttime = null;
		Date endtime = null;
		if (sstarttime != null && sstarttime.length() > 0){
			try {
				starttime = ConstantValue.DFYMDHMS.parse(sstarttime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (sendtime != null && sendtime.length() > 0){
			try {
				endtime = ConstantValue.DFYMDHMS.parse(sendtime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int count = indentDA.getIndentCount(starttime, endtime, bStatus, deskname);
		if (count >= 300)
			return new ObjectListResult("Record is over 300, please change the filter", false, null, count);
		List<Indent> indents = indentDA.getIndents(start, limit, starttime, endtime, bStatus, deskname, orderbys, orderbydescs);
		if (indents == null || indents.isEmpty())
			return new ObjectListResult(Result.OK, true, null, 0);
		for (int i = 0; i < indents.size(); i++) {
			Indent indent = indents.get(i);
			Hibernate.initialize(indent);
			Hibernate.initialize(indent.getItems());
		}
		return new ObjectListResult(Result.OK, true, (ArrayList<Indent>)indents, count);
	}

	@Override
	@Transactional
	public OperateIndentResult operateIndent(int userId, int indentId, byte operationType, double paidPrice, String payWay, String memberCard) {
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null)
			return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
		String logtype = LogData.LogType.INDENT_PAY.toString();
		if (operationType == ConstantValue.INDENT_OPERATIONTYPE_CANCEL){
			logtype = LogData.LogType.INDENT_CANCEL.toString();
		}
		if (operationType == ConstantValue.INDENT_OPERATIONTYPE_CANCEL){
			indent.setStatus(ConstantValue.INDENT_STATUS_CANCELED);
		} else if (operationType == ConstantValue.INDENT_OPERATIONTYPE_PAY){
			indent.setStatus(ConstantValue.INDENT_STATUS_PAID);
			indent.setPaidPrice(Double.parseDouble(doubleFormat.format(paidPrice)));
			indent.setPayWay(payWay);
			indent.setMemberCard(memberCard);
		}
		indent.setEndTime(new Date());
		indentDA.update(indent);
		
		//clear merge table record if exists
		Desk maindesk = deskDA.getDeskByName(indent.getDeskName());
		if (maindesk == null){
			logger.error("cannot find desk by name : " + indent.getDeskName());
		} else {
			List<Desk> alldesks = deskDA.queryDesks();
			for(Desk d : alldesks){
				if (d.getMergeTo() != null && d.getMergeTo().getId() == maindesk.getId()){
					d.setMergeTo(null);
				}
			}
		}
		
		if (operationType == ConstantValue.INDENT_OPERATIONTYPE_PAY){
			String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
			printTicket2Counter(indent, tempfilePath + "/payorder_template.json", "结账单");
		} else if (operationType == ConstantValue.INDENT_OPERATIONTYPE_CANCEL){
			String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
			printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json", false);
		}
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate indent, id =" + indentId + ", operationType = " + logtype + ".");
		return new OperateIndentResult(Result.OK, true);
	}

	@Override
	@Transactional
	/**
	 * 
	 * @param indentId
	 * @param dishId
	 * @param amount this value is just for the number after modify; for example, the original amount is 5, the input value is 2,
	 * 					after operation, the final amount is 2(it means there are 3 removed); 
	 * @param operateType
	 * @return
	 */
	public OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType) {
		String logtype = null;
		Indent indent = null;
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT){
			logtype = LogData.LogType.INDENTDETAIL_CHANGEAMOUNT.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			int originalAmount = detail.getAmount();
			if (amount == originalAmount){
				
			}
			detail.setAmount(amount);
			indentDetailDA.update(detail);
			indent = detail.getIndent();
			double totalprice = 0.0d;
			for(IndentDetail d : indent.getItems()){
				Dish dish = dishDA.getDishById(d.getDishId());
				if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
					totalprice += d.getAmount() * d.getDishPrice();
				else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
					totalprice += d.getAmount() * d.getDishPrice() * d.getWeight();
			}
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
			indentDA.update(indent);
			printCucaigoudan2Kitchen4ChangeAmount(detail, tempfilePath + "/cucaigoudan.json", amount - originalAmount);
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE){
			logtype = LogData.LogType.INDENTDETAIL_DELETE.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			indent = detail.getIndent();
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			Dish dish = dishDA.getDishById(detail.getDishId());
			if (dish == null)
				return new OperateIndentResult("cannot find dish by id:" + detail.getDishId(), false);
			indent.getItems().remove(detail);//must remove from the collection firstly, otherwise hibernate will rebuild the detail object by cascade
			indentDetailDA.delete(detail);
			double newPrice = detail.getIndent().getTotalPrice();
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				newPrice -= detail.getAmount() * detail.getWeight() * detail.getDishPrice();
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				newPrice -= detail.getAmount() * detail.getDishPrice();
			}
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(newPrice)));
			indentDA.update(detail.getIndent());
			printCucaigoudan2Kitchen4ChangeAmount(detail, tempfilePath + "/cucaigoudan.json", detail.getAmount() * (-1));
		} 
		OperateIndentResult result = new OperateIndentResult("ok", true);
		if (indent != null){
			result.data = new OperateIndentResult.Indent();
			result.data.id = indent.getId();
			result.data.startTime = ConstantValue.DFHMS.format(indent.getStartTime());
			result.data.customerAmount = indent.getCustomerAmount();
			result.data.deskName = indent.getDeskName();
			result.data.totalPrice = indent.getTotalPrice();
			for (int i = 0; i < indent.getItems().size(); i++) {
				IndentDetail d = indent.getItems().get(i);
				OperateIndentResult.IndentDetail dinfo = new OperateIndentResult.IndentDetail();
				dinfo.additionalRequirements = d.getAdditionalRequirements();
				dinfo.amount = d.getAmount();
				dinfo.dishFirstLanguageName = d.getDishFirstLanguageName();
				dinfo.dishSecondLanguageName = d.getDishSecondLanguageName();
				dinfo.dishId = d.getDishId();
				dinfo.dishPrice = d.getDishPrice();
				dinfo.id = d.getId();
				dinfo.weight = d.getWeight();
				result.data.items.add(dinfo);
			}
		}
		
		// write log.
		if (userId != 0){
			UserData selfUser = userDA.getUserById(userId);
			logService.write(selfUser, logtype,
				"User " + selfUser + " operate IndentDetail, indentId = " + indentId 
				+ ", dishId = " + dishId + ", indentdetailId = " + indentDetailId 
				+ ", amount = " + amount + ", operationType = " + logtype + ".");
		}
		
		return result;
		
	}


	@Override
	@Transactional
	public ObjectResult printIndent(int userId, int indentId) {
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null){
			return new ObjectResult("No order for ID : " + indentId, false);
		}
//		print(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENT_PRINT.toString(),
				"User " + selfUser + " operate IndentDetail, indentId = " + indentId + ".");
		return new ObjectResult(Result.OK, true);
	}


	//清除无法使用的桌台数据, 比如已经并桌无法开桌的, 无法结账的
	@Override
	@Transactional
	public ObjectResult clearDesk(int userId, int deskId) {
		Desk desk = deskDA.getDeskById(deskId);
		if (desk == null){
			return new ObjectResult("cannot find desk by id " + deskId, false);
		}
		if (desk.getMergeTo() != null){
			desk.setMergeTo(null);
			deskDA.updateDesk(desk);
		}
		String indentIds = "";
		List<Indent> indents = indentDA.getIndents(0, 100, null, null, new Byte[]{ConstantValue.INDENT_STATUS_OPEN}, desk.getName(), null, null);
		if (indents != null && !indents.isEmpty()){
			for(Indent indent : indents){
				indent.setStatus(ConstantValue.INDENT_STATUS_FORCEEND);
				indentDA.save(indent);
				indentIds += indent.getId()+",";
			}
		}
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENT_FORCEEND.toString(),
				"User " + selfUser + " clear table id("+deskId+"), affect indentId (" + indentIds + ").");
		
		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public MakeOrderResult addDishToIndent(int deskId, JSONArray jsonOrder) {
		Desk desk = deskDA.getDeskById(deskId);
		if (desk == null){
			return new MakeOrderResult("cannot find desk by id " + deskId, false, -1);
		}
		List<Indent> indents = indentDA.getUnpaidIndent(desk.getName());
		if (indents == null || indents.isEmpty()){
			return new MakeOrderResult("cannot find order on desk " + desk.getName(), false, -1);
		}
		ArrayList<IndentDetail> listPrintDetails = new ArrayList<>();
		Indent indent = indents.get(0);
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				return new MakeOrderResult("cannot find dish by id "+ dishid, false, -1);
			if (dish.isSoldOut()){
				return new MakeOrderResult("dish "+ dish.getSecondLanguageName() + " is Sold Out, cannot make order", false, -1);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			double totalprice = 0;
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalprice = indent.getTotalPrice() + detail.getAmount() * dish.getPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalprice = indent.getTotalPrice() + detail.getAmount() * dish.getPrice() * detail.getWeight();
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
//			indent.addItem(detail);//这里不能把detail加入indent集合, 在最终提交前, 如果调用indent.getItems, 会有两个相同的detail出现在集合中; 
									//如果此处不添加, 不影响对数据库的存储. 具体原理不清楚.
			indentDetailDA.save(detail);
			listPrintDetails.add(detail);
		}
		indentDA.update(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(listPrintDetails, tempfilePath + "/cucaigoudan.json");
//		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		listPrintDetails.clear();//release this beans from collection to avoid hibernate exception
		return new MakeOrderResult(Result.OK, true, indent.getDailySequence());
	}

	@Override
	@Transactional
	public ObjectResult changeDesks(int userId, int srcDeskId, int destDeskId) {
		Desk srcDesk = deskDA.getDeskById(srcDeskId);
		if (srcDesk == null){
			return new MakeOrderResult("cannot find desk by id " + srcDeskId, false, -1);
		}
		Desk destDesk = deskDA.getDeskById(destDeskId);
		if (destDesk == null){
			return new MakeOrderResult("cannot find desk by id " + destDeskId, false, -1);
		}
		List<Indent> srcIndents = indentDA.getUnpaidIndent(srcDesk.getName());
		List<Indent> destIndents = indentDA.getUnpaidIndent(destDesk.getName());
		
		if (srcIndents == null || srcIndents.isEmpty()){
			return new ObjectResult("tables " + srcDesk.getName() + " is empty, no need to do change", false);
		}
		if (destIndents != null && !destIndents.isEmpty()){
			return new ObjectResult("tables " + destDesk.getName() + " is not empty, cannot do change", false);
		}
		Indent indent = srcIndents.get(0);
		indent.setDeskName(destDesk.getName());
		ArrayList<Desk> changedDesks = new ArrayList();
		changedDesks.add(srcDesk);
		List<Desk> alldesks = deskDA.queryDesks();
		for(Desk d : alldesks){
			if (d.getMergeTo() != null && d.getMergeTo().getId() == srcDeskId){
				d.setMergeTo(destDesk);
				deskDA.updateDesk(d);
				changedDesks.add(d);
			}
		}
		indentDA.save(indent);
		
		//prepare return data
		List<GetDeskWithIndentResult.DeskWithIndent> deskinfos = new ArrayList<>();
		GetDeskWithIndentResult.DeskWithIndent deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
		deskinfo.id = destDeskId;
		deskinfo.name = destDesk.getName();
		Hibernate.initialize(indent);
		Hibernate.initialize(indent.getItems());
		deskinfo.indent = indent;
//		deskinfo.price = indent.getTotalPrice();
//		deskinfo.customerAmount = indent.getCustomerAmount();
//		deskinfo.startTime = ConstantValue.DFYMDHMS.format(indent.getStartTime());
		deskinfos.add(deskinfo);
		for(Desk desk : changedDesks){
			deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
			deskinfo.id = desk.getId();
			deskinfo.name = desk.getName();
			if (desk.getMergeTo() != null)
				deskinfo.mergeTo = desk.getMergeTo().getName();
			deskinfos.add(deskinfo);
		}		
		return new ObjectResult(Result.OK, true, deskinfos);
	}

	
	
}
