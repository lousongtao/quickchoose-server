package com.shuishou.digitalmenu.management.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.account.views.GetAccountsResult;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.management.services.IManagementService;
import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

@Controller
public class ManagementController {
	private Logger log = Logger.getLogger("ManagementController");

	@Autowired
	private IManagementService managementService;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	@RequestMapping(value="/management/getcurrentduty", method = (RequestMethod.GET))
	public @ResponseBody CurrentDutyResult getCurrentDuty() throws Exception{
		return managementService.getCurrentDuty();
	}
	
	@RequestMapping(value="/management/printshiftwork", method = (RequestMethod.POST))
	public @ResponseBody GridResult printShiftWork(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "shiftWorkId", required = true) int shiftWorkId) throws Exception{
		return managementService.printShiftWork(userId, shiftWorkId);
	}
	
	@RequestMapping(value="/management/getshiftwork", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody ShiftWorkResult getShiftWork(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "startTime", required = false) String sStartTime,
			@RequestParam(value = "endTime", required = false) String sEndTime) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_SHIFTWORK)){
			return new ShiftWorkResult("no_permission", false);
		}
		Date startTime = null;
		Date endTime = null;
		if (sStartTime != null && sStartTime.length() > 0){
			try {
				startTime = ConstantValue.DFYMD.parse(sStartTime);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}
		if (sEndTime != null && sEndTime.length() > 0){
			try {
				endTime = ConstantValue.DFYMD.parse(sEndTime);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}
		return managementService.getShiftWorkList(userId, start, limit, userName, startTime, endTime);
	}
	
	@RequestMapping(value="/management/endshiftwork", method = (RequestMethod.POST))
	public @ResponseBody CurrentDutyResult endWork(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "startTime", required = true) String sStartTime,
			@RequestParam(value = "printShiftTicket", required = true) boolean printShiftTicket) throws Exception{
		return managementService.endShiftWork(userId, ConstantValue.DFYMDHMS.parse(sStartTime), printShiftTicket);
	}
	
	@RequestMapping(value="/management/startshiftwork", method = (RequestMethod.POST))
	public @ResponseBody CurrentDutyResult startWork(@RequestParam(value = "userId", required = true) int userId) throws Exception{
		return managementService.startShiftWork(userId);
	}
}
