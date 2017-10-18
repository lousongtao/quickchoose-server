package com.shuishou.digitalmenu.menu.views;

import java.util.List;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetMenuResult2 extends ObjectResult {

	
	
	
	
	public List data;
	public GetMenuResult2(String result, boolean success, List infos) {
		super(result, success);
		this.data = infos;
	}

}
