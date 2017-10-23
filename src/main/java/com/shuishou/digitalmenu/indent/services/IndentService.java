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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.controllers.AccountController;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.common.models.ConfirmCode;
import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.common.models.IConfirmCodeDataAccessor;
import com.shuishou.digitalmenu.common.models.IDeskDataAccessor;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.IIndentDetailDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.indent.views.GetIndentDetailResult;
import com.shuishou.digitalmenu.indent.views.GetIndentResult;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.printertool.PrintJob;
import com.shuishou.digitalmenu.printertool.PrintQueue;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class IndentService implements IIndentService {
	
	private final static Logger logger = LoggerFactory.getLogger(IndentService.class);
	
	@Autowired
	private IConfirmCodeDataAccessor confirmCodeDA;
	@Autowired
	private ILogService logService;
	
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
	
	@Override
	@Transactional
	public synchronized MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid, int customerAmount) {
		ConfirmCode cc = confirmCodeDA.getCode();
		if (!confirmCode.equals(cc.getCode()))
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
				return new MakeOrderResult("dish "+ dish.getEnglishName() + " is Sold Out, cannot make order", false, -1);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishChineseName(dish.getChineseName());
			detail.setDishEnglishName(dish.getEnglishName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("addtionalRequirements"))
				detail.setAdditionalRequirements(o.getString("addtionalRequirements"));
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
		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		return new MakeOrderResult(Result.OK, true, sequence);
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
			int copy = p.getCopy();
			for(int i = 0; i< copy; i++){
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
					mg.put("name", d.getDishChineseName());
					mg.put("price", d.getDishPrice()+"");
					mg.put("amount", d.getAmount()+"");
					
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += "\n" + d.getWeight();
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT){
						mg.put("totalPrice", (d.getWeight() * d.getDishPrice() * d.getAmount()) + "");
					} else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT){
						mg.put("totalPrice", (d.getDishPrice() * d.getAmount()) + "");
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
		
		for(IndentDetail detail : indent.getItems()){
			totalamount += detail.getAmount();
			Dish dish = dishDA.getDishById(detail.getDishId());
			Printer printer = dish.getCategory2().getPrinter();
			if (mapPrintDish.get(printer) == null){
				mapPrintDish.put(printer, new ArrayList<IndentDetail>());
			}
			mapPrintDish.get(printer).add(detail);
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
			int amount = 0;
			List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
			for(IndentDetail d : mapPrintDish.get(p)){
				Map<String, String> mg = new HashMap<String, String>();
				Dish dish = dishDA.getDishById(d.getDishId());
				for (int i = 0; i < d.getAmount(); i++) {// 每个菜品单独打印一行,重复的打印多行
					mg.put("name", d.getDishChineseName());
					mg.put("amount", "1");
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += "\n" + d.getWeight();
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
		Printer printer = dish.getCategory2().getPrinter();
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
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
		Map<String, String> mg = new HashMap<String, String>();
		for (int i = 0; i < Math.abs(changedAmount); i++) {//每个菜品单独打印一行, 重复的打印多行
			mg.put("name", detail.getDishChineseName());
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
		PrintJob job = new PrintJob(tempfile, params, printer.getPrinterName());
		PrintQueue.add(job);
		
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
		for (IndentDetail detail : indent.getItems()){
			totalamount += detail.getAmount();
		}
		for (IndentDetail detail : details) {
			Dish dish = dishDA.getDishById(detail.getDishId());
			Printer printer = dish.getCategory2().getPrinter();
			if (mapPrintDish.get(printer) == null) {
				mapPrintDish.put(printer, new ArrayList<IndentDetail>());
			}
			mapPrintDish.get(printer).add(detail);
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
			int amount = 0;
			List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
			for (IndentDetail d : mapPrintDish.get(p)) {
				Map<String, String> mg = new HashMap<String, String>();
				Dish dish = dishDA.getDishById(d.getDishId());
				for (int i = 0; i < d.getAmount(); i++) {// 每个菜品单独打印一行,重复的打印多行
					mg.put("name", d.getDishChineseName());
					mg.put("amount", "1");
					String requirement = "";
					if (d.getAdditionalRequirements() != null)
						requirement += d.getAdditionalRequirements();
					//按重量卖的dish, 把重量加入requirement
					if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
						requirement += "\n" + d.getWeight();
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

	@Transactional
	private void print(Indent indent){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		for(Printer p : printers){
			int copy = p.getCopy();
			for(int i = 0; i< copy; i++){
				String tempfile = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE + "/";
				if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_TOGETHER){
					tempfile += "print_together.json";
					Map<String,String> keys = new HashMap<String, String>();
					keys.put("restaurantname", "restaurantname");
					keys.put("desk", indent.getDeskName());
					keys.put("sequence", indent.getDailySequence()+"");
					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
					keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
					List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
					for(IndentDetail d : indent.getItems()){
						Map<String, String> mg = new HashMap<String, String>();
						mg.put("name", d.getDishChineseName());
						mg.put("price", d.getDishPrice()+"");
						mg.put("amount", d.getAmount()+"");
						mg.put("totalPrice", (d.getDishPrice() * d.getAmount()) + "");
						mg.put("requirement", d.getAdditionalRequirements());
						goods.add(mg);
						
					}
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("keys", keys);
					params.put("goods", goods);
					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
					PrintQueue.add(job);
				}else if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_SEPARATELY){
					tempfile += "print_separately.json";
					for(IndentDetail d : indent.getItems()){
						Map<String,String> keys = new HashMap<String, String>();
						keys.put("restaurantname", "restaurantname");
						keys.put("desk", indent.getDeskName());
						keys.put("sequence", indent.getDailySequence()+"");
						keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
						keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
						keys.put("dishname", d.getDishChineseName());
						keys.put("amount", d.getAmount()+"");
						keys.put("requirement", d.getAdditionalRequirements());
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("keys", keys);
						PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
						PrintQueue.add(job);
					}
				}
				
			}
		}
	}
	
	@Transactional
	private void print(IndentDetail detail){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		Indent indent = detail.getIndent();
		
		for(Printer p : printers){
			int copy = p.getCopy();
			for(int i = 0; i< copy; i++){
				String tempfile = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE + "/";
				if (p.getPrintStyle() == ConstantValue.PRINT_STYLE_SEPARATELY){
					tempfile += "print_separately.json";
					Map<String,String> keys = new HashMap<String, String>();
					keys.put("restaurantname", "restaurantname");
					keys.put("desk", indent.getDeskName());
					keys.put("sequence", indent.getDailySequence()+"");
					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
					keys.put("totalPrice", String.format("%.2f", indent.getTotalPrice()));
					keys.put("dishname", detail.getDishChineseName());
					keys.put("amount", detail.getAmount()+"");
					keys.put("requirement", detail.getAdditionalRequirements());
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("keys", keys);
					PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
					PrintQueue.add(job);
				}
			}
		}
	}

	@Override
	@Transactional
	public GetIndentResult queryIndent(int start, int limit, String sstarttime, String sendtime, String status, String deskname, String orderby) {
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
		List<Indent> indents = indentDA.getIndents(start, limit, starttime, endtime, bStatus, deskname, orderbys);
		if (indents == null || indents.isEmpty())
			return new GetIndentResult(Result.OK, true, null, 0);
//		ArrayList<GetIndentResult.Indent> resultinfos = new ArrayList<GetIndentResult.Indent>(indents.size());
//		
//		for (int i = 0; i < indents.size(); i++) {
//			Indent indenti = indents.get(i);
//			GetIndentResult.Indent resultindent = new GetIndentResult.Indent();
//			resultindent.id = indenti.getId();
//			resultindent.dailySequence = indenti.getDailySequence();
//			resultindent.deskName = indenti.getDeskName();
//			resultindent.status = indenti.getStatus();
//			resultindent.startTime = ConstantValue.DFYMDHMS.format(indenti.getStartTime());
//			if (indenti.getEndTime() != null)
//				resultindent.endTime = ConstantValue.DFYMDHMS.format(indenti.getEndTime());
//			resultindent.paidPrice = indenti.getPaidPrice();
//			resultindent.totalPrice = indenti.getTotalPrice();
//			resultindent.payWay = indenti.getPayWay();
//			resultindent.customerAmount = indenti.getCustomerAmount();
//			resultinfos.add(resultindent);
//			for (int j = 0; j < indenti.getItems().size(); j++) {
//				GetIndentResult.IndentDetail det = new GetIndentResult.IndentDetail();
//				det.id = indenti.getItems().get(j).getId();
//				det.additionalRequirements = indenti.getItems().get(j).getAdditionalRequirements();
//				det.amount = indenti.getItems().get(j).getAmount();
//				det.dishChineseName = indenti.getItems().get(j).getDishChineseName();
//				det.dishEnglishName = indenti.getItems().get(j).getDishEnglishName();
//				det.dishId = indenti.getItems().get(j).getDishId();
//				det.dishPrice = indenti.getItems().get(j).getDishPrice();
//				resultindent.items.add(det);
//			}
//		}
		int count = indentDA.getIndentCount(starttime, endtime, bStatus, deskname);
		return new GetIndentResult(Result.OK, true, (ArrayList<Indent>)indents, count);
	}

	@Override
	@Transactional
	public OperateIndentResult operateIndent(int userId, int indentId, byte operationType, double paidPrice, byte payWay, String memberCard) {
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
			indent.setPaidPrice(paidPrice);
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
			printTicket2Counter(indent, tempfilePath + "/payorder_template.json", "客用单");
		}
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate indent, id =" + indentId + ", operationType = " + operationType + ".");
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
//		if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_ADD){
//			logtype = LogData.LogType.INDENTDETAIL_ADDDISH.toString();
//			Dish dish = dishDA.getDishById(dishId);
//			if (dish == null)
//				return new OperateIndentResult("cannot find Dish by Id:" + dishId, false);
//			indent = indentDA.getIndentById(indentId);
//			if (indent == null)
//				return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
//			IndentDetail detail = new IndentDetail();
//			detail.setAmount(amount);
//			detail.setDishId(dishId);
//			detail.setDishChineseName(dish.getChineseName());
//			detail.setDishEnglishName(dish.getEnglishName());
//			detail.setDishPrice(dish.getPrice());
//			detail.setIndent(indent);
//			indent.addItem(detail);
//			indent.setTotalPrice(indent.getTotalPrice() + amount * dish.getPrice());
//			indentDA.update(indent);
//			ArrayList<IndentDetail> listPrintDetails = new ArrayList<>();
//			printCucaigoudan2Kitchen(indent, tempfilePath + "/cucaigoudan.json");
//			printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
//		} else 
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
//			ArrayList<IndentDetail> listPrintDetails = new ArrayList<>();
//			listPrintDetails.add(detail);
			printCucaigoudan2Kitchen4ChangeAmount(detail, tempfilePath + "/cucaigoudan.json", amount - originalAmount);
			printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
//			listPrintDetails.clear();//must remove from the collection firstly, otherwise hibernate will rebuild the detail object by cascade
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE){
			logtype = LogData.LogType.INDENTDETAIL_DELETE.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			indent = detail.getIndent();
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			indent.getItems().remove(detail);//must remove from the collection firstly, otherwise hibernate will rebuild the detail object by cascade
			indentDetailDA.delete(detail);
			double totalprice = detail.getIndent().getTotalPrice() - detail.getAmount() * detail.getDishPrice();
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
			indentDA.update(detail.getIndent());
//			ArrayList<IndentDetail> listPrintDetails = new ArrayList<>();
//			listPrintDetails.add(detail);
			printCucaigoudan2Kitchen4ChangeAmount(detail, tempfilePath + "/cucaigoudan.json", detail.getAmount() * (-1));
			printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
//			listPrintDetails.clear();//must remove from the collection firstly, otherwise hibernate will rebuild the detail object by cascade
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
				dinfo.dishChineseName = d.getDishChineseName();
				dinfo.dishEnglishName = d.getDishEnglishName();
				dinfo.dishId = d.getDishId();
				dinfo.dishPrice = d.getDishPrice();
				dinfo.id = d.getId();
				result.data.items.add(dinfo);
			}
		}
		
		// write log.
		if (userId != 0){
			UserData selfUser = userDA.getUserById(userId);
			logService.write(selfUser, logtype,
				"User " + selfUser + " operate IndentDetail, indentId = " + indentId 
				+ ", dishId = " + dishId + ", indentdetailId = " + indentDetailId 
				+ ", amount = " + amount + ", operationType = " + operateType + ".");
		}
		
		return result;
		
	}

	@Override
	@Transactional
	public GetIndentDetailResult queryIndentDetail(int indentId) {
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null){
			return new GetIndentDetailResult(Result.FAIL, false, null);
		}
		List<IndentDetail> details = indent.getItems();
		if (details == null || details.isEmpty())
			return new GetIndentDetailResult(Result.OK, true, null);
		List<GetIndentDetailResult.IndentDetail> resultinfos = new ArrayList<GetIndentDetailResult.IndentDetail>(details.size());
		for (int i = 0; i < details.size(); i++) {
			GetIndentDetailResult.IndentDetail resultdetail = new GetIndentDetailResult.IndentDetail();
			resultdetail.id = details.get(i).getId();
			resultdetail.amount = details.get(i).getAmount();
			resultdetail.dishChineseName = details.get(i).getDishChineseName();
			resultdetail.dishEnglishName = details.get(i).getDishEnglishName();
			resultdetail.dishPrice = details.get(i).getDishPrice();
			resultdetail.additionalRequirements = details.get(i).getAdditionalRequirements();
			resultinfos.add(resultdetail);
		}
		return new GetIndentDetailResult(Result.OK, true, resultinfos);
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

	@Override
	@Transactional
	public ObjectResult printIndentDetail(int userId, int indentDetailId) {
		IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
		if (detail == null){
			return new ObjectResult("No order detail for ID : " + indentDetailId, false);
		}
		print(detail);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENTDETAIL_PRINTISH.toString(),
				"User " + selfUser + " print IndentDetail, indentId = " + detail.getIndent().getId() + ", dishId = " + detail.getDishId() + ".");
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
		List<Indent> indents = indentDA.getIndents(0, 100, null, null, new Byte[]{ConstantValue.INDENT_STATUS_OPEN}, desk.getName(), null);
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
				return new MakeOrderResult("dish "+ dish.getEnglishName() + " is Sold Out, cannot make order", false, -1);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishChineseName(dish.getChineseName());
			detail.setDishEnglishName(dish.getEnglishName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("weight"))
				detail.setWeight(Double.parseDouble(o.getString("weight")));
			if (o.has("addtionalRequirements"))
				detail.setAdditionalRequirements(o.getString("addtionalRequirements"));
			double totalprice = 0;
			if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_UNIT)
				totalprice = indent.getTotalPrice() + detail.getAmount() * dish.getPrice();
			else if (dish.getPurchaseType() == ConstantValue.DISH_PURCHASETYPE_WEIGHT)
				totalprice = indent.getTotalPrice() + detail.getAmount() * dish.getPrice() * detail.getWeight();
			indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
			indent.addItem(detail);
			indentDetailDA.save(detail);
			listPrintDetails.add(detail);
		}
		indentDA.update(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printCucaigoudan2Kitchen(listPrintDetails, tempfilePath + "/cucaigoudan.json");
		printTicket2Counter(indent, tempfilePath + "/newIndent_template.json", "对账单");
		listPrintDetails.clear();//release this beans from collection to avoid hibernate exception
		return new MakeOrderResult(Result.OK, true, indent.getDailySequence());
	}

	@Override
	@Transactional
	public ObjectResult changeDesks(int userId, int deskId1, int deskId2) {
		Desk desk1 = deskDA.getDeskById(deskId1);
		if (desk1 == null){
			return new MakeOrderResult("cannot find desk by id " + deskId1, false, -1);
		}
		Desk desk2 = deskDA.getDeskById(deskId2);
		if (desk2 == null){
			return new MakeOrderResult("cannot find desk by id " + deskId2, false, -1);
		}
		List<Indent> indents1 = indentDA.getUnpaidIndent(desk1.getName());
		List<Indent> indents2 = indentDA.getUnpaidIndent(desk2.getName());
		boolean desk1Empty = false;
		boolean desk2Empty = false;
		if (indents1 == null || indents1.isEmpty()){
			desk1Empty = true;
		}
		if (indents2 == null || indents2.isEmpty()){
			desk2Empty = true;
		}
		if (desk1Empty && desk2Empty){
			return new ObjectResult(Result.OK, true);
		}
		if (!desk1Empty && !desk2Empty){
			return new ObjectResult("both tables are occupied", false);
		}
		Indent indent = null;
		if (indents1 != null && indents1.size() > 0){
			indent = indents1.get(0);
			indent.setDeskName(desk2.getName());
		} else {
			indent = indents2.get(0);
			indent.setDeskName(desk1.getName());
		}
		indentDA.save(indent);
		return new ObjectResult(Result.OK, true);
	}
	
}
