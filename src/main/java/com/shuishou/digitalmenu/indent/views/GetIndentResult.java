package com.shuishou.digitalmenu.indent.views;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.shuishou.digitalmenu.common.models.Desk;
import com.shuishou.digitalmenu.views.GridResult;

public class GetIndentResult extends GridResult{

	public static final class Indent{
		public int id;
		public String deskname;
		public String startTime;
		public String endTime;
		public byte status;
		public int dailysequence;
		public double totalprice;
		public double paidPrice;
		public byte payWay;
		public int customerAmount;
	}
	
	public List<Indent> indents;
	public final int total;
	public GetIndentResult(String result, boolean success,List<Indent> indents, int total) {
		super(result, success);
		this.indents = indents;
		this.total = total;
	}

}
