package com.shuishou.digitalmenu.menu.views;

import java.util.Map;

import com.shuishou.digitalmenu.views.GridResult;

public class OperationResult extends GridResult {

	public Map infoMap;
	public OperationResult(String result, boolean success, Map objectsInfo) {
		super(result, success);
		this.infoMap = objectsInfo;
	}

}
