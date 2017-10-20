package com.shuishou.digitalmenu.common.views;

import com.shuishou.digitalmenu.views.ObjectResult;

public class GetConfirmCodeResult extends ObjectResult{

	public String data;
	public GetConfirmCodeResult(String result, boolean success,String confirmCode) {
		super(result, success);
		this.data = confirmCode;
	}

}
