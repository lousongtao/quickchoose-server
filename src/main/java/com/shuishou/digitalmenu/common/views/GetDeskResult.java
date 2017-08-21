package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetDeskResult extends GridResult{

	public List<Desk> desks;
	
	public final static class Desk{
		public int id;
		public String name;
		public Desk(int id, String name){
			this.id = id;
			this.name = name;
		}
	}
	public GetDeskResult(String result, boolean success) {
		super(result, success);
	}

}
