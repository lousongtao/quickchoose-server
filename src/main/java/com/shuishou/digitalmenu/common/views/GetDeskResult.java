package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetDeskResult extends GridResult{

	public List<Desk> data;
	
	public final static class Desk{
		public int id;
		public String name;
		public int sequence;
		public String mergeTo;
	}
	public GetDeskResult(String result, boolean success) {
		super(result, success);
	}

}
