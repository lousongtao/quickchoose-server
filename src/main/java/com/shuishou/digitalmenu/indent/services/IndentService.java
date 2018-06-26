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
import com.shuishou.digitalmenu.DataCheckException;
import com.shuishou.digitalmenu.ServerProperties;
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
import com.shuishou.digitalmenu.member.services.IMemberCloudService;
import com.shuishou.digitalmenu.member.services.IMemberService;
import com.shuishou.digitalmenu.menu.models.Category2Printer;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.DishMaterialConsume;
import com.shuishou.digitalmenu.menu.models.ICategory2PrinterDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.printertool.PrintJob;
import com.shuishou.digitalmenu.printertool.PrintQueue;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialRecordDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.Material;
import com.shuishou.digitalmenu.rawmaterial.models.MaterialRecord;
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
	private IMaterialRecordDataAccessor materialRecordDA;
	
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
	
	@Autowired
	private IMemberService memberService;
	
	@Autowired
	private IMemberCloudService memberCloudService;
	
	private DecimalFormat doubleFormat = new DecimalFormat("0.00");
	
	@Override
	@Transactional
	public synchronized MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount, String comments) throws DataCheckException{
		long l1 = System.currentTimeMillis();
		Configs configs = configsDA.getConfigsByName(ConstantValue.CONFIGS_CONFIRMCODE);
		if (!confirmCode.equals(configs.getValue()))
			return new MakeOrderResult("The confirm code is wrong, cannot make order.", false, -1);
		Desk desk = deskDA.getDeskById(deskid);
		if (desk == null)
			return new MakeOrderResult("cannot find table by id "+ deskid, false, -1);
		List<Indent> indents = indentDA.getUnpaidIndent(desk.getName());
		if (indents != null && !indents.isEmpty()){
			return new MakeOrderResult("The table is occupied now, cannot open table. Please refresh and redo.", false, -1);
		}
		if (desk.getMergeTo() != null){
			//if exist merge desk, first check if the main desk exist indent, if so, do add dish function
			List<Indent> mainTableIndents = indentDA.getUnpaidIndent(desk.getMergeTo().getName());
			if (mainTableIndents != null && !mainTableIndents.isEmpty()){
				return addDishToIndent(desk.getMergeTo().getId(), jsonOrder);
			}
		}
		//if there are already exist order, then 
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
		Date now = new Date();
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				throw new DataCheckException("cannot find dish by id "+ dishid);
			if (dish.isSoldOut()){
				throw new DataCheckException("dish "+ dish.getFirstLanguageName() + " is Sold Out, cannot make order");
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setTime(now);
			detail.setAmount(o.getInt("amount"));
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(o.getDouble("dishPrice"));
			if (o.has("operator"))
				detail.setOperator(o.getString("operator"));
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				totalprice += detail.getAmount() * detail.getDishPrice();
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				totalprice += detail.getAmount() * detail.getDishPrice() * detail.getWeight();
			}
			indent.addItem(detail);
		}
		indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
		indentDA.save(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json", true);
//		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to make indent"); 
		return new MakeOrderResult(Result.OK, true, sequence);
	}
	
	@Override
	@Transactional(rollbackFor = DataCheckException.class)
	public ObjectResult splitIndent(int userId, String confirmCode, JSONArray jsonOrder, int originIndentId, 
			double paidPrice, double paidCash, String payWay, String discountTemplate, String memberCard, String memberPassword) throws DataCheckException {
		long l1 = System.currentTimeMillis();
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
		Date now = new Date();
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
			detail.setTime(now);
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(o.getDouble("dishPrice"));
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalprice += detail.getAmount() * detail.getDishPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalprice += detail.getAmount() * detail.getDishPrice() * detail.getWeight();
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
				totalPrice_OriginIndent -= detail.getAmount() * detail.getDishPrice() ;
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalPrice_OriginIndent -= detail.getAmount() * detail.getDishPrice()  * detail.getWeight();
			originIndent.setTotalPrice(Double.parseDouble(doubleFormat.format(totalPrice_OriginIndent)));
		}
		indent.setTotalPrice(Double.parseDouble(doubleFormat.format(totalprice)));
		//start to pay the new indent
		indent.setStatus(ConstantValue.INDENT_STATUS_PAID);
		indent.setPaidPrice(Double.parseDouble(doubleFormat.format(paidPrice)));
		indent.setPayWay(payWay);
		indent.setDiscountTemplate(discountTemplate);
		indent.setMemberCard(memberCard);
		indent.setEndTime(now);
		indentDA.save(indent);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENT_SPLITANDPAY.toString(),
						"User " + selfUser + " operate indent, id =" + indent.getId() + ", from originIndent id = " + originIndentId + ".");
		
		indentDA.update(originIndent);
		//if originIndent is already null for items, then paid it
		//if there are merge desks, clear them status
		if (originIndent.getItems().isEmpty()){
			doPayIndent(userId, originIndentId, 0, paidCash, ConstantValue.INDENT_PAYWAY_CASH, discountTemplate, null, null);
			List<Desk> desks = deskDA.queryDesks();
			for(Desk d : desks){
				if (d.getMergeTo() != null && d.getMergeTo().getId() == desk.getId()){
					d.setMergeTo(null);
					deskDA.updateDesk(desk);
				}
			}
		}
		Hibernate.initialize(originIndent.getItems());
		
		//record member consumption, if there are wrong, throw exception for rollback
		if (ConstantValue.INDENT_PAYWAY_MEMBER.equals(payWay)){
			ObjectResult result;
			if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
				result = memberService.recordMemberConsumption(memberCard, memberPassword, paidPrice);
			} else {
				result = memberCloudService.recordMemberConsumption(memberCard, memberPassword, paidPrice);
			}
			if (!result.success)
				throw new DataCheckException(result.result);
		}
		
//		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
//		printTicket2Counter(indent, tempfilePath + "/payorder_template.json", "对账单", paidCash);
		
		//把原始订单和新订单一起返回
		ArrayList<Indent> indents = new ArrayList<>();
		indents.add(originIndent);
		indents.add(indent);
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to make indent");
		return new ObjectResult(Result.OK, true, indents);
	}
	
	
	//在总台打印的单子, 包括对账单, 结账单, 客用单
	@Transactional
	private void printTicket2Counter(Indent indent, String tempfile, String title, double paidCash){
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
				keys.put("payway", indent.getPayWay());
				if (paidCash > indent.getPaidPrice()){
					keys.put("change", String.format(ConstantValue.FORMAT_DOUBLE,paidCash - indent.getPaidPrice()));
				} else {
					keys.put("change", "0");
				}
				List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
				for(IndentDetail d : indent.getItems()){
					Dish dish = dishDA.getDishById(d.getDishId());
					if (dish == null)
						continue;
					Map<String, String> mg = new HashMap<String, String>();
					mg.put("name", d.getDishFirstLanguageName());
					mg.put("price", String.format("%.2f",d.getDishPrice()));
					mg.put("amount", d.getAmount()+"");
					
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += " " + d.getWeight();
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
		Map<Printer, List<IndentDetail_PrintStyle>> mapPrintDish = new HashMap<>();
		
		int totalamount = 0;
		
		for (int i = 0; i < indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			totalamount += detail.getAmount();
			Dish dish = dishDA.getDishById(detail.getDishId());
			if (dish == null)
				continue;
			List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
			for(Category2Printer cp : cps){
				Printer printer = cp.getPrinter();
				if (mapPrintDish.get(printer) == null){
					mapPrintDish.put(printer, new ArrayList<IndentDetail_PrintStyle>());
				}
				IndentDetail_PrintStyle idps = new IndentDetail_PrintStyle();
				idps.indentDetail = detail;
				idps.printStyle = cp.getPrintStyle();
				mapPrintDish.get(printer).add(idps);
			}
		}
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
		keyMap.put("operator", indent.getItems().get(0).getOperator());
		printCucaigoudan2KitchenWithPrintStyle(mapPrintDish, tempfile, keyMap, isAdd);
	}
	
	/**
	 * receive a map with Printer and the IndentDetail list 
	 * loop every Printer and print the IndentDetail as the printstyle
	 * @param mapPrintDish key = Printer, value = list of indentdetail with printstyle info
	 * @param tempfile, the file of print template
	 * @param keyMap, some basic information value needed to be printed on the ticket
	 * @param isAdd, if this is a cancel order, isAdd = false, otherwise isAdd = true
	 */
	@Transactional
	private void printCucaigoudan2KitchenWithPrintStyle(Map<Printer, List<IndentDetail_PrintStyle>> mapPrintDish, String tempfile, Map<String,String> keyMap, boolean isAdd){
		Iterator<Printer> keys = mapPrintDish.keySet().iterator();
		while(keys.hasNext()){
			Printer p = keys.next();
			
			List<IndentDetail_PrintStyle> detailList = mapPrintDish.get(p);
			
			/**
			 * 每个打印机对应的IndentDetail列表中, 都包含分单打印和整单打印两种类型的数据;
			 * 首先挑出来需要分单打印的, 单独打印出来, 
			 * 然后再把整单打印的一起输出
			 * 两种情况, 都要考虑到amount的值, 如果该值大于1, 需要多次打印
			 */
			ArrayList<IndentDetail_PrintStyle> printSeparateDetailList = new ArrayList<IndentDetail_PrintStyle>();//存储需要单独打印的数据
			for (int ij = detailList.size() -1; ij >= 0; ij--) {
				IndentDetail_PrintStyle idsp = detailList.get(ij);
				if (idsp.printStyle == ConstantValue.PRINT_STYLE_SEPARATELY){
					printSeparateDetailList.add(idsp);
					detailList.remove(ij);//把这个IndentDetail从列表中剔除
				}
			}
			//需要独立打印的单子
			for (int j = 0; j < printSeparateDetailList.size(); j++) {
				
				IndentDetail_PrintStyle idsp = printSeparateDetailList.get(j);
				Map<String, String> mg = new HashMap<String, String>();
				Dish dish = dishDA.getDishById(idsp.indentDetail.getDishId());
				if (dish == null)
					continue;
				for (int i = 0; i < idsp.indentDetail.getAmount(); i++) {
					List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
					if (isAdd)
						mg.put("name", idsp.indentDetail.getDishFirstLanguageName());
					else 
						mg.put("name", "Canceled " + idsp.indentDetail.getDishFirstLanguageName());
					mg.put("amount", "1");
					String requirement = "";
					if (idsp.indentDetail.getAdditionalRequirements() != null)
						requirement += idsp.indentDetail.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += " " + idsp.indentDetail.getWeight();
					mg.put("requirement", requirement);
					
					goods.add(mg);
					
					keyMap.put("amountOnThisTicket", "1");

					Map<String, Object> params = new HashMap<String, Object>();
					params.put("keys", keyMap);
					params.put("goods", goods);
					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
					PrintQueue.add(job);
				}
			}
			//打印剩余所有到一个单子上
			if (!detailList.isEmpty()){
				/**
				 * sort these dishes by sequence, firstly compare by category1's sequence, then category2's sequence,
				 * at last compare dish's sequence
				 */
				Collections.sort(detailList, comparator);
				int amount = 0;
				List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
				for (int ij = 0; ij < detailList.size(); ij++) {
					IndentDetail_PrintStyle idsp = detailList.get(ij);
					Map<String, String> mg = new HashMap<String, String>();
					Dish dish = dishDA.getDishById(idsp.indentDetail.getDishId());
					if (dish == null)
						continue;
					for (int i = 0; i < idsp.indentDetail.getAmount(); i++) {// 每个菜品单独打印一行,重复的打印多行
						if (isAdd)
							mg.put("name", idsp.indentDetail.getDishFirstLanguageName());
						else 
							mg.put("name", "Canceled" + idsp.indentDetail.getDishFirstLanguageName());
						mg.put("amount", "1");
						String requirement = "";
						if (idsp.indentDetail.getAdditionalRequirements() != null)
							requirement += idsp.indentDetail.getAdditionalRequirements();
						//按重量卖的dish, 把重量加入requirement
						if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
							requirement += " " + idsp.indentDetail.getWeight();
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
	}
	
	/**
	 * print a Chucaigoudan for a special IndentDetail. it maybe for add dish, or delete dish, or change amount 
	 * @param indent
	 * @param tempfile
	 * @param changedAmount maybe negative or positive; if is negative, it means this dish is canceled or change amount
	 */
	@Transactional
	private void printCucaigoudan2Kitchen4ChangeAmount(IndentDetail detail, String tempfile, int changedAmount){
		// 把indent下面的dish根据不同的打印输出, 分组
		Map<Printer, IndentDetail_PrintStyle> mapPrintDish = new HashMap<>();
		
		Indent indent = detail.getIndent();
		Dish dish = dishDA.getDishById(detail.getDishId());
		if (dish == null)
			return;
		int totalamount = 0;
		for (IndentDetail d : indent.getItems()){
			totalamount += d.getAmount();
		}
		
		List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
		for (Category2Printer cp : cps) {
			Printer printer = cp.getPrinter();
			IndentDetail_PrintStyle idps = new IndentDetail_PrintStyle();
			idps.indentDetail = detail;
			idps.printStyle = cp.getPrintStyle();
			mapPrintDish.put(printer, idps);
		}
		
		Map<String,String> keyMap = new HashMap<String, String>();
		if (changedAmount > 0)
			keyMap.put("title", "出菜勾单");
		else 
			keyMap.put("title", "出菜勾单-取消单");
		keyMap.put("operator", detail.getOperator());
		keyMap.put("tableNumb", indent.getDeskName());
		keyMap.put("orderId", indent.getDailySequence()+"");
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
		keyMap.put("amountOnThisTable", totalamount + "");
		keyMap.put("indentcomments", indent.getComments() == null ? "" : indent.getComments());
		
//		printCucaigoudan2KitchenWithPrintStyle(mapPrintDish, tempfile, keyMap);
		Iterator<Printer> keys = mapPrintDish.keySet().iterator();
		while(keys.hasNext()){
			Printer p = keys.next();
			
			IndentDetail_PrintStyle idps = mapPrintDish.get(p);
			if (idps.printStyle == ConstantValue.PRINT_STYLE_SEPARATELY){
				Map<String, String> mg = new HashMap<String, String>();
				for (int i = 0; i < Math.abs(changedAmount); i++) {
					List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
					if (changedAmount > 0)
						mg.put("name", idps.indentDetail.getDishFirstLanguageName());
					else 
						mg.put("name", "Canceled " +idps.indentDetail.getDishFirstLanguageName());
					mg.put("amount", "1");
					String requirement = "";
					if (idps.indentDetail.getAdditionalRequirements() != null)
						requirement += idps.indentDetail.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += " " + idps.indentDetail.getWeight();
					mg.put("requirement", requirement);
					
					goods.add(mg);
					
					keyMap.put("amountOnThisTicket", "1");
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("keys", keyMap);
					params.put("goods", goods);
					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
					PrintQueue.add(job);
				}
			} else if (idps.printStyle == ConstantValue.PRINT_STYLE_TOGETHER){
				List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
				Map<String, String> mg = new HashMap<String, String>();
				for (int i = 0; i < Math.abs(changedAmount); i++) {//每个菜品单独打印一行, 重复的打印多行
					if (changedAmount > 0)
						mg.put("name", detail.getDishFirstLanguageName());
					else 
						mg.put("name", "Canceled " + detail.getDishFirstLanguageName());
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
				PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
				PrintQueue.add(job);
			}
			
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
		Map<Printer, List<IndentDetail_PrintStyle>> mapPrintDish = new HashMap<>();

		int totalamount = 0;
		Indent indent = details.get(0).getIndent();
		for (int i = 0; i< indent.getItems().size(); i++){
			totalamount += indent.getItems().get(i).getAmount();
		}
		for (int i = 0; i < details.size(); i++) {
			Dish dish = dishDA.getDishById(details.get(i).getDishId());
			if (dish == null)
				continue;
			List<Category2Printer> cps = dish.getCategory2().getCategory2PrinterList();
			for(Category2Printer cp : cps){
				Printer printer = cp.getPrinter();
				if (mapPrintDish.get(printer) == null){
					mapPrintDish.put(printer, new ArrayList<IndentDetail_PrintStyle>());
				}
				IndentDetail_PrintStyle idps = new IndentDetail_PrintStyle();
				idps.indentDetail = details.get(i);
				idps.printStyle = cp.getPrintStyle();
				mapPrintDish.get(printer).add(idps);
			}
		}
		
		Map<String,String> keyMap = new HashMap<String, String>();
		keyMap.put("title", "出菜勾单");
		keyMap.put("tableNumb", indent.getDeskName());
		keyMap.put("orderId", indent.getDailySequence()+"");
		keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(new Date()));
		keyMap.put("amountOnThisTable", totalamount + "");
		keyMap.put("indentcomments", indent.getComments() == null ? "" : indent.getComments());
		keyMap.put("operator", details.get(0).getOperator());//取第一个detail的操作人即可
		printCucaigoudan2KitchenWithPrintStyle(mapPrintDish, tempfile, keyMap, true);
		
	}

	@Override
	@Transactional
	public ObjectListResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, String orderby, String orderbydesc) {
		long l1 = System.currentTimeMillis();
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
		if (count >= 1000)
			return new ObjectListResult("Record is over 1000, please change the filter", false, null, count);
		List<Indent> indents = indentDA.getIndents(start, limit, starttime, endtime, bStatus, deskname, orderbys, orderbydescs);
		if (indents == null || indents.isEmpty())
			return new ObjectListResult(Result.OK, true, null, 0);
		for (int i = 0; i < indents.size(); i++) {
			Indent indent = indents.get(i);
			Hibernate.initialize(indent);
			Hibernate.initialize(indent.getItems());
		}
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to query indent, params : start = " + start + ", limit = " + limit + ", sStartTime = " + sstarttime + ", sEndTime = " + sendtime + ", status = " + status + ", deskName = " + deskname + ", orderby = " + orderby + ", orderByDesc = " + orderbydesc);
		return new ObjectListResult(Result.OK, true, (ArrayList<Indent>)indents, count);
	}

	@Override
	@Transactional(rollbackFor = DataCheckException.class)
	public OperateIndentResult doPayIndent(int userId, int indentId, double paidPrice, double paidCash, String payWay, String discountTemplate, String memberCard, String memberPassword) throws DataCheckException {
		long l1 = System.currentTimeMillis();
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null)
			return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
		UserData selfUser = userDA.getUserById(userId);
		Date operateDate = new Date();
		String logtype = LogData.LogType.INDENT_PAY.toString();

		indent.setStatus(ConstantValue.INDENT_STATUS_PAID);
		indent.setPaidPrice(Double.parseDouble(doubleFormat.format(paidPrice)));
		indent.setPayWay(payWay);
		indent.setDiscountTemplate(discountTemplate);
		indent.setMemberCard(memberCard);
		//record material consume
		for (int i = 0; indent.getItems() != null && i < indent.getItems().size(); i++) {
			IndentDetail detail = indent.getItems().get(i);
			Dish dish = dishDA.getDishById(detail.getDishId());
			if (dish == null){
				throw new DataCheckException("cannot find dish by id " + detail.getDishId());
			}
			if (dish.getMaterialConsumes()!= null){
				for (int j = 0; j < dish.getMaterialConsumes().size(); j++) {
					DishMaterialConsume dmc = dish.getMaterialConsumes().get(j);
					Material m = dmc.getMaterial();
					m.setLeftAmount(m.getLeftAmount() - dmc.getAmount() * detail.getAmount());
					MaterialRecord mr = new MaterialRecord();
					mr.setMaterial(m);
					mr.setAmount(detail.getAmount() * dmc.getAmount() * (-1));
					mr.setLeftAmount(m.getLeftAmount());
					mr.setType(ConstantValue.MATERIALRECORD_TYPE_SELLDISH);
					mr.setDate(operateDate);
					mr.setIndentDetailId(detail.getId());
					materialRecordDA.save(mr);
				}
			}
		}
		indent.setEndTime(operateDate);
		indentDA.update(indent);
		
		//clear merge table record if exists
		Desk maindesk = deskDA.getDeskByName(indent.getDeskName());
		if (maindesk == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("cannot find desk by name : " + indent.getDeskName());
		} else {
			List<Desk> alldesks = deskDA.queryDesks();
			for(Desk d : alldesks){
				if (d.getMergeTo() != null && d.getMergeTo().getId() == maindesk.getId()){
					d.setMergeTo(null);
				}
			}
		}
		//record member consumption, if there are wrong, throw exception for rollback
		if (ConstantValue.INDENT_PAYWAY_MEMBER.equals(payWay)){
			ObjectResult result = null;
			if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
				result = memberService.recordMemberConsumption(memberCard, memberPassword, paidPrice);
			} else {
				result = memberCloudService.recordMemberConsumption(memberCard, memberPassword, paidPrice);
			}
			if (!result.success)
				throw new DataCheckException(result.result);
		}
//		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
//		printTicket2Counter(indent, tempfilePath + "/payorder_template.json", "结账单", paidCash);
		
		// write log.
		
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate indent, id =" + indentId + ", operationType = " + logtype + ".");
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to pay indent");
		return new OperateIndentResult(Result.OK, true);
	}
	
	@Override
	@Transactional
	public OperateIndentResult doCancelIndent(int userId, int indentId) {
		long l1 = System.currentTimeMillis();
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null)
			return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
		UserData selfUser = userDA.getUserById(userId);
		Date operateDate = new Date();
		String logtype = LogData.LogType.INDENT_CANCEL.toString();
			
		indent.setStatus(ConstantValue.INDENT_STATUS_CANCELED);
		indent.setEndTime(operateDate);
		indentDA.update(indent);
		
		//clear merge table record if exists
		Desk maindesk = deskDA.getDeskByName(indent.getDeskName());
		if (maindesk == null){
			logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
			logger.error("cannot find desk by name : " + indent.getDeskName());
		} else {
			List<Desk> alldesks = deskDA.queryDesks();
			for(Desk d : alldesks){
				if (d.getMergeTo() != null && d.getMergeTo().getId() == maindesk.getId()){
					d.setMergeTo(null);
				}
			}
		}

		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json", false);
		
		// write log.
		
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate indent, id =" + indentId + ", operationType = " + logtype + ".");
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to cancel indent");
		return new OperateIndentResult(Result.OK, true);
	}
	
	@Override
	@Transactional
	public OperateIndentResult doRefundIndent(int userId, int indentId) {
		long l1 = System.currentTimeMillis();
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null)
			return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
		if (indent.getStatus() != ConstantValue.INDENT_STATUS_PAID)
			return new OperateIndentResult("The order's current status is not PAID", false);
		UserData selfUser = userDA.getUserById(userId);
		Date operateDate = new Date();
		String logtype = LogData.LogType.INDENT_REFUND.toString();
			
		indent.setStatus(ConstantValue.INDENT_STATUS_REFUND);
		indent.setEndTime(operateDate);
		indentDA.update(indent);
		

		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json", false);
		
		// write log.
		
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate indent, id =" + indentId + ", operationType = " + logtype + ".");
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to refund indent");
		return new OperateIndentResult(Result.OK, true);
	}
	
	@Override
	@Transactional(rollbackFor=DataCheckException.class)
	/**
	 * 
	 * @param indentId
	 * @param dishId
	 * @param amount this value is just for the number after modify; for example, the original amount is 5, the input value is 2,
	 * 					after operation, the final amount is 2(it means there are 3 removed); 
	 * @param operateType
	 * @return
	 */
	public OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType) throws DataCheckException{
		long l1 = System.currentTimeMillis();
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
				if (dish == null){
					throw new DataCheckException("cannot find dish by id " + detail.getDishId());
				}
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
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_REFUND){
			logtype = LogData.LogType.INDENTDETAIL_REFUND.toString();
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
			double newPaidPrice = detail.getIndent().getPaidPrice();
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				newPrice -= detail.getAmount() * detail.getWeight() * detail.getDishPrice();
				newPaidPrice -= detail.getAmount() * detail.getWeight() * detail.getDishPrice();
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				newPrice -= detail.getAmount() * detail.getDishPrice();
				newPaidPrice -= detail.getAmount() * detail.getDishPrice();
			}
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(newPrice)));
			indent.setPaidPrice(Double.parseDouble(new DecimalFormat("0.00").format(newPaidPrice)));
			indentDA.update(detail.getIndent());
			printCucaigoudan2Kitchen4ChangeAmount(detail, tempfilePath + "/cucaigoudan.json", detail.getAmount() * (-1));
		} 
		OperateIndentResult result = new OperateIndentResult("ok", true);
		if (indent != null){
			result.data = new OperateIndentResult.Indent();
			result.data.id = indent.getId();
			result.data.dailySequence = indent.getDailySequence();
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
		
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to operate indent detail. operate type = " + operateType);
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
		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单", 0);
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
		long l1 = System.currentTimeMillis();
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
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to clear desk");
		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional(rollbackFor=DataCheckException.class)
	public MakeOrderResult addDishToIndent(int deskId, JSONArray jsonOrder) throws DataCheckException {
		long l1 = System.currentTimeMillis();
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
		Date now = new Date();
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				throw new DataCheckException("cannot find dish by id "+ dishid);
			if (dish.isSoldOut()){
				throw new DataCheckException("dish "+ dish.getFirstLanguageName() + " is Sold Out, cannot make order");
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setTime(now);
			detail.setAmount(o.getInt("amount"));
			detail.setDishFirstLanguageName(dish.getFirstLanguageName());
			detail.setDishSecondLanguageName(dish.getSecondLanguageName());
			detail.setDishPrice(o.getDouble("dishPrice"));
			if (o.has("operator"))
				detail.setOperator(o.getString("operator"));
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("additionalRequirements"))
				detail.setAdditionalRequirements(o.getString("additionalRequirements"));
			double totalprice = 0;
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
				totalprice = indent.getTotalPrice() + detail.getAmount() * detail.getDishPrice() ;
			} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
				totalprice = indent.getTotalPrice() + detail.getAmount() * detail.getDishPrice()  * detail.getWeight();
			}
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
//			indent.addItem(detail);//这里不能把detail加入indent集合, 在最终提交前, 如果调用indent.getItems, 会有两个相同的detail出现在集合中; 
									//如果此处不添加, 不影响对数据库的存储. 具体原理不清楚.
			indentDetailDA.save(detail);
			listPrintDetails.add(detail);
		}
		indentDA.update(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
//		logger.debug("addDishToIndent : " + ConstantValue.DFYMDHMS.format(new Date()) + "\njsonOrder="+jsonOrder.toString()+"\nlistPrintDetails="+listPrintDetails);
		printCucaigoudan2Kitchen(listPrintDetails, tempfilePath + "/cucaigoudan.json");
//		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		listPrintDetails.clear();//release this beans from collection to avoid hibernate exception
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to add dish into indent");
		return new MakeOrderResult(Result.OK, true, indent.getDailySequence());
	}

	@Override
	@Transactional
	public ObjectResult changeDesks(int userId, int srcDeskId, int destDeskId) {
		long l1 = System.currentTimeMillis();
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
		ArrayList<Desk> changedDesks = new ArrayList<Desk>();
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
		deskinfos.add(deskinfo);
		for(Desk desk : changedDesks){
			deskinfo = new GetDeskWithIndentResult.DeskWithIndent();
			deskinfo.id = desk.getId();
			deskinfo.name = desk.getName();
			if (desk.getMergeTo() != null)
				deskinfo.mergeTo = desk.getMergeTo().getName();
			deskinfos.add(deskinfo);
		}		
		
		//print a ticket
		String datatime = ConstantValue.DFYMDHMS.format(new Date());
		List<Printer> printerList = printerDA.queryPrinters();
		Map<String, String> keyMap = new HashMap<String, String>();
		keyMap.put("from", srcDesk.getName());
		keyMap.put("to", destDesk.getName());
		keyMap.put("dateTime", datatime);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keyMap);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		for (int i = 0; i < printerList.size(); i++) {
			PrintJob job = new PrintJob(tempfilePath + "/changetable.json", params, printerList.get(i).getPrinterName());
			PrintQueue.add(job);
		}
		
		long l2 = System.currentTimeMillis();
		logger.debug((l2 - l1) + "ms to change desk for indent");
		return new ObjectResult(Result.OK, true, deskinfos);
	}

	class IndentDetail_PrintStyle{
		IndentDetail indentDetail;
		int printStyle;
	}
	
	private Comparator<IndentDetail_PrintStyle> comparator = new Comparator<IndentDetail_PrintStyle>(){

		@Override
		public int compare(IndentDetail_PrintStyle o1, IndentDetail_PrintStyle o2) {
			Dish dish1 = dishDA.getDishById(o1.indentDetail.getDishId());
			Dish dish2 = dishDA.getDishById(o2.indentDetail.getDishId());
			if (dish1.getCategory2().getCategory1().getId() == dish2.getCategory2().getCategory1().getId()){
				if (dish1.getCategory2().getId() == dish2.getCategory2().getId()){
					return dish1.getSequence() - dish2.getSequence();
				} else {
					return dish1.getCategory2().getSequence() - dish2.getCategory2().getSequence();
				}
			} else {
				return dish1.getCategory2().getCategory1().getSequence() - dish2.getCategory2().getCategory1().getSequence();
			}	
		}};
}
