package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.views.GridResult;

public class GetDishResult extends GridResult {
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
	
	public List dishes;
	public GetDishResult(String result, boolean success, List infos) {
		super(result, success);
		this.dishes = infos;
	}

}
