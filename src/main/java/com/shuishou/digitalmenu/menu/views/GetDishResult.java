package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetDishResult extends ObjectResult {
	public static class DishInfo{
		public int id;
		public String chineseName;
		public String englishName;
		public int sequence;
		public int category2Id;
		public double price;
		public boolean isSoldOut;
		public String level = ConstantValue.TYPE_DISHINFO;
	}
	
	public List data;
	public GetDishResult(String result, boolean success, List infos) {
		super(result, success);
		this.data = infos;
	}

}
