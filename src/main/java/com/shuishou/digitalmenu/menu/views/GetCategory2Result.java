package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.views.GridResult;

public class GetCategory2Result extends GridResult {
	public static class Category2Info{
		public int id;
		public String chineseName;
		public String englishName;
		public int sequence;
		public int category1Id;
		public int printerId;
		public String level = ConstantValue.TYPE_CATEGORY2INFO;
		public Category2Info(int id, String chineseName,String englishName,int sequence,int category1Id,int printerId){
			this.id = id;
			this.chineseName = chineseName;
			this.englishName = englishName;
			this.sequence = sequence;
			this.category1Id = category1Id;
			this.printerId = printerId;
		}
	}
	
	public List categories;
	public GetCategory2Result(String result, boolean success, List infos) {
		super(result, success);
		this.categories = infos;
	}

}
