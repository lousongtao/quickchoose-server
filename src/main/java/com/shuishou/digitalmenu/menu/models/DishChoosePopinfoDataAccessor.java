package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishChoosePopinfoDataAccessor extends BaseDataAccessor implements IDishChoosePopinfoDataAccessor {

	@Override
	public Serializable save(DishChoosePopinfo dishChoosePopinfo) {
		return sessionFactory.getCurrentSession().save(dishChoosePopinfo);
	}

	@Override
	public void update(DishChoosePopinfo dishChoosePopinfo) {
		sessionFactory.getCurrentSession().update(dishChoosePopinfo);
	}

	@Override
	public void delete(DishChoosePopinfo dishChoosePopinfo) {
		sessionFactory.getCurrentSession().delete(dishChoosePopinfo);
	}

	@Override
	public DishChoosePopinfo getDishChoosePopinfoById(int id) {
		String hql = "from DishChoosePopinfo where id="+id;
		return (DishChoosePopinfo) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<DishChoosePopinfo> getAllDishChoosePopinfo() {
		String hql = "from DishChoosePopinfo";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
