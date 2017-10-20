package com.shuishou.digitalmenu.menu.views;

import java.util.Map;

import com.shuishou.digitalmenu.views.ObjectResult;

public class OperationResult extends ObjectResult {

	public Map data;
	public OperationResult(String result, boolean success, Map objectsInfo) {
		super(result, success);
		this.data = objectsInfo;
	}

}
