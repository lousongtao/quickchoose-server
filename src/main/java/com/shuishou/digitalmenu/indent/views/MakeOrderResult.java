package com.shuishou.digitalmenu.indent.views;

import com.shuishou.digitalmenu.views.ObjectResult;

public class MakeOrderResult extends ObjectResult {

	public int data;
	public MakeOrderResult(String result, boolean success, int sequence) {
		super(result, success);
		this.data = sequence;
	}

}
