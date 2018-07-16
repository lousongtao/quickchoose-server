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

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.models.IPayWayDataAccessor;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.PayWay;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.indent.models.IIndentDataAccessor;
import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.indent.models.IndentDetail;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.management.models.IShiftWorkDataAccessor;
import com.shuishou.digitalmenu.management.models.ShiftWork;
import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.printertool.PrintJob;
import com.shuishou.digitalmenu.printertool.PrintQueue;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
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
	
	@Autowired
	private IPayWayDataAccessor paywayDA;
	
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
	public ObjectListResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime) {
		List<ShiftWork> sws = shiftWorkDA.queryShiftWork(start, limit, shiftName, startTime, endTime);
		if (sws == null || sws.isEmpty())
			return new ObjectListResult(Result.OK, true);

		int count = shiftWorkDA.queryShiftWorkCount(start, limit, shiftName, startTime, endTime);
		if (count >= 300)
			return new ObjectListResult("Record is over 300, please change the filter", false, null, count);
		ObjectListResult result = new ObjectListResult(Result.OK, true, sws);
		return result;
	}

	@Override
	@Transactional
	public CurrentDutyResult startShiftWork(int userId, boolean printLastDutyTicket) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new CurrentDutyResult(Result.FAIL, false);
		}
		//firstly, load last duty record
		ShiftWork lastSW = shiftWorkDA.getLastShiftWork();
		if (lastSW != null && lastSW.getEndTime() == null){
			lastSW.setEndTime(new Date());
			shiftWorkDA.save(lastSW);
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
		if (printLastDutyTicket){
			String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
			printShiftTicket(tempfilePath + "/ShiftWorkStat.json", lastSW.getStartTime(), lastSW.getEndTime(), lastSW.getUserName());
		}
			
		return result;
	}

	@Override
	@Transactional
	public CurrentDutyResult endShiftWork(int userId, boolean printShiftTicket) {
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
			printShiftTicket(tempfilePath + "/ShiftWorkStat.json", sw.getStartTime(), sw.getEndTime(), user.getUsername());
		}
		
		return new CurrentDutyResult(Result.OK, true);
	}
	
	@Transactional
	private void printShiftTicket(String tempfile, Date startTime, Date endTime, String userName){
		List<Printer> printers = printerDA.queryPrinters();
		if (printers == null || printers.isEmpty())
			return;
		if (endTime == null)
			return;
		List<PayWay> otherPayWays = paywayDA.queryPayWays();
		//calculate period
		long millsecs = endTime.getTime() - startTime.getTime();
		int hours = (int)(millsecs / (60*60*1000));
		int minutes = (int)((millsecs - hours * 60 * 60 * 1000)/(60*1000));
		int seconds = (int)((millsecs - hours * 60 * 60 * 1000 - minutes * 60 * 1000)/1000);
		String workPeriod = (hours > 0 ? hours+"h " : "") + minutes + "m " + seconds + "s";
		List<Indent> paidIndents = indentDA.getIndentsByPaidTime(startTime, endTime);
		int customerAmount= 0;
		int indnetAmount = 0;
		int dishAmount = 0;
		double cashMoney = 0;
		double bankcardMoney = 0;
		double memberMoney = 0;
		double totalPrice = 0;
		double paidPrice = 0;
		HashMap<String, Double> mapOtherPay = new HashMap<>();//other pay way money
		if (otherPayWays != null && !otherPayWays.isEmpty()){
			for(PayWay pw : otherPayWays){
				mapOtherPay.put(pw.getName(), new Double(0.0));
			}
		}
		Map<String, Map<String, String>> mapDishAmount = new HashMap<String, Map<String, String>>();
		if (paidIndents != null){
			for(Indent indent : paidIndents){
				customerAmount += indent.getCustomerAmount();
				indnetAmount++;
				totalPrice += indent.getTotalPrice();
				paidPrice += indent.getPaidPrice();
				if (ConstantValue.INDENT_PAYWAY_CASH.equals(indent.getPayWay())){
					cashMoney += indent.getPaidPrice();
				} else if (ConstantValue.INDENT_PAYWAY_BANKCARD.equals(indent.getPayWay())){
					bankcardMoney += indent.getPaidPrice();
				} else if (ConstantValue.INDENT_PAYWAY_MEMBER.equals(indent.getPayWay())){
					memberMoney += indent.getPaidPrice();
				} else {
					//do double check for other payway, maybe there are some payway not existing in the list, which will make get(payway) == null
					if (mapOtherPay.get(indent.getPayWay()) == null){
						mapOtherPay.put(indent.getPayWay(), new Double(0.0));
					}
					mapOtherPay.put(indent.getPayWay(), mapOtherPay.get(indent.getPayWay()) + indent.getPaidPrice());
				}
				List<IndentDetail> details = indent.getItems();
				for(IndentDetail d : details){
					dishAmount += d.getAmount();
					Map<String, String> mg = mapDishAmount.get(d.getDishFirstLanguageName());
					if (mg == null){
						mg = new HashMap<String, String>();
						mg.put("name", d.getDishFirstLanguageName());
						mg.put("price", d.getDishPrice()+"");
						mg.put("amount", d.getAmount()+"");
						mg.put("totalPrice", (d.getDishPrice() * d.getAmount()) + "");
						mapDishAmount.put(d.getDishFirstLanguageName(), mg);
					} else {
						mg.put("amount", Integer.parseInt(mg.get("amount")) + d.getAmount()+"");
						mg.put("totalPrice", (d.getDishPrice() * Integer.parseInt(mg.get("amount"))) + "");
					}
				}
			}
		}
		Map<String,String> keys = new HashMap<String, String>();
		keys.put("userName", userName);
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
		for(String key : mapOtherPay.keySet()){
			keys.put(key, String.format("%.2f",mapOtherPay.get(key)));
		}
		List<Map<String, String>> goods = new ArrayList<Map<String, String>>(mapDishAmount.values());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keys", keys);
		params.put("goods", goods);
		for(Printer p : printers){
			if (p.getType() == ConstantValue.PRINTER_TYPE_COUNTER){
				PrintJob job = new PrintJob(tempfile, params, p.getPrinterName());
				PrintQueue.add(job);
				break;
			}
		}
	}

	@Override
	@Transactional
	public ObjectResult printShiftWork(int userId, int shiftWorkId) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new CurrentDutyResult("cannot find user by id "+ userId, false);
		}
		ShiftWork sw = shiftWorkDA.getShiftWorkById(shiftWorkId);
		if (sw == null){
			return new ObjectResult("cannot find shift work record by id "+ shiftWorkId, false);
		}
		String tempfilePath = request.getSession().getServletContext().getRealPath("/") + ConstantValue.CATEGORY_PRINTTEMPLATE;
		printShiftTicket(tempfilePath + "/ShiftWorkStat.json", sw.getStartTime(), sw.getEndTime(), user.getUsername());
		return new ObjectResult(Result.OK, true);
	}
}
