package com.shuishou.digitalmenu.account.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.models.IPermissionDataAccessor;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.Permission;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.account.models.UserPermission;
import com.shuishou.digitalmenu.account.views.GetPermissionResult;
import com.shuishou.digitalmenu.account.views.GetPermissionResult.PermissionInfo;
import com.shuishou.digitalmenu.views.Result;

@Service
public class PermissionService implements IPermissionService {

	@Autowired
	private IUserDataAccessor userDataAccessor;
	
	@Autowired
	private IPermissionDataAccessor permissionAccessor;
	
	@Override
	@Transactional
	public boolean checkPermission(long userId, String permission) {
		UserData user = userDataAccessor.getUserById(userId);
		List<UserPermission> userPermissions = user.getPermissions();
		for(UserPermission up : userPermissions){
			if (up.getPermission().getName().equals(permission))
				return true;
		}
		return false;
	}

	@Override
	@Transactional
	public GetPermissionResult queryAllPermissions() {
		List<Permission> permissions = permissionAccessor.queryAllPermission();
		List<PermissionInfo> pis = new ArrayList<PermissionInfo>();
		for(Permission p : permissions){
			pis.add(new PermissionInfo(p.getId()+"", p.getName()));
		}
		return new GetPermissionResult(Result.OK, true, pis);
	}

}
