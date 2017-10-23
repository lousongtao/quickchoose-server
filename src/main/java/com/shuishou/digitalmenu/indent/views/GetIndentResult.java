package com.shuishou.digitalmenu.indent.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetIndentResult extends ObjectResult{

//	public static final class Indent{
//		public int id;
//		public String deskName;
//		public String startTime;
//		public String endTime;
//		public byte status;
//		public int dailySequence;
//		public double totalPrice;
//		public double paidPrice;
//		public byte payWay;
//		public int customerAmount;
//		public ArrayList<IndentDetail> items = new ArrayList<>();
//	}
//	
//	public static final class IndentDetail{
//		public int id;
//		public int dishId;
//		public int amount;
//		public double dishPrice;//单个dish价格, 不考虑amount
//		public String dishChineseName;
//		public String dishEnglishName;
//		public String additionalRequirements;
//	}
	
	public ArrayList<com.shuishou.digitalmenu.indent.models.Indent> data;
	public final int total;
	public GetIndentResult(String result, boolean success,ArrayList<com.shuishou.digitalmenu.indent.models.Indent> indents, int total) {
		super(result, success);
		this.data = indents;
		this.total = total;
	}

}
