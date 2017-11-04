package com.shuishou.digitalmenu.account.services;

import com.shuishou.digitalmenu.views.ObjectListResult;

public interface IPermissionService {
	public boolean checkPermission(long userId, String permission);
	
	public ObjectListResult queryAllPermissions();
}
