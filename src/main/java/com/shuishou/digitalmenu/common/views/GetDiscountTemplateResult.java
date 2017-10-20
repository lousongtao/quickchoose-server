package com.shuishou.digitalmenu.common.views;

import java.util.List;

import com.shuishou.digitalmenu.views.ObjectResult;

public class GetDiscountTemplateResult extends ObjectResult{

	public List<DiscountTemplate> data;
	
	public final static class DiscountTemplate{
		public int id;
		public String name;
		public double rate;
	}
	public GetDiscountTemplateResult(String result, boolean success) {
		super(result, success);
	}

}
