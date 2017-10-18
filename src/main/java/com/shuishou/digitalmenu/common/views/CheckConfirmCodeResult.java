package com.shuishou.digitalmenu.common.views;

import com.shuishou.digitalmenu.views.ObjectResult;

public class CheckConfirmCodeResult extends ObjectResult {
	public boolean data;

	public CheckConfirmCodeResult(String result, boolean success, boolean isRight) {
		super(result, success);
		this.data = isRight;
	}

}
