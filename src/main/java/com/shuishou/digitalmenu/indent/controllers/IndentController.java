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
import com.shuishou.digitalmenu.views.ObjectResult;


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
			@RequestParam(value="deskid", required = true) int deskid,
			@RequestParam(value="customerAmount", required = true) int customerAmount) throws Exception{
		JSONArray jsonOrder = new JSONArray(indents);
		
		return indentService.saveIndent(confirmCode, jsonOrder, deskid, customerAmount);
	}
	
	@RequestMapping(value="/indent/cleardesk", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult clearDesk(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="deskId", required = true) int deskId) throws Exception{
		
		return indentService.clearDesk(userId, deskId);
	}
	
	@RequestMapping(value="/indent/queryindent", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody GetIndentResult queryIndent(
			@RequestParam(value = "page", required = false, defaultValue = "0") String pageStr,
			@RequestParam(value = "start", required = false, defaultValue = "0") String startStr,
			@RequestParam(value = "limit", required = false, defaultValue = "100") String limitStr,
			@RequestParam(value="starttime", required = false) String starttime,
			@RequestParam(value="endtime", required = false) String endtime,
			@RequestParam(value="status", required = false) String status,
			@RequestParam(value="deskname", required = false) String deskname,
			@RequestParam(value="orderby", required = false) String orderby) throws Exception{
		
		int page = Integer.parseInt(pageStr);
		int start = Integer.parseInt(startStr);
		int limit = Integer.parseInt(limitStr);
		return indentService.queryIndent(start, limit, starttime, endtime, status, deskname,orderby);
	}
	
	@RequestMapping(value="/indent/queryindentdetail", method = (RequestMethod.GET))
	public @ResponseBody GetIndentDetailResult queryIndentDetail(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value="indentId", required = false) int indentId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_ORDER)){
			return new GetIndentDetailResult("no_permission", false, null);
		}
		return indentService.queryIndentDetail(indentId);
	}
	
	/**
	 * 
	 * @param userId
	 * @param indentId
	 * @param operateType delete/cancel/pay/
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/operateindent", method = (RequestMethod.POST))
	public @ResponseBody OperateIndentResult operateIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int indentId,
			@RequestParam(value="operatetype", required = true) byte operateType,
			@RequestParam(value="paidPrice", required = false, defaultValue = "0") double paidPrice,
			@RequestParam(value="payWay", required = false, defaultValue = "0") byte payWay,
			@RequestParam(value="memberCard", required = false, defaultValue = "0") String memberCard) throws Exception{
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.operateIndent(userId, indentId, operateType, paidPrice, payWay, memberCard);
	}
	
	/**
	 * 
	 * @param userId
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
			@RequestParam(value="indentId", required = false, defaultValue = "0") int indentId,
			@RequestParam(value="indentDetailId", required = false, defaultValue = "0") int indentDetailId,
			@RequestParam(value="dishId", required = false, defaultValue = "0") int dishId,
			@RequestParam(value="amount", required = false, defaultValue = "0") int amount,
			@RequestParam(value="operatetype", required = true) byte operateType) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.operateIndentDetail(userId, indentId, dishId, indentDetailId, amount, operateType);
	}
	
	@RequestMapping(value="/indent/adddishtoindent", method = (RequestMethod.POST))
	public @ResponseBody MakeOrderResult addDishToIndent(
			@RequestParam(value="deskid", required = true) int deskId,
			@RequestParam(value="indents", required = true) String indents) throws Exception{
		JSONArray jsonOrder = new JSONArray(indents);
		return indentService.addDishToIndent(deskId, jsonOrder);
	}
	
	@RequestMapping(value="/indent/printindent", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult printIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="indentId", required = true) int indentId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.printIndent(userId, indentId);
	}
	
	@RequestMapping(value="/indent/printindentdetail", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult printIndentDetail(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="indentDetailId", required = true) int indentDetailId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.printIndentDetail(userId, indentDetailId);
	}
	
	@RequestMapping(value="/indent/changedesks", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult changeDesks(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="deskId1", required = true) int deskId1,
			@RequestParam(value="deskId2", required = true) int deskId2) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.changeDesks(userId, deskId1, deskId2);
	}
}
