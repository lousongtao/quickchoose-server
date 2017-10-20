package com.shuishou.digitalmenu.indent.views;

import java.util.List;

import com.shuishou.digitalmenu.views.ObjectResult;

public class GetIndentDetailResult extends ObjectResult{

	public static final class IndentDetail{
		public int id;
		public int amount;
		public double dishPrice;//单个dish价格, 不考虑amount
		public String dishChineseName;
		public String dishEnglishName;
		public String additionalRequirements;
	}
	
	public List<IndentDetail> data;
	public GetIndentDetailResult(String result, boolean success,List<IndentDetail> details) {
		super(result, success);
		this.data = details;
	}

}
