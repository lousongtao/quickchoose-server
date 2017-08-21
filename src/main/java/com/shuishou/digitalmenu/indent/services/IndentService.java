package com.shuishou.digitalmenu.indent.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	private DateFormat f = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	@Override
	@Transactional
	public synchronized MakeOrderResult saveIndent(String confirmCode, JSONArray jsonOrder, int deskid) {
		ConfirmCode cc = confirmCodeDA.getCode();
		if (!confirmCode.equals(cc.getCode()))
			return new MakeOrderResult("The confirm code is wrong, cannot make order.", false, -1);
		Desk desk = deskDA.getDeskById(deskid);
		if (desk == null)
			return new MakeOrderResult("cannot find table by id "+ deskid, false, -1);
		double totalprice = 0;
		Indent indent = new Indent();
		indent.setDeskName(desk.getName());
		indent.setTime(Calendar.getInstance().getTime());
		int sequence = indentDA.getMaxSequenceToday() + 1;
		indent.setDailySequence(sequence);
		
		for(int i = 0; i< jsonOrder.length(); i++){
			JSONObject o = (JSONObject) jsonOrder.get(i);
			int dishid = o.getInt("id");
			Dish dish = dishDA.getDishById(dishid);
			if (dish == null)
				return new MakeOrderResult("cannot find dish by id "+ dishid, false, -1);
			if (dish.isSoldOut()){
				return new MakeOrderResult("dish "+ dish.getChineseName() + " is Sold Out, cannot make order", false, -1);
			}
			IndentDetail detail = new IndentDetail();
			detail.setIndent(indent);
			detail.setDishId(dishid);
			detail.setAmount(o.getInt("amount"));
			detail.setDishChineseName(dish.getChineseName());
			detail.setDishEnglishName(dish.getEnglishName());
			detail.setDishPrice(dish.getPrice());
			detail.setAdditionalRequirements(o.getString("addtionalRequirements"));
			totalprice += detail.getAmount() * dish.getPrice();
			indent.addItem(detail);
//			indentDetailDA.save(detail);
		}
		indent.setTotalPrice(totalprice);
		indentDA.save(indent);
		print(indent);
		return new MakeOrderResult(Result.OK, true, sequence);
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
					keys.put("time", f.format(indent.getTime()));
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
						keys.put("time", f.format(indent.getTime()));
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
					keys.put("time", f.format(indent.getTime()));
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
			if (orderby.indexOf("time")>=0){
				orderbys.add("time");
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
				starttime = f.parse(sstarttime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (sendtime != null && sendtime.length() > 0){
			try {
				endtime = f.parse(sendtime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		List<Indent> indents = indentDA.getIndents(start, limit, starttime, endtime, bStatus, deskname, orderbys);
		if (indents == null || indents.isEmpty())
			return new GetIndentResult(Result.OK, true, null, 0);
		List<GetIndentResult.Indent> resultinfos = new ArrayList<GetIndentResult.Indent>(indents.size());
		
		for (int i = 0; i < indents.size(); i++) {
			GetIndentResult.Indent resultindent = new GetIndentResult.Indent();
			resultindent.id = indents.get(i).getId();
			resultindent.dailysequence = indents.get(i).getDailySequence();
			resultindent.deskname = indents.get(i).getDeskName();
			resultindent.status = indents.get(i).getStatus();
			resultindent.time = f.format(indents.get(i).getTime());
			resultindent.totalprice = indents.get(i).getTotalPrice();
			resultinfos.add(resultindent);
		}
		int count = indentDA.getIndentCount(starttime, endtime, bStatus, deskname);
		return new GetIndentResult(Result.OK, true, resultinfos, count);
	}

	@Override
	@Transactional
	public OperateIndentResult operateIndent(int userId, int indentId, byte operationType) {
		Indent indent = indentDA.getIndentById(indentId);
		if (indent == null)
			return new OperateIndentResult("cannot find Indent by Id:" + indentId, false);
		String logtype = LogData.LogType.INDENT_PAY.toString();
		if (operationType == ConstantValue.INDENT_OPERATIONTYPE_CANCEL)
			logtype = LogData.LogType.INDENT_CANCEL.toString();
		if (operationType == ConstantValue.INDENT_OPERATIONTYPE_CANCEL)
			indent.setStatus(ConstantValue.INDENT_STATUS_CANCELED);
		else if (operationType == ConstantValue.INDENT_OPERATIONTYPE_PAY)
			indent.setStatus(ConstantValue.INDENT_STATUS_PAID);
		indentDA.update(indent);
		
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
		if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_ADD){
			logtype = LogData.LogType.INDENTDETAIL_ADDDISH.toString();
			Dish dish = dishDA.getDishById(dishId);
			if (dish == null)
				return new OperateIndentResult("cannot find Dish by Id:" + dishId, false);
			Indent indent = indentDA.getIndentById(indentId);
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
			print(detail);
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_CHANGEAMOUNT){
			logtype = LogData.LogType.INDENTDETAIL_CHANGEAMOUNT.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			detail.setAmount(amount);
			indentDetailDA.update(detail);
			Indent indent = detail.getIndent();
			double totalprice = 0.0d;
			for(IndentDetail d : indent.getItems()){
				totalprice += d.getAmount() * d.getDishPrice();
			}
			indent.setTotalPrice(totalprice);
			indentDA.update(indent);
			print(detail);
		} else if (operateType == ConstantValue.INDENTDETAIL_OPERATIONTYPE_DELETE){
			logtype = LogData.LogType.INDENTDETAIL_DELETE.toString();
			IndentDetail detail = indentDetailDA.getIndentDetailById(indentDetailId);
			if (detail == null)
				return new OperateIndentResult("cannot find IndentDetail by IndentId:" + indentId + " + dishId:" + dishId, false);
			indentDetailDA.delete(detail);
			detail.getIndent().setTotalPrice(detail.getIndent().getTotalPrice() - detail.getAmount() * detail.getDishPrice());
			indentDA.update(detail.getIndent());
		} 
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, logtype,
						"User " + selfUser + " operate IndentDetail, indentId = " + indentId 
						+ ", dishId = " + dishId + ", indentdetailId = " + indentDetailId 
						+ ", amount = " + amount + ", operationType = " + operateType + ".");
		return new OperateIndentResult("ok", true);
		
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
		print(indent);
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

}
