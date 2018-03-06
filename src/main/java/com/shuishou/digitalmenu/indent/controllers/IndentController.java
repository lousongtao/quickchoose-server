package com.shuishou.digitalmenu.indent.controllers;

import java.util.Date;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.DataCheckException;
import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.indent.services.IIndentService;
import com.shuishou.digitalmenu.indent.views.MakeOrderResult;
import com.shuishou.digitalmenu.indent.views.OperateIndentResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;


@Controller
public class IndentController extends BaseController {

	@Autowired
	private IIndentService indentService;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	/**
	 * 
	 * @param indents : a string of a JSONArray, including dishid, amount, additionalRequirements
	 * @param deskid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/makeindent", method = (RequestMethod.POST))
	public @ResponseBody synchronized MakeOrderResult makeOrder(
			@RequestParam(value="confirmCode", required = true) String confirmCode,
			@RequestParam(value="indents", required = true) String indents,
			@RequestParam(value="deskid", required = true) int deskid,
			@RequestParam(value="customerAmount", required = true) int customerAmount,
			@RequestParam(value="comments", required = false, defaultValue = "") String comments) throws Exception{
		JSONArray jsonOrder = new JSONArray(indents);
		
		return indentService.saveIndent(confirmCode, jsonOrder, deskid, customerAmount, comments);
	}
	
	/**
	 * split an order and pay it 
	 * 
	 * @param indents: a string of a JSONArray, including dishid, amount, additionalRequirements
	 * @param deskid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/splitindentandpay", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult splitOrderAndPay(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "confirmCode", required = true) String confirmCode,
			@RequestParam(value = "originIndentId", required = true) int originIndentId,
			@RequestParam(value = "indents", required = true) String indents,
			@RequestParam(value = "paidCash", required = true) double paidCash,
			@RequestParam(value = "paidPrice", required = true) double paidPrice,
			@RequestParam(value = "payWay", required = true) String payWay,
			@RequestParam(value = "memberCard", required = false) String memberCard,
			@RequestParam(value = "memberPassword", required = false) String memberPassword) throws Exception{
		JSONArray jsonOrder = new JSONArray(indents);
		try {
			return indentService.splitIndent(userId, confirmCode, jsonOrder, originIndentId, paidPrice, paidCash, payWay, memberCard, memberPassword);
		} catch(DataCheckException e){
			return new OperateIndentResult(e.getMessage(), false);
		}
	}
	
	@RequestMapping(value="/indent/cleardesk", method = (RequestMethod.POST))
	public @ResponseBody ObjectResult clearDesk(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="deskId", required = true) int deskId) throws Exception{
		
		return indentService.clearDesk(userId, deskId);
	}
	
	/**
	 * 
	 * @param pageStr
	 * @param startStr
	 * @param limitStr : java UI is not good to develop table page bar, so set this value is 300. if records are more, warn operator to set more filter
	 * @param starttime : just compare the indent's starttime
	 * @param endtime : just compare the indent's starttime
	 * @param status use string to express status. take care the letter's case; Paid/Unpaid/Other
	 * @param deskname
	 * @param orderby
	 * @param orderbydesc
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/queryindent", method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody ObjectListResult queryIndent(
			@RequestParam(value = "page", required = false, defaultValue = "0") String pageStr,
			@RequestParam(value = "start", required = false, defaultValue = "0") String startStr,
			@RequestParam(value = "limit", required = false, defaultValue = "300") String limitStr,
			@RequestParam(value="starttime", required = false) String starttime,
			@RequestParam(value="endtime", required = false) String endtime,
			@RequestParam(value="status", required = false) String status,
			@RequestParam(value="deskname", required = false) String deskname,
			@RequestParam(value="orderby", required = false) String orderby,
			@RequestParam(value="orderbydesc", required = false) String orderbydesc) throws Exception{
		
		int page = Integer.parseInt(pageStr);
		int start = Integer.parseInt(startStr);
		int limit = Integer.parseInt(limitStr);
		return indentService.queryIndent(start, limit, starttime, endtime, status, deskname,orderby,orderbydesc);
	}
	
	/**
	 * 
	 * @param userId
	 * @param indentId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/dopayindent", method = (RequestMethod.POST))
	public @ResponseBody OperateIndentResult doPayIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int indentId,
			@RequestParam(value="paidPrice", required = false, defaultValue = "0") double paidPrice,
			@RequestParam(value="paidCash", required = true) double paidCash,
			@RequestParam(value="payWay", required = false, defaultValue = ConstantValue.INDENT_PAYWAY_CASH) String payWay,
			@RequestParam(value="memberPassword", required = false) String memberPassword,
			@RequestParam(value="memberCard", required = false) String memberCard) throws Exception{
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		try{
			return indentService.doPayIndent(userId, indentId, paidPrice, paidCash, payWay, memberCard, memberPassword);
		} catch(DataCheckException e){
			return new OperateIndentResult(e.getMessage(), false);
		}
	}
	
	/**
	 * 
	 * @param userId
	 * @param indentId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/indent/docancelindent", method = (RequestMethod.POST))
	public @ResponseBody OperateIndentResult doCancelIndent(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value="id", required = true) int indentId) throws Exception{
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
			return new OperateIndentResult("no_permission", false);
		}
		return indentService.doCancelIndent(userId, indentId);
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
	
//	@RequestMapping(value="/indent/printindentdetail", method = (RequestMethod.POST))
//	public @ResponseBody ObjectResult printIndentDetail(
//			@RequestParam(value = "userId", required = true) int userId,
//			@RequestParam(value="indentDetailId", required = true) int indentDetailId) throws Exception{
//		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_ORDER)){
//			return new OperateIndentResult("no_permission", false);
//		}
//		return indentService.printIndentDetail(userId, indentDetailId);
//	}
	
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
