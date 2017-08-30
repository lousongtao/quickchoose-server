package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetDeskWithIndentResult extends GridResult{

	public List<Desk> desks;
	
	public final static class Desk{
		public int id;
		public String name;
		public long indentId;
		public int customerAmount; //顾客人数
		public String startTime;
		public double price;//订单价格
	}
	public GetDeskWithIndentResult(String result, boolean success, List<Desk> desks) {
		super(result, success);
		this.desks = desks;
	}

}
