package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishChooseSubitemDataAccessor extends BaseDataAccessor implements IDishChooseSubitemDataAccessor {

	@Override
	public Serializable save(DishChooseSubitem dishChooseSubitem) {
		return sessionFactory.getCurrentSession().save(dishChooseSubitem);
	}

	@Override
	public void update(DishChooseSubitem dishChooseSubitem) {
		sessionFactory.getCurrentSession().update(dishChooseSubitem);
	}

	@Override
	public void delete(DishChooseSubitem dishChooseSubitem) {
		sessionFactory.getCurrentSession().delete(dishChooseSubitem);
	}

	@Override
	public DishChooseSubitem getDishChooseSubitemById(int id) {
		String hql = "from DishChooseSubitem where id="+id;
		return (DishChooseSubitem) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<DishChooseSubitem> getAllDishChooseSubitem() {
		String hql = "from DishChooseSubitem";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
