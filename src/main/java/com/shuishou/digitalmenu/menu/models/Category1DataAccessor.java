package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class Category1DataAccessor extends BaseDataAccessor implements ICategory1DataAccessor {

	

	@Override
	public Serializable save(Category1 category) {
		return sessionFactory.getCurrentSession().save(category);
	}

	@Override
	public void update(Category1 category) {
		sessionFactory.getCurrentSession().update(category);
	}

	@Override
	public void delete(Category1 category) {
		sessionFactory.getCurrentSession().delete(category);
	}

	@Override
	public Category1 getCategory1ById(int id) {
		String hql = "from Category1 where id="+id;
		return (Category1) sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.uniqueResult();
	}

	@Override
	public List<Category1> getAllCategory1() {
		String hql = "from Category1";
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}
}
