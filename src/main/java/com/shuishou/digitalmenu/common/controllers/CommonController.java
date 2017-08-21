package com.shuishou.digitalmenu.common.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.common.services.ICommonService;
import com.shuishou.digitalmenu.common.views.GetConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetPrinterResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

@Controller
public class CommonController {

	@Autowired
	private ICommonService commonService;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	@RequestMapping(value="/common/checkconfirmcode", method = (RequestMethod.POST))
	public @ResponseBody Result checkConfirmCode(
			@RequestParam(value="code", required = true) String code) throws Exception{
		return commonService.checkConfirmCode(code);
	}
	
	@RequestMapping(value="/common/getconfirmcode", method = (RequestMethod.GET))
	public @ResponseBody GetConfirmCodeResult getConfirmCode() throws Exception{
		return commonService.getConfirmCode();
	}
	
	@RequestMapping(value="/common/saveconfirmcode", method = (RequestMethod.POST))
	public @ResponseBody Result saveConfirmCode(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="code", required = true) String code) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIRMCODE)){
			return new Result("no_permission");
		}
		return commonService.saveConfirmCode(userId,code);
	}
	
	@RequestMapping(value="/common/getdesks", method = (RequestMethod.GET))
	public @ResponseBody GetDeskResult getDesks() throws Exception{
		//由于安卓端需要此请求, 这里不做权限验证
//		if (!accountService.checkSession(userId, sessionId))
//			return new GetDeskResult("invalid_session", false);
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_DESK)){
//			return new GetDeskResult("no_permission", false);
//		}
		return commonService.getDesks();
	}
	
	@RequestMapping(value="/common/adddesk", method = (RequestMethod.POST))
	public @ResponseBody Result saveDesk(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="name", required = true) String name) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.saveDesk(userId,name);
	}
	
	@RequestMapping(value="/common/updatedesk", method = (RequestMethod.POST))
	public @ResponseBody Result updateDesk(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="id", required = true) int id,
			@RequestParam(value="name", required = true) String name) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.updateDesk(userId,id, name);
	}
	
	@RequestMapping(value="/common/deletedesk", method = (RequestMethod.POST))
	public @ResponseBody Result deleteDesk(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.deleteDesk(userId,id);
	}
	
	@RequestMapping(value="/common/getprinters", method = (RequestMethod.GET))
	public @ResponseBody GetPrinterResult getPrinters() throws Exception{
		return commonService.getPrinters();
	}
	
	@RequestMapping(value="/common/addprinter", method = (RequestMethod.POST))
	public @ResponseBody Result savePrinter(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="printerName", required = true) String printerName,
			@RequestParam(value="copy", required = true) int copy,
			@RequestParam(value="printStyle", required = true) byte printStyle) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PRINTER)){
			return new Result("no_permission");
		}
		return commonService.savePrinter(userId,name, printerName, copy, printStyle);
	}
	
	@RequestMapping(value="/common/deleteprinter", method = (RequestMethod.POST))
	public @ResponseBody Result deletePrinter(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new Result("invalid_session");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PRINTER)){
			return new Result("no_permission");
		}
		return commonService.deletePrinter(userId,id);
	}
}
