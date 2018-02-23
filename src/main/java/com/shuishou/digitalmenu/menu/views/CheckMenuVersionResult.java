package com.shuishou.digitalmenu.menu.views;

import java.util.List;
import java.util.Map;

import com.shuishou.digitalmenu.views.ObjectResult;

public class CheckMenuVersionResult extends ObjectResult {

	public static class MenuVersionInfo{
		public int id;
		public int objectId;
		public int type;
	}
	public List<MenuVersionInfo> data;
	public CheckMenuVersionResult(String result, boolean success, List<MenuVersionInfo> infos) {
		super(result, success);
		this.data = infos;
	}

}
