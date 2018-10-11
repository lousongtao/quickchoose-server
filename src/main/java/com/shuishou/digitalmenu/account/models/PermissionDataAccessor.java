package com.shuishou.digitalmenu.account.models;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class PermissionDataAccessor extends BaseDataAccessor implements IPermissionDataAccessor {

	@Override
	public List<Permission> queryAllPermission() {
		String hql = "from Permission";
		return (List<Permission>)sessionFactory.getCurrentSession().createQuery(hql).list();
	}

}
