package com.shuishou.digitalmenu.indent.views;

import java.util.ArrayList;

import com.shuishou.digitalmenu.views.ObjectResult;

public class OperateIndentResult extends ObjectResult {
	public static final class Indent{
		public int id;
		public String deskName;
		public String startTime;
		public int dailySequence;
		public int customerAmount;
		public ArrayList<IndentDetail> items = new ArrayList<>();
		public double totalPrice;
	}
	
	public static final class IndentDetail{
		public int id;
		public int dishId;
		public int amount;
		public double weight;
		public double dishPrice;//单个dish价格, 不考虑amount
		public String dishFirstLanguageName;
		public String dishSecondLanguageName;
		public String additionalRequirements;
	}

	public Indent data;
	public OperateIndentResult(String result, boolean success) {
		super(result, success);
		// TODO Auto-generated constructor stub
	}

}
