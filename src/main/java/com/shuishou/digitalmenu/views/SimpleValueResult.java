package com.shuishou.digitalmenu.views;

public class SimpleValueResult extends GridResult {

	public String data;
	public SimpleValueResult(String result, boolean success, String simpleValue) {
		super(result, success);
		data = simpleValue;
	}

}
