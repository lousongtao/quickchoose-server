package com.shuishou.digitalmenu.views;

public class SimpleValueResult extends GridResult {

	public String value;
	public SimpleValueResult(String result, boolean success, String simpleValue) {
		super(result, success);
		value = simpleValue;
	}

}
