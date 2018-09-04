package com.shuishou.digitalmenu.member.controllers;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.ServerProperties;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.member.services.IMemberCloudService;
import com.shuishou.digitalmenu.member.services.IMemberService;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

@Controller
public class MemberController extends BaseController {
	private Logger log = Logger.getLogger("MemberController");
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IMemberService memberService;
	
	@Autowired
	private IMemberCloudService memberCloudService;
	
	@RequestMapping(value = "/member/querymember", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult queryMember(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "memberCard", required = false, defaultValue = "") String memberCard,
			@RequestParam(value = "name", required = false, defaultValue = "") String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone ) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_MEMBER)){
			return new ObjectListResult("no_permission", false);
		}
		ObjectListResult result = null;
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			result = memberService.queryMember(name, memberCard, address, postCode, telephone);
		} else {
			result = memberCloudService.queryMember(name, memberCard, address, postCode, telephone);
		}
		return result;
	}

	/**
	 * 这个方法给付款界面用, 不要求权限
	 * @param userId
	 * @param memberCard
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/querymemberbycard", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult queryMemberByCard(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "memberCard", required = true) String memberCard) throws Exception{
		ObjectResult result = null;
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			result = memberService.queryMemberByCard(memberCard);
		} else {
			result = memberCloudService.queryMemberByCard(memberCard);
		}
		return result;
	}
	
	@RequestMapping(value = "/member/querymemberhazily", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult queryMember(
			@RequestParam(value = "key", required = true) String key) throws Exception{
		ObjectListResult result = null;
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			result = memberService.queryMemberHazily(key);
		} else {
			result = memberCloudService.queryMemberHazily(key);
		}
		return result;
	}
	
	@RequestMapping(value = "/member/queryallmember", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody ObjectListResult queryAllMember() throws Exception{
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.queryAllMember();
		} else {
			return memberCloudService.queryAllMember();
		}
	}
	
	@RequestMapping(value = "/member/querymemberscore", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMemberScore(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "memberId", required = true) int memberId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_MEMBER)){
			return new ObjectListResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.queryMemberScore(memberId);
		} else {
			return memberCloudService.queryMemberScore(memberId);
		}
	}
	
	@RequestMapping(value = "/member/querymemberbalance", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMemberBalance(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "memberId", required = true) int memberId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_MEMBER)){
			return new ObjectListResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.queryMemberBalance(memberId);
		} else {
			return memberCloudService.queryMemberBalance(memberId);
		}
	}
	
	@RequestMapping(value = "/member/querymemberrecharge", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMemberRecharge(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "startTime", required = false, defaultValue = "") String sStartTime,
			@RequestParam(value = "endTime", required = false, defaultValue = "") String sEndTime) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_MEMBER)){
			return new ObjectListResult("no_permission", false);
		}
		Date startTime = null;
		Date endTime = null;
		if (sStartTime != null && sStartTime.length() > 0){
			startTime = ConstantValue.DFYMDHMS.parse(sStartTime);
		}
		if (sEndTime != null && sEndTime.length() > 0){
			endTime = ConstantValue.DFYMDHMS.parse(sEndTime);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.queryMemberRecharge(startTime, endTime);
		} else {
			return memberCloudService.queryMemberRecharge(startTime, endTime);
		}
	}
	
	@RequestMapping(value = "/member/addmember", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult addMember(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "memberCard", required = true) String memberCard,
			@RequestParam(value = "name", required = true) String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone,
			@RequestParam(value = "password", required = false, defaultValue = "") String password,
			@RequestParam(value = "discountRate", required = true) double discountRate,
			@RequestParam(value = "birth", required = false, defaultValue = "") String sBirth) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBER)){
			return new ObjectResult("no_permission", false);
		}
		Date birth = null;
		if (sBirth != null && sBirth.length() > 0){
			birth = ConstantValue.DFYMD.parse(sBirth);
		}
		try{
			if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
				return memberService.addMember(userId, name, memberCard, address, postCode, telephone, birth, discountRate, password);
			} else {
				return memberCloudService.addMember(userId, name, memberCard, address, postCode, telephone, birth, discountRate, password);
			}
		} catch(Exception e){
			log.error(ConstantValue.DFYMDHMS.format(new Date()));
	        log.error("", e);
	        e.printStackTrace();
			return new ObjectResult(e.getMessage()+"\n"+e.getCause(), false);
		}
	}
	
	@RequestMapping(value = "/member/updatemember", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult updateMember(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "memberCard", required = true) String memberCard,
			@RequestParam(value = "name", required = true) String name, 
			@RequestParam(value = "address", required = false, defaultValue = "") String address, 
			@RequestParam(value = "postCode", required = false, defaultValue = "") String postCode, 
			@RequestParam(value = "telephone", required = false, defaultValue = "") String telephone,
			@RequestParam(value = "discountRate", required = false, defaultValue = "") double discountRate,
			@RequestParam(value = "birth", required = false, defaultValue = "") String sBirth) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBER)){
			return new ObjectResult("no_permission", false);
		}
		Date birth = null;
		if (sBirth != null && sBirth.length() > 0){
			birth = ConstantValue.DFYMD.parse(sBirth);
		}
		try{
			if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
				return memberService.updateMember(userId, id, name, memberCard, address, postCode, telephone, birth, discountRate);
			} else {
				return memberCloudService.updateMember(userId, id, name, memberCard, address, postCode, telephone, birth, discountRate);
			}
		} catch(Exception e){
			log.error(ConstantValue.DFYMDHMS.format(new Date()));
	        log.error("", e);
	        e.printStackTrace();
			return new ObjectResult(e.getMessage()+"\n"+e.getCause(), false);
		}
	}
	
	@RequestMapping(value = "/member/deletemember", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult deleteMember(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_DELETE_MEMBER)){
			return new ObjectResult("no_permission", false);
		}
		try{
			if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
				return memberService.deleteMember(userId, id);
			} else {
				return memberCloudService.deleteMember(userId, id);
			}
		} catch(Exception e){
			log.error(ConstantValue.DFYMDHMS.format(new Date()));
	        log.error("", e);
	        e.printStackTrace();
			return new ObjectResult(e.getMessage()+"\n"+e.getCause(), false);
		}
	}
	
	@RequestMapping(value = "/member/updatememberscore", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult updateMemberScore(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newScore", required = true) double newScore) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBERSCORE)){
			return new ObjectResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.updateMemberScore(userId, id, newScore);
		} else {
			return memberCloudService.updateMemberScore(userId, id, newScore);
		}
	}
	
	@RequestMapping(value = "/member/updatememberpassword", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult updateMemberPassword(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBERPASSWORD)){
			return new ObjectResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.updateMemberPassword(userId, id, oldPassword, newPassword);
		} else {
			return memberCloudService.updateMemberPassword(userId, id, oldPassword, newPassword);
		}
	}
	
	@RequestMapping(value = "/member/resetmemberpassword111111", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult resetMemberPassword111111(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBERPASSWORD)){
			return new ObjectResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.resetMemberPassword111111(userId, id);
		} else {
			return memberCloudService.resetMemberPassword111111(userId, id);
		}
	}
	
	@RequestMapping(value = "/member/updatememberbalance", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult updateMemberBalance(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newBalance", required = true) double newBalance) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBERBALANCE)){
			return new ObjectResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.updateMemberBalance(userId, id, newBalance);
		} else {
			return memberCloudService.updateMemberBalance(userId, id, newBalance);
		}
	}
	
	@RequestMapping(value = "/member/memberrecharge", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult memberRecharge(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "rechargeValue", required = true) double rechargeValue,
			@RequestParam(value = "payway", required = true) String payway) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_UPDATE_MEMBERBALANCE)){
			return new ObjectResult("no_permission", false);
		}
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			return memberService.memberRecharge(userId, id, rechargeValue, payway);
		} else {
			return memberCloudService.memberRecharge(userId, id, rechargeValue, payway);
		}
	}

	/**
	 * test the consuming time for insert 10000 records 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/member/test10000", method = {RequestMethod.GET})
	public @ResponseBody ObjectResult testInsert10000() throws Exception{
		long l1 = System.currentTimeMillis();
		String testaim = "test 10000 query different id.";
		log.debug("test 10000 insert start " + l1);
		ObjectResult result = null;
		if (ServerProperties.MEMBERLOCATION_LOCAL.equals(ServerProperties.MEMBERLOCATION)){
			result = memberService.testInsert10000();
		} else {
			result = memberCloudService.test10000();
		}
		log.debug(testaim + " use time " + (System.currentTimeMillis() - l1));
		return result;
	}
}
