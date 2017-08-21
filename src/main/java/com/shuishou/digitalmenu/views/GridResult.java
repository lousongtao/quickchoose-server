/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.views;

public class GridResult extends Result {

	/**
	 * success or not.
	 */
	public final boolean success;

	/**
	 * the constructor.
	 * 
	 * @param result
	 *            the result.
	 * @param success
	 *            success or not.
	 */
	public GridResult(String result, boolean success) {
		super(result);
		this.success = success;
	}

}
