package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MenuVersionDataAccessor extends BaseDataAccessor implements IMenuVersionDataAccessor {

	@Override
	public Serializable save(MenuVersion mv) {
		return sessionFactory.getCurrentSession().save(mv);
	}

	@Override
	public MenuVersion getMenuVersionById(int id){
		String hql = "from MenuVersion where id="+id;
		return (MenuVersion) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<MenuVersion> getMenuVersionFromId(int id){
		String hql = "from MenuVersion where id > "+ id;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public MenuVersion getLastRecord() {
		String hql = "from MenuVersion as mv where mv.id = (select max(mv1.id) from MenuVersion mv1)";
		return (MenuVersion) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}
	
	

}
