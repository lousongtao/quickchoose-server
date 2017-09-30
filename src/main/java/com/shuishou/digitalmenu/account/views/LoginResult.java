/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.views;

public class LoginResult {

	/**
	 * the result.
	 */
	private final String result;

	/**
	 * the user id.
	 */
	private final String userId;
	
	private final String userName;

	/**
	 * the session id.
	 */
	private final String sessionId;

	/**
	 * constructor.
	 * 
	 * @param result
	 *            the result.
	 * @param userId
	 *            the user id.
	 * @param sessionId
	 *            the session id.
	 */
	public LoginResult(String result, String userId, String userName, String sessionId) {
		this.result = result;
		this.userId = userId;
		this.sessionId = sessionId;
		this.userName = userName;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	public String getUserName() {
		return userName;
	}

	
}
