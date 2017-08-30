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

import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.management.services.IManagementService;
import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
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
	public CurrentDutyResult getCurrentDuty(){
		return managementService.getCurrentDuty();
	}
	
	@RequestMapping(value="/management/getshiftwork", method = (RequestMethod.POST))
	public ShiftWorkResult getShiftWork(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value = "start", required = true) int start,
			@RequestParam(value = "limit", required = true) int limit,
			@RequestParam(value = "shiftName", required = false) String shiftName,
			@RequestParam(value = "startTime", required = false) String sStartTime,
			@RequestParam(value = "endTime", required = false) String sEndTime){
		if (!accountService.checkSession(userId, sessionId))
			return new ShiftWorkResult("invalid_session", false);
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIRMCODE)){
			return new ShiftWorkResult("no_permission", false);
		}
		DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
		Date startTime = null;
		Date endTime = null;
		if (sStartTime != null){
			try {
				startTime = df.parse(sStartTime);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}
		if (sEndTime != null){
			try {
				endTime = df.parse(sEndTime);
			} catch (ParseException e) {
				log.error(e.getMessage());
			}
		}
		return managementService.getShiftWorkList(userId, start, limit, shiftName, startTime, endTime);
	}
	
	@RequestMapping(value="/management/endshiftwork", method = (RequestMethod.POST))
	public Result endWork(@RequestParam(value = "userId", required = true) int userId){
		return managementService.endShiftWork(userId);
	}
	
	@RequestMapping(value="/management/startshiftwork", method = (RequestMethod.POST))
	public Result startWork(@RequestParam(value = "userId", required = true) int userId){
		return managementService.startShiftWork(userId);
	}
}
