package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetCategory1Result extends ObjectResult {
	public static class Category1Info{
		public int id;
		public String chineseName;
		public String englishName;
		public int sequence;
		public String level = ConstantValue.TYPE_CATEGORY1INFO;
		public Category1Info(int id, String chineseName,String englishName,int sequence){
			this.id = id;
			this.chineseName = chineseName;
			this.englishName = englishName;
			this.sequence = sequence;
		}
	}
	
	public List data;
	public GetCategory1Result(String result, boolean success, List infos) {
		super(result, success);
		this.data = infos;
	}
}
