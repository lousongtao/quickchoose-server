package com.shuishou.digitalmenu.common.views;

import com.shuishou.digitalmenu.views.GridResult;

public class CheckConfirmCodeResult extends GridResult {
	public boolean data;

	public CheckConfirmCodeResult(String result, boolean success, boolean isRight) {
		super(result, success);
		this.data = isRight;
	}

}
