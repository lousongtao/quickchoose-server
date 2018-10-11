package com.shuishou.digitalmenu.common.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.common.services.ICommonService;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Controller
public class CommonController extends BaseController {

	@Autowired
	private ICommonService commonService;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	@RequestMapping(value="/common/testserverconnection", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result testConnection() throws Exception{
		return new Result(Result.OK);
	}
	
	@RequestMapping(value="/common/checkupgradeapk", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result checkUpgradeApk() throws Exception{
		return commonService.checkUpgradeApk();
	}
	
	@RequestMapping(value="/common/queryconfigmap", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult queryConfigMap() throws Exception{
		return commonService.queryConfigMap();
	}
	
	@RequestMapping(value="/common/savecode", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveCode(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="oldCode", required = true) String oldCode,
			@RequestParam(value="code", required = true) String code,
			@RequestParam(value="key", required = true) String key) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
			return new Result("no_permission");
		}
		return commonService.saveCode(userId, oldCode, code, key);
	}
	
	@RequestMapping(value="/common/saveprintticket", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result savePrintTicket(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="PRINTTICKET", required = true) String printTicket) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
			return new Result("no_permission");
		}
		return commonService.savePrintTicket(userId, printTicket);
	}
	
	@RequestMapping(value="/common/savebranchname", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveBranchName(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="branchName", required = true) String branchName) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
			return new Result("no_permission");
		}
		return commonService.saveBranchName(userId, branchName);
	}
	
	@RequestMapping(value="/common/savemembermanagementway", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveMemberManagementWay(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="needPassword", required = true) boolean needPassword,
			@RequestParam(value="byScore", required = true) boolean byScore,
			@RequestParam(value="byDeposit", required = true) boolean byDeposit,
			@RequestParam(value="scorePerDollar", required = false, defaultValue = "0") double scorePerDollar) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
			return new Result("no_permission");
		}
		return commonService.saveMemberManagementWay(userId, byScore, byDeposit, scorePerDollar, needPassword);
	}
	
//	@RequestMapping(value="/common/saveconfirmcode", method = {RequestMethod.POST, RequestMethod.GET})
//	public @ResponseBody Result saveConfirmCode(
//			@RequestParam(value = "userId", required = true) int userId,
//			@RequestParam(value="oldCode", required = true) String oldCode,
//			@RequestParam(value="code", required = true) String code) throws Exception{
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
//			return new Result("no_permission");
//		}
//		return commonService.saveConfirmCode(userId, oldCode,code);
//	}
//	
//	@RequestMapping(value="/common/savecleartablecode", method = {RequestMethod.POST, RequestMethod.GET})
//	public @ResponseBody Result saveClearTableCode(
//			@RequestParam(value = "userId", required = true) int userId,
//			@RequestParam(value="oldCode", required = true) String oldCode,
//			@RequestParam(value="code", required = true) String code) throws Exception{
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
//			return new Result("no_permission");
//		}
//		return commonService.saveClearTableCode(userId, oldCode,code);
//	}
//	
//	@RequestMapping(value="/common/savecancelordercode", method = {RequestMethod.POST, RequestMethod.GET})
//	public @ResponseBody Result saveCancelOrderCode(
//			@RequestParam(value = "userId", required = true) int userId,
//			@RequestParam(value="oldCode", required = true) String oldCode,
//			@RequestParam(value="code", required = true) String code) throws Exception{
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
//			return new Result("no_permission");
//		}
//		return commonService.saveCancelOrderCode(userId, oldCode,code);
//	}
//	
//	@RequestMapping(value="/common/saveopencashdrawercode", method = {RequestMethod.POST, RequestMethod.GET})
//	public @ResponseBody Result saveOpenCashdrawerCode(
//			@RequestParam(value = "userId", required = true) int userId,
//			@RequestParam(value="oldCode", required = true) String oldCode,
//			@RequestParam(value="code", required = true) String code) throws Exception{
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
//			return new Result("no_permission");
//		}
//		return commonService.saveOpenCashdrawerCode(userId,oldCode, code);
//	}
	
	@RequestMapping(value="/common/savelanguageset", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveLanguageSet(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="amount", required = true) int amount,
			@RequestParam(value="firstName", required = true) String firstName,
			@RequestParam(value="secondName", required = false, defaultValue = "") String secondName,
			@RequestParam(value = "print2ndLanguage", required = false, defaultValue = "false") boolean print2ndLanguage) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CHANGE_CONFIG)){
			return new Result("no_permission");
		}
		return commonService.saveLanguageSet(userId,amount, firstName, secondName, print2ndLanguage);
	}
	
	@RequestMapping(value="/common/getdesks", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody GetDeskResult getDesks() throws Exception{
		//由于安卓端需要此请求, 这里不做权限验证
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_DESK)){
//			return new GetDeskResult("no_permission", false);
//		}
		return commonService.getDesks();
	}
	
	@RequestMapping(value="/common/getdeskswithindents", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody GetDeskWithIndentResult getDesksWithIndents(
			@RequestParam(value = "userId", required = true) int userId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_DESK)){
			return new GetDeskWithIndentResult("no_permission", false, null);
		}
		return commonService.getDesksWithIndents();
	}
	
	@RequestMapping(value="/common/adddesk", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveDesk(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.saveDesk(userId,name, sequence);
	}
	
	@RequestMapping(value="/common/updatedesk", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result updateDesk(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.updateDesk(userId,id, name, sequence);
	}
	
	@RequestMapping(value="/common/deletedesk", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result deleteDesk(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DESK)){
			return new Result("no_permission");
		}
		return commonService.deleteDesk(userId,id);
	}
	
	@RequestMapping(value="/common/getprinters", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult getPrinters() throws Exception{
		return commonService.getPrinters();
	}
	
	@RequestMapping(value="/common/addprinter", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result savePrinter(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="printerName", required = true) String printerName,
			@RequestParam(value="type", required = true) int type) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PRINTER)){
			return new Result("no_permission");
		}
		return commonService.savePrinter(userId,name, printerName, type);
	}
	
	@RequestMapping(value="/common/updateprinter", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result updatePrinter(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="printerName", required = true) String printerName,
			@RequestParam(value="type", required = true) int type) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PRINTER)){
			return new Result("no_permission");
		}
		return commonService.updatePrinter(userId, id, name, printerName, type);
	}
	
	@RequestMapping(value="/common/deleteprinter", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result deletePrinter(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PRINTER)){
			return new Result("no_permission");
		}
		return commonService.deletePrinter(userId,id);
	}
	
	@RequestMapping(value="/common/testconnection", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result testPrinterConnection(
			@RequestParam(value="id", required = true) int id) throws Exception{
		return commonService.testPrinterConnection(id);
	}
	
	@RequestMapping(value="/common/getdiscounttemplates", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult getDiscountTemplates() throws Exception{
		return commonService.getDiscountTemplates();
	}
	
	@RequestMapping(value="/common/adddiscounttemplate", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result saveDiscountTemplate(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="name", required = true) String name,
			@RequestParam(value="value", required = true) double value,
			@RequestParam(value="type", required = true) int type) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DISCOUNTTEMPLATE)){
			return new Result("no_permission");
		}
		return commonService.saveDiscountTemplate(userId, name, value, type);
	}
	
	@RequestMapping(value="/common/deletediscounttemplate", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result deleteDiscountTemplate(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_DISCOUNTTEMPLATE)){
			return new Result("no_permission");
		}
		return commonService.deleteDiscountTemplate(userId,id);
	}
	
	@RequestMapping(value="/common/getpayways", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult getPayWays() throws Exception{
		return commonService.getPayWays();
	}
	
	@RequestMapping(value="/common/addpayway", method =  {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result savePayWay(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="name", required = true) String name) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PAYWAY)){
			return new Result("no_permission");
		}
		return commonService.savePayWay(userId, name);
	}
	
	@RequestMapping(value="/common/deletepayway", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result deletePayWay(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_PAYWAY)){
			return new Result("no_permission");
		}
		return commonService.deletePayWay(userId,id);
	}
	
	@RequestMapping(value="/common/mergedesks", method= {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody GetDeskWithIndentResult mergeDesks(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "mainDeskId", required = true) int mainDeskId,
			@RequestParam(value = "subDeskId", required = true) String subDesksId) throws Exception{
		return commonService.mergeDesks(userId, mainDeskId, subDesksId);
	}
	
	@RequestMapping(value="/common/uploaderrorlog", method= {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult uploadErrorLog(
			@RequestParam(value = "machineCode", required = true) String machineCode,
			@RequestParam(value = "logfile", required = true) MultipartFile logfile) throws Exception{
		return commonService.uploadErrorLog(machineCode, logfile);
	}
}
