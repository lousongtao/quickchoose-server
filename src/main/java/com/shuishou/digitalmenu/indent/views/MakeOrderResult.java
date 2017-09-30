package com.shuishou.digitalmenu.indent.views;

import com.shuishou.digitalmenu.views.GridResult;

public class MakeOrderResult extends GridResult {

	public int data;
	public MakeOrderResult(String result, boolean success, int sequence) {
		super(result, success);
		this.data = sequence;
	}

}
