package com.shuishou.digitalmenu.management.views;

import com.shuishou.digitalmenu.views.GridResult;

public class CurrentDutyResult extends GridResult {

	public String currentDuty;
	public CurrentDutyResult(String result, boolean success) {
		super(result, success);
	}

	public CurrentDutyResult(String result, boolean success, String currentDuty) {
		super(result, success);
		this.currentDuty = currentDuty;
	}
}
