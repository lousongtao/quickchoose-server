package com.shuishou.digitalmenu.management.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.management.models.IShiftWorkDataAccessor;
import com.shuishou.digitalmenu.management.models.ShiftWork;
import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
import com.shuishou.digitalmenu.printertool.PrintJob;
import com.shuishou.digitalmenu.printertool.PrintQueue;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class ManagementService implements IManagementService{

	@Autowired
	private IShiftWorkDataAccessor shiftWorkDA;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IPrinterDataAccessor printerDA;
	
	@Autowired
	private IIndentDataAccessor indentDA;
	
	@Autowired
	private HttpServletRequest request;
	
	@Override
	@Transactional
	public CurrentDutyResult getCurrentDuty() {
		ShiftWork sw = shiftWorkDA.getLastShiftWork();
		if (sw == null){
			return new CurrentDutyResult(Result.OK, true);
		} else {
			if (sw.getEndTime() != null){
				return new CurrentDutyResult(Result.OK, true);
			} else {
				CurrentDutyResult result = new CurrentDutyResult(Result.OK, true);
				result.data.currentDutyName = sw.getUserName();
				result.data.currentDutyId = sw.getUserId();
				result.data.startTime = ConstantValue.DFYMDHMS.format(sw.getStartTime());
				return result;
			}
		}
	}

	@Override
	@Transactional
	public ShiftWorkResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime) {
		List<ShiftWork> sws = shiftWorkDA.queryShiftWork(start, limit, shiftName, startTime, endTime);
		if (sws == null || sws.isEmpty())
			return new ShiftWorkResult(Result.OK, true);

		ShiftWorkResult.ShiftWork swInfo = null;
		List<ShiftWorkResult.ShiftWork> swInfos = new ArrayList<ShiftWorkResult.ShiftWork>();
		
		for (int i = 0; i < sws.size(); i++) {
			ShiftWork sw = sws.get(i);
			swInfo = new ShiftWorkResult.ShiftWork();
			swInfo.id = sw.getId();
			swInfo.userName = sw.getUserName();
			swInfo.startTime = ConstantValue.DFYMDHMS.format(sw.getStartTime());
			if (sw.getEndTime() != null){
				swInfo.endTime = ConstantValue.DFYMDHMS.format(sw.getEndTime());
			}
			swInfos.add(swInfo);
		}
		ShiftWorkResult result = new ShiftWorkResult(Result.OK, true);
		result.data = swInfos;
		return result;
	}

	@Override
	@Transactional
	public CurrentDutyResult startShiftWork(int userId) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new CurrentDutyResult(Result.FAIL, false);
		}
		ShiftWork sw = new ShiftWork();
		sw.setUserName(user.getUsername());
		sw.setUserId(userId);
		sw.setStartTime(new Date());
		shiftWorkDA.insertShitWork(sw);
		logService.write(user, LogData.LogType.SHIFTWORK.toString(),
				"User " + user.getUsername() + " start work.");
		CurrentDutyResult result = new CurrentDutyResult(Result.OK, true);
		result.data.currentDutyId = (int)user.getId();
		result.data.currentDutyName = user.getUsername();
		result.data.startTime = ConstantValue.DFYMDHMS.format(sw.getStartTime());
		return result;
	}

	@Override
	@Transactional
	public CurrentDutyResult endShiftWork(int userId, Date startTime, boolean printShiftTicket) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new CurrentDutyResult("cannot find user by id "+ userId, false);
		}
		ShiftWork sw = shiftWorkDA.getLastShiftWork();
		if (sw.getUserId() != userId){
			return new CurrentDutyResult("Database record error. The last on duty record is not for the user, userId : "+ userId, false);
		}
		sw.setEndTime(new Date());
		shiftWorkDA.save(sw);
		logService.write(user, LogData.LogType.SHIFTWORK.toString(),
				"User " + user.getUsername() + " end work.");
		if (printShiftTicket){
			String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
			printShiftTicket(tempfilePath + "/ShiftWorkStat.json", startTime, sw.getEndTime());
		}
		
		return new CurrentDutyResult(Result.OK, true);
	}
	
	@Transactional
	private void printShiftTicket(String tempfile, Date startTime, Date endTime){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		//calculate period
		long millsecs = endTime.getTime() - startTime.getTime();
		int hours = (int)(millsecs / (60*60*1000));
		int minutes = (int)((millsecs - hours * 60 * 60 * 1000)/(60*1000));
		int seconds = (int)((millsecs - hours * 60 * 60 * 1000 - minutes * 60 * 1000)/1000);
		String workPeriod = (hours > 0 ? hours+" hs " : "") + minutes + " ms " + seconds + " s";
		List<Indent> paidIndents = indentDA.getIndentsByPaidTime(startTime, endTime);
		int customerAmount= 0;
		int indnetAmount = 0;
		int dishAmount = 0;
		double cashMoney = 0;
		double bankcardMoney = 0;
		double memberMoney = 0;
		double totalPrice = 0;
		double paidPrice = 0;
		Map<String, Map<String, String>> mapDishAmount = new HashMap<String, Map<String, String>>();
		if (paidIndents != null){
			for(Indent indent : paidIndents){
				customerAmount += indent.getCustomerAmount();
				indnetAmount++;
				totalPrice += indent.getPaidPrice();
				paidPrice += indent.getPaidPrice();
				if (indent.getPayWay() == ConstantValue.INDENT_PAYWAY_CASH){
					cashMoney += indent.getPaidPrice();
				} else if (indent.getPayWay() == ConstantValue.INDENT_PAYWAY_CARD){
					bankcardMoney += indent.getPaidPrice();
				} else if (indent.getPayWay() == ConstantValue.INDENT_PAYWAY_MEMBER){
					memberMoney += indent.getPaidPrice();
				}
				List<IndentDetail> details = indent.getItems();
				for(IndentDetail d : details){
					dishAmount += d.getAmount();
					Map<String, String> mg = mapDishAmount.get(d.getDishChineseName());
					if (mg == null){
						mg = new HashMap<String, String>();
						mg.put("name", d.getDishChineseName());
						mg.put("price", d.getDishPrice()+"");
						mg.put("amount", d.getAmount()+"");
						mg.put("totalPrice", (d.getDishPrice() * d.getAmount()) + "");
						mapDishAmount.put(d.getDishChineseName(), mg);
					} else {
						mg.put("amount", Integer.parseInt(mg.get("amount")) + d.getAmount()+"");
						mg.put("totalPrice", (d.getDishPrice() * Integer.parseInt(mg.get("amount"))) + "");
					}
				}
			}
		}
		Map<String,String> keys = new HashMap<String, String>();
		keys.put("restaurant", "HAO SZECHUAN 好吃嘴 北桥总店");
		keys.put("printType", "交班单");
		keys.put("startTime", ConstantValue.DFYMDHMS.format(startTime));
		keys.put("endTime", ConstantValue.DFYMDHMS.format(endTime));
		keys.put("workHours", workPeriod);
		keys.put("customerAmount", customerAmount+"");
		keys.put("indnetAmount", indnetAmount+"");
		keys.put("dishAmount", dishAmount + "");
		keys.put("cashMoney", String.format("%.2f",cashMoney));
		keys.put("bankcardMoney", String.format("%.2f",bankcardMoney));
		keys.put("memberMoney", String.format("%.2f",memberMoney));
		keys.put("totalPrice", String.format("%.2f",totalPrice));
		keys.put("paidPrice", String.format("%.2f",paidPrice));
		keys.put("gst", String.format("%.2f",(double)(totalPrice/11)));
		keys.put("printTime", ConstantValue.DFYMDHMS.format(new Date()));

		List<Map<String, String>> goods = new ArrayList<Map<String, String>>(mapDishAmount.values());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keys);
		params.put("goods", goods);
		for(Printer p : printers){
			if (!"counter".equals(p.getName()))
				continue;
			int copy = p.getCopy();
			for(int i = 0; i< copy; i++){
				
				PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
				PrintQueue.add(job);
			}
		}
	}

	@Override
	@Transactional
	public GridResult printShiftWork(int userId, int shiftWorkId) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new CurrentDutyResult("cannot find user by id "+ userId, false);
		}
		ShiftWork sw = shiftWorkDA.getShiftWorkById(shiftWorkId);
		if (sw == null){
			return new GridResult("cannot find shift work record by id "+ shiftWorkId, false);
		}
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printShiftTicket(tempfilePath + "/ShiftWorkStat.json", sw.getStartTime(), sw.getEndTime());
		return new GridResult(Result.OK, true);
	}
}
