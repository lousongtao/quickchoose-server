/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetAccountsResult extends GridResult {

	public final static class AccountInfo {
		public final String id;
		public final String username;
		public final String permission;

		public AccountInfo(String id, String username,String permission) {
			this.id = id;
			this.username = username;
			this.permission = permission;
		}
	}

	/**
	 * the result.
	 */
	public final List<AccountInfo> accounts;

	/**
	 * the total count.
	 */
	public final int total;

	/**
	 * the constructor.
	 * 
	 * @param accounts
	 *            the account list.
	 * @param total
	 *            the total count.
	 */
	public GetAccountsResult(String result, boolean success, List<AccountInfo> accounts, int total) {
		super(result, success);
		this.accounts = accounts;
		this.total = total;
	}

}
