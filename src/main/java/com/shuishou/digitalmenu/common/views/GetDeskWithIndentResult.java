package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.indent.models.Indent;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetDeskWithIndentResult extends ObjectResult{

	public List<DeskWithIndent> data;
	
	public final static class DeskWithIndent{
		public int id;
		public String name;
		public Indent indent;
//		public int customerAmount; //顾客人数
//		public String startTime;
//		public double price;//订单价格
		public String mergeTo;
	}
	public GetDeskWithIndentResult(String result, boolean success, List<DeskWithIndent> desks) {
		super(result, success);
		this.data = desks;
	}

}
