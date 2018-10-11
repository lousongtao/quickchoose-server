/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.log.controllers;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.models.LogData.LogType;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.views.ObjectListResult;

@Controller
public class LogController extends BaseController {

	/**
	 * the log service.
	 */
	@Autowired
	private ILogService logService;

	/**
	 * the account service.
	 */
	@Autowired
	private IAccountService accountService;

	/**
	 * get accounts.
	 * 
	 * @param userId
	 *            the user id.
	 * @return the account list.
	 * @throws Exception
	 */
	@RequestMapping(value = "/log/logs", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ObjectListResult getLogList(@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "message", required = false, defaultValue = "") String message,
			@RequestParam(value = "page", required = false, defaultValue = "0") String pageStr,
			@RequestParam(value = "start", required = false, defaultValue = "0") String startStr,
			@RequestParam(value = "limit", required = false, defaultValue = "300") String limitStr,
			@RequestParam(value = "username", required = false, defaultValue = "") String username,
			@RequestParam(value = "type", required = false, defaultValue = "") String type,
			@RequestParam(value = "beginTime", required = false, defaultValue = "") String beginTimeStr,
			@RequestParam(value = "endTime", required = false, defaultValue = "") String endTimeStr) throws Exception {
		int start = Integer.parseInt(startStr);
		int limit = Integer.parseInt(limitStr);
		Date beginTime = null;
		Date endTime = null;
		if (beginTimeStr != null && beginTimeStr.length() > 0) {
			beginTime = ConstantValue.DFYMD.parse(beginTimeStr);
		}
		if (endTimeStr != null && endTimeStr.length() > 0) {
			endTime = ConstantValue.DFYMD.parse(endTimeStr);
		}
		return logService.queryLog(start, limit, username, beginTime, endTime, type, message);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/log/log_types", method = { RequestMethod.GET,  RequestMethod.POST })
	public @ResponseBody ObjectListResult getLogTypeList(
			@RequestParam(value = "userId", required = true) long userId) throws Exception {
		ArrayList<String> data = new ArrayList<String>();
		LogType[] logTypes = LogData.LogType.values();
		for (LogType lt : logTypes) {
			data.add(lt.toString());
		}
		return new ObjectListResult("ok", true, data);
	}

}
