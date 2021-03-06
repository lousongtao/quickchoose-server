/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.controllers;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.account.views.LoginResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Controller
public class AccountController extends BaseController {

	/**
	 * the logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(AccountController.class);

	/**
	 * the account service.
	 */
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;

	/**
	 * login.
	 * 
	 * @param username
	 *            the username.
	 * @param password
	 *            the password.
	 * @return the result.
	 * @throws Exception
	 */
	@RequestMapping(value = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody LoginResult login(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password) throws Exception {
		logger.debug(String.format(ConstantValue.DFYMDHMS.format(new Date()) + "\nLOGIN: %s, %s", username, password));
		return accountService.auth(username, password);
	}

	/**
	 * get accounts.
	 * 
	 * @param userId
	 *            the user id.
	 * @return the account list.
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/accounts", method = { RequestMethod.GET })
	public @ResponseBody ObjectListResult getAccountList(
			@RequestParam(value = "userId", required = true) long userId)
			throws Exception {
		
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_QUERY_USER)){
			return new ObjectListResult("no_permission", false, null);
		}

		return accountService.getAccounts();
	}
	
	@RequestMapping(value = "/account/queryaccount", method = { RequestMethod.GET })
	public @ResponseBody ObjectListResult getAccountList()
			throws Exception {
		return accountService.getAccounts();
	}

	/**
	 * change password.
	 * 
	 * @param userId
	 *            the user id.
	 * @param oldPassword
	 *            the old password.
	 * @param newPassword
	 *            the new password.
	 * @return the result.
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/change_password", method = { RequestMethod.POST })
	public @ResponseBody ObjectResult changePassword(@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "accountId", required = true) int accountId,
			@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword) throws Exception {
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CREATE_USER)){
			return new ObjectResult("no_permission", false);
		}
		return accountService.changePassword(userId, accountId, oldPassword, newPassword);
	}

	/**
	 * add account.
	 * 
	 * @param userId
	 *            the user id.
	 * @param password
	 *            the password of add user.
	 * @return the result.
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/add", method = { RequestMethod.POST })
	public @ResponseBody Result addAccount(@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "username", required = true, defaultValue = "") String username,
			@RequestParam(value = "password", required = true, defaultValue = "") String password,
			@RequestParam(value = "permission", required = true, defaultValue = "") String permission)
			throws Exception {
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CREATE_USER)){
			return new Result("no_permission");
		}
//		long userId = Long.parseLong(userIdStr);
//		long permGroupId = Long.parseLong(permGroupIdStr);
		return accountService.addAccount(userId, username, password, permission);
	}

	/**
	 * modify account.
	 * 
	 * @param userId : the operator's id
	 * @return the result.
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/modify", method = { RequestMethod.POST })
	public @ResponseBody Result modifyAccount(@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "id", required = true) long updateUserId,
			@RequestParam(value = "username", required = true, defaultValue = "") String username,
			@RequestParam(value = "permission", required = true, defaultValue = "") String permission)
			throws Exception {
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CREATE_USER)){
			return new Result("no_permission");
		}
//		long userId = Long.parseLong(userIdStr);
//		long permGroupId = Long.parseLong(permGroupIdStr);
		return accountService.modifyAccount(userId, updateUserId, username, permission);
	}

	/**
	 * remove account.
	 * 
	 * @param userId
	 *            the user id.
	 * @param idStr
	 *            the user id of remove user.
	 * @return the result.
	 * @throws Exception
	 */
	@RequestMapping(value = "/account/remove", method = { RequestMethod.POST })
	public @ResponseBody Result removeAccount(@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "id", required = true, defaultValue = "") String idStr) throws Exception {
		UserData user = accountService.getUserById(userId);
		if (user.getId() == Long.parseLong(idStr))
			return new Result("can_not_remove_self");
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_CREATE_USER)){
			return new Result("no_permission");
		}
//		long userId = Long.parseLong(userIdStr);
		long id = Long.parseLong(idStr);
		return accountService.removeAccount(userId, id);
	}

	
	@RequestMapping(value = "/account/querypermission", method={RequestMethod.GET})
	public @ResponseBody ObjectListResult queryPermission() throws Exception{
		return permissionService.queryAllPermissions();
	}

}
