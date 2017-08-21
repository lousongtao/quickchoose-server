package com.shuishou.digitalmenu.indent.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
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
import com.shuishou.digitalmenu.indent.services.IIndentService;
import com.shuishou.digitalmenu.indent.views.GetIndentDetailResult;
import com.shuishou.digitalmenu.indent.views.GetIndentResult;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.views.GridResult;


@Controller
public class IndentController {

	@Autowired
	private IIndentService indentService;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	/**
	 * 
	 * @param indents String of a JSONArray, including dishid, amount, additionalRequirements
	 * @param deskid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/makeindent", method = (RequestMethod.POST))
	public @ResponseBody MakeOrderResult makeOrder(
			@RequestParam(value="confirmCode", required = true) String confirmCode,
			@RequestParam(value="indents", required = true) String indents,
			@RequestParam(value="deskid", required = true) int deskid) throws Exception{
		JSONArray jsonOrder = new JSONArray(indents);
		
		return indentService.saveIndent(confirmCode, jsonOrder, deskid);
	}
	
	@RequestMapping(value="/indent/queryindent", method = (RequestMethod.GET))
	public @ResponseBody GetIndentResult queryIndent(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value = "page", required = false, defaultValue = "0") String pageStr,
			@RequestParam(value = "start", required = false, defaultValue = "0") String startStr,
			@RequestParam(value = "limit", required = false, defaultValue = "10") String limitStr,
			@RequestParam(value="starttime", required = false) String starttime,
			@RequestParam(value="endtime", required = false) String endtime,
			@RequestParam(value="status", required = false) String status,
			@RequestParam(value="deskname", required = false) String deskname,
			@RequestParam(value="orderby", required = false) String orderby) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new GetIndentResult("invalid_session", false, null,0);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_ORDER)){
			return new GetIndentResult("no_permission", false, null, 0);
		}
		int page = Integer.parseInt(pageStr);
		int start = Integer.parseInt(startStr);
		int limit = Integer.parseInt(limitStr);
		return indentService.queryIndent(start, limit, starttime, endtime, status, deskname,orderby);
	}
	
	@RequestMapping(value="/indent/queryindentdetail", method = (RequestMethod.GET))
	public @ResponseBody GetIndentDetailResult queryIndentDetail(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="indentId", required = false) int indentId) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new GetIndentDetailResult("invalid_session", false, null);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_ORDER)){
			return new GetIndentDetailResult("no_permission", false, null);
		}
		return indentService.queryIndentDetail(indentId);
	}
	
	/**
	 * 
	 * @param userId
	 * @param sessionId
	 * @param indentId
	 * @param operateType delete/cancel/pay/
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/operateindent", method = (RequestMethod.POST))
	public @ResponseBody OperateIndentResult operateIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="id", required = true) int indentId,
			@RequestParam(value="operatetype", required = true) byte operateType) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new OperateIndentResult("invalid_session", false);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.operateIndent(userId, indentId, operateType);
	}
	
	/**
	 * 
	 * @param userId
	 * @param sessionId
	 * @param indentId
	 * @param dishId
	 * @param amount
	 * @param operateType addDish/removeDish/changeAmount
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/operateindentdetail", method = (RequestMethod.POST))
	public @ResponseBody OperateIndentResult operateIndentDetail(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="indentId", required = false, defaultValue = "0") int indentId,
			@RequestParam(value="indentDetailId", required = false, defaultValue = "0") int indentDetailId,
			@RequestParam(value="dishId", required = false, defaultValue = "0") int dishId,
			@RequestParam(value="amount", required = false, defaultValue = "0") int amount,
			@RequestParam(value="operatetype", required = true) byte operateType) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new OperateIndentResult("invalid_session", false);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.operateIndentDetail(userId, indentId, dishId, indentDetailId, amount, operateType);
	}
	
	@RequestMapping(value="/indent/printindent", method = (RequestMethod.POST))
	public @ResponseBody GridResult printIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="indentId", required = true) int indentId) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new OperateIndentResult("invalid_session", false);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.printIndent(userId, indentId);
	}
	
	@RequestMapping(value="/indent/printindentdetail", method = (RequestMethod.POST))
	public @ResponseBody GridResult printIndentDetail(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "sessionId", required = true) String sessionId,
			@RequestParam(value="indentDetailId", required = true) int indentDetailId) throws Exception{
		if (!accountService.checkSession(userId, sessionId))
			return new OperateIndentResult("invalid_session", false);
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.printIndentDetail(userId, indentDetailId);
	}
}
