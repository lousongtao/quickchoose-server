package com.shuishou.digitalmenu.indent.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetIndentDetailResult extends GridResult{

	public static final class IndentDetail{
		public int id;
		public int amount;
		public double dishPrice;//单个dish价格, 不考虑amount
		public String dishChineseName;
		public String dishEnglishName;
		public String additionalRequirements;
	}
	
	public List<IndentDetail> details;
	public GetIndentDetailResult(String result, boolean success,List<IndentDetail> details) {
		super(result, success);
		this.details = details;
	}

}
