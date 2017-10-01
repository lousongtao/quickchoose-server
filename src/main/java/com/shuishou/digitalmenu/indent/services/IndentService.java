package com.shuishou.digitalmenu.indent.services;

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
import com.shuishou.digitalmenu.views.GridResult;
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
		//if this table is already merged to other table, which means this is a sub-desk, then do ADDDISH operation to main-desk
		if (desk.getMergeTo() != null){
			//TODO:
		}
		double totalprice = 0;
		Indent indent = new Indent();
		indent.setDeskName(desk.getName());
		indent.setStartTime(Calendar.getInstance().getTime());
		indent.setCustomerAmount(customerAmount);
		int sequence = indentDA.getMaxSequenceToday() + 1;
		indent.setDailySequence(sequence);
		
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
			if (o.has("addtionalRequirements"))
				detail.setAdditionalRequirements(o.getString("addtionalRequirements"));
			totalprice += detail.getAmount() * dish.getPrice();
			indent.addItem(detail);
//			indentDetailDA.save(detail);
		}
		indent.setTotalPrice(Double.parseDouble(new DecimalFormat("0.00").format(totalprice)));
		indentDA.save(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printNewIndent2Kitchen(indent, tempfilePath + "/cucaigoudan.json");
		printNewIndent2Counter(indent, tempfilePath + "/newIndentCounter.json", "对账单");
		return new MakeOrderResult(Result.OK, true, sequence);
	}
	
	//新订单在总台打印, 完整的部分
	@Transactional
	private void printNewIndent2Counter(Indent indent, String tempfile, String printType){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		for(Printer p : printers){
			if (!"counter".equals(p.getName()))
				continue;
			int copy = p.getCopy();
			for(int i = 0; i< copy; i++){
				Map<String,String> keys = new HashMap<String, String>();
				keys.put("restaurant", "HAO SZECHUAN 好吃嘴 北桥总店");
				keys.put("abn", "86163933686");
				keys.put("address", "74 FRANCIS ST, NORTHBIRDHE, 6003");
				keys.put("telephone", "(08)92280636");
				keys.put("sequence", indent.getDailySequence()+"");
				keys.put("customerAmount", indent.getCustomerAmount()+"");
				keys.put("tableNo", indent.getDeskName());
				keys.put("printType", printType);
				keys.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
				keys.put("totalPrice", String.valueOf(indent.getTotalPrice()));
				keys.put("paidPrice", String.format("%.2f", indent.getPaidPrice()));//TODO: 对账单不需要这个
				keys.put("gst", String.format("%.2f",(double)(indent.getPaidPrice()/11)));//TODO: 对账单不需要这个
				keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));
				keys.put("slogan", "澳大利亚好吃嘴 欢迎您的光临");
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
			}
		}
	}
	
	//创建订单时, 根据订单内的不同内容, 找到对应的打印机, 发送出去
	@Transactional
	private void printNewIndent2Kitchen(Indent indent, String tempfile){
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
			keyMap.put("title", "出菜勾单");
			keyMap.put("tableNumb", indent.getDeskName());
			keyMap.put("orderId", indent.getDailySequence()+"");
			keyMap.put("dateTime", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
			keyMap.put("totalAmountMsg", "本桌台共有 " + totalamount + " 份菜品");
			int amountMsg = 0;
			List<Map<String, String>> goods = new ArrayList<Map<String, String>>();
			for(IndentDetail d : mapPrintDish.get(p)){
				Map<String, String> mg = new HashMap<String, String>();
				for (int i = 0; i < d.getAmount(); i++) {//每个菜品单独打印一行, 重复的打印多行
					mg.put("name", d.getDishChineseName());
					mg.put("amount", "1");
					mg.put("requirement", d.getAdditionalRequirements());
					amountMsg++;
					goods.add(mg);
				}
			}
			keyMap.put("amountMsg", "此单据包括 " + amountMsg + " 个菜品");
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
				if (p.getPrintStyle() == ConstantValue.PRINT_TYPE_TOGETHER){
					tempfile += "print_together.json";
					Map<String,String> keys = new HashMap<String, String>();
					keys.put("restaurantname", "restaurantname");
					keys.put("desk", indent.getDeskName());
					keys.put("sequence", indent.getDailySequence()+"");
					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
					keys.put("totalPrice", String.valueOf(indent.getTotalPrice()));
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
				}else if (p.getPrintStyle() == ConstantValue.PRINT_TYPE_SEPARATELY){
					tempfile += "print_separately.json";
					for(IndentDetail d : indent.getItems()){
						Map<String,String> keys = new HashMap<String, String>();
						keys.put("restaurantname", "restaurantname");
						keys.put("desk", indent.getDeskName());
						keys.put("sequence", indent.getDailySequence()+"");
						keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
						keys.put("totalPrice", String.valueOf(indent.getTotalPrice()));
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
				if (p.getPrintStyle() == ConstantValue.PRINT_TYPE_SEPARATELY){
					tempfile += "print_separately.json";
					Map<String,String> keys = new HashMap<String, String>();
					keys.put("restaurantname", "restaurantname");
					keys.put("desk", indent.getDeskName());
					keys.put("sequence", indent.getDailySequence()+"");
					keys.put("time", ConstantValue.DFYMDHMS.format(indent.getStartTime()));
					keys.put("totalPrice", String.valueOf(indent.getTotalPrice()));
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
		ArrayList<GetIndentResult.Indent> resultinfos = new ArrayList<GetIndentResult.Indent>(indents.size());
		
		for (int i = 0; i < indents.size(); i++) {
			Indent indenti = indents.get(i);
			GetIndentResult.Indent resultindent = new GetIndentResult.Indent();
			resultindent.id = indenti.getId();
			resultindent.dailySequence = indenti.getDailySequence();
			resultindent.deskName = indenti.getDeskName();
			resultindent.status = indenti.getStatus();
			resultindent.startTime = ConstantValue.DFYMDHMS.format(indenti.getStartTime());
			if (indenti.getEndTime() != null)
				resultindent.endTime = ConstantValue.DFYMDHMS.format(indenti.getEndTime());
			resultindent.paidPrice = indenti.getPaidPrice();
			resultindent.totalPrice = indenti.getTotalPrice();
			resultindent.payWay = indenti.getPayWay();
			resultindent.customerAmount = indenti.getCustomerAmount();
			resultinfos.add(resultindent);
			for (int j = 0; j < indenti.getItems().size(); j++) {
				GetIndentResult.IndentDetail det = new GetIndentResult.IndentDetail();
				det.id = indenti.getItems().get(j).getId();
				det.additionalRequirements = indenti.getItems().get(j).getAdditionalRequirements();
				det.amount = indenti.getItems().get(j).getAmount();
				det.dishChineseName = indenti.getItems().get(j).getDishChineseName();
				det.dishEnglishName = indenti.getItems().get(j).getDishEnglishName();
				det.dishId = indenti.getItems().get(j).getDishId();
				det.dishPrice = indenti.getItems().get(j).getDishPrice();
				resultindent.items.add(det);
			}
		}
		int count = indentDA.getIndentCount(starttime, endtime, bStatus, deskname);
		return new GetIndentResult(Result.OK, true, resultinfos, count);
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
			printNewIndent2Counter(indent, tempfilePath + "/payorder_template.json", "结账单");
			printNewIndent2Counter(indent, tempfilePath + "/payorder_template.json", "客用单");
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
	 * @param amount negative value is MINUS amount, positive value is ADD amount 
	 * @param operateType
	 * @return
	 */
	public OperateIndentResult operateIndentDetail(int userId, int indentId, int dishId, int indentDetailId, int amount, byte operateType) {
		String logtype = null;
		Indent indent = null;
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_ADD){
			logtype = LogData.LogType.INDENTDETAIL_ADDDISH.toString();
			Dish dish = dishDA.getDishById(dishId);
			if (dish == null)
				return new OperateIndentResult("cannot find Dish by Id:" + dishId, false);
			indent = indentDA.getIndentById(indentId);
			if (indent == null)
				return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
			IndentDetail detail = new IndentDetail();
			detail.setAmount(amount);
			detail.setDishId(dishId);
			detail.setDishChineseName(dish.getChineseName());
			detail.setDishEnglishName(dish.getEnglishName());
			detail.setDishPrice(dish.getPrice());
			detail.setIndent(indent);
			indent.addItem(detail);
			indent.setTotalPrice(indent.getTotalPrice() + amount * dish.getPrice());
			indentDA.update(indent);
			printNewIndent2Kitchen(indent, tempfilePath + "/cucaigoudan.json");
			printNewIndent2Counter(indent, tempfilePath + "/newIndentCounter.json", "对账单");
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT){
			logtype = LogData.LogType.INDENTDETAIL_CHANGEAMOUNT.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			detail.setAmount(amount);
			indentDetailDA.update(detail);
			indent = detail.getIndent();
			double totalprice = 0.0d;
			for(IndentDetail d : indent.getItems()){
				totalprice += d.getAmount() * d.getDishPrice();
			}
			indent.setTotalPrice(totalprice);
			indentDA.update(indent);
			printNewIndent2Kitchen(indent, tempfilePath + "/cucaigoudan.json");
			printNewIndent2Counter(indent, tempfilePath + "/newIndentCounter.json", "对账单");
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE){
			logtype = LogData.LogType.INDENTDETAIL_DELETE.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			indent = detail.getIndent();
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			indent.getItems().remove(detail);//must remove from the collection firstly, otherwise hibernate will rebuild the detail object by cascade
			indentDetailDA.delete(detail);
			detail.getIndent().setTotalPrice(detail.getIndent().getTotalPrice() - detail.getAmount() * detail.getDishPrice());
			indentDA.update(detail.getIndent());
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
	public GridResult printIndent(int userId, int indentId) {
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null){
			return new GridResult("No order for ID : " + indentId, false);
		}
//		print(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printNewIndent2Counter(indent, tempfilePath + "/newIndentCounter.json", "对账单");
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENT_PRINT.toString(),
				"User " + selfUser + " operate IndentDetail, indentId = " + indentId + ".");
		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GridResult printIndentDetail(int userId, int indentDetailId) {
		IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
		if (detail == null){
			return new GridResult("No order detail for ID : " + indentDetailId, false);
		}
		print(detail);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.INDENTDETAIL_PRINTISH.toString(),
				"User " + selfUser + " print IndentDetail, indentId = " + detail.getIndent().getId() + ", dishId = " + detail.getDishId() + ".");
		return new GridResult(Result.OK, true);
	}

	//清除无法使用的桌台数据, 比如已经并桌无法开桌的, 无法结账的
	@Override
	@Transactional
	public GridResult clearDesk(int userId, int deskId) {
		Desk desk = deskDA.getDeskById(deskId);
		if (desk == null){
			return new GridResult("cannot find desk by id " + deskId, false);
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
		
		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public OperateIndentResult addDishToIndent(int deskId, JSONArray jsonOrder) {
		Desk desk = deskDA.getDeskById(deskId);
		if (desk == null){
			return new OperateIndentResult("cannot find desk by id " + deskId, false);
		}
		List<Indent> indents = indentDA.getIndents(0, 100, null, null, new Byte[]{ConstantValue.INDENT_STATUS_OPEN}, desk.getName(), null);
		if (indents == null || indents.isEmpty()){
			return new OperateIndentResult("cannot find order on desk " + desk.getName(), false);
		}
		Indent indent = indents.get(0);
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				return new OperateIndentResult("cannot find dish by id "+ dishid, false);
			if (dish.isSoldOut()){
				return new OperateIndentResult("dish "+ dish.getEnglishName() + " is Sold Out, cannot make order", false);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishChineseName(dish.getChineseName());
			detail.setDishEnglishName(dish.getEnglishName());
			detail.setDishPrice(dish.getPrice());
			if (o.has("addtionalRequirements"))
				detail.setAdditionalRequirements(o.getString("addtionalRequirements"));
			indent.setTotalPrice(indent.getTotalPrice() + detail.getAmount() * dish.getPrice());
			indent.addItem(detail);
			indentDetailDA.save(detail);
		}
		indentDA.update(indent);
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printNewIndent2Kitchen(indent, tempfilePath + "/cucaigoudan.json");
		printNewIndent2Counter(indent, tempfilePath + "/newIndentCounter.json", "对账单");
		return new OperateIndentResult(Result.OK, true);
	}
	
}
