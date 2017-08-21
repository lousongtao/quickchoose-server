package com.shuishou.digitalmenu.menu.views;

import java.util.List;
import java.util.Map;

import com.shuishou.digitalmenu.views.GridResult;

public class CheckMenuVersionResult extends GridResult {

	public static class MenuVersionInfo{
		public int id;
		public int dishId;
		public int type;
	}
	public List<MenuVersionInfo> infos;
	public CheckMenuVersionResult(String result, boolean success, List<MenuVersionInfo> infos) {
		super(result, success);
		this.infos = infos;
	}

}
