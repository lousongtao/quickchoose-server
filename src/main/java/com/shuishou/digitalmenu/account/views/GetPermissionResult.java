package com.shuishou.digitalmenu.account.views;

import java.util.List;

import com.shuishou.digitalmenu.views.GridResult;

public class GetPermissionResult extends GridResult{

	public final static class PermissionInfo{
		public String id;
		public String name;
		
		public PermissionInfo(String id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
	public List<PermissionInfo> permissions;
	
	public GetPermissionResult(String result, boolean success, List<PermissionInfo> permissions) {
		super(result, success);
		this.permissions = permissions;
	}

}
