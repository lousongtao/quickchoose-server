package com.shuishou.digitalmenu.account.services;

import com.shuishou.digitalmenu.account.views.GetPermissionResult;

public interface IPermissionService {
	public boolean checkPermission(long userId, String permission);
	
	public GetPermissionResult queryAllPermissions();
}
