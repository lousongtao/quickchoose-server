/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.log.views;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetLogsResult extends GridResult {

	/**
	 * @author zhing the log information.
	 */
	public final static class LogInfo {
		public final static DateFormat simpleDateFmt = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		public final long id;
		public final long userId;
		public final String username;
		public final String type;
		public final String time;
		public final String message;

		public LogInfo(long id, long userId, String username, String type, Date time, String message) {
			this.id = id;
			this.userId = userId;
			this.username = username;
			this.type = type;
			this.time = simpleDateFmt.format(time);
			this.message = message;
		}
	}

	/**
	 * the result.
	 */
	public final List<LogInfo> logs;

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
	public GetLogsResult(String result, boolean success, List<LogInfo> logs, int total) {
		super(result, success);
		this.logs = logs;
		this.total = total;
	}

}
