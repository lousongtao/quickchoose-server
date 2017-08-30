package com.shuishou.digitalmenu.management.views;

import java.util.Date;
import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class ShiftWorkResult extends GridResult {

	public final static class ShiftWork{
		public int id;
		public String userName;
		public String startTime;
		public String endTime;
	}
	
	public List<ShiftWork> shiftWorks;
	public ShiftWorkResult(String result, boolean success) {
		super(result, success);
	}

}
