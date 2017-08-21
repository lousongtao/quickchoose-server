package com.shuishou.digitalmenu.common.views;

import com.shuishou.digitalmenu.views.GridResult;

public class GetConfirmCodeResult extends GridResult{

	public String confirmCode;
	public GetConfirmCodeResult(String result, boolean success,String confirmCode) {
		super(result, success);
		this.confirmCode = confirmCode;
	}

}
