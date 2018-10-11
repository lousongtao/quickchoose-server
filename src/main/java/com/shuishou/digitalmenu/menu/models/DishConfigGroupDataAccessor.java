package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishConfigGroupDataAccessor extends BaseDataAccessor implements IDishConfigGroupDataAccessor {

	@Override
	public Serializable save(DishConfigGroup dishConfigGroup) {
		return sessionFactory.getCurrentSession().save(dishConfigGroup);
	}

	@Override
	public void update(DishConfigGroup dishConfigGroup) {
		sessionFactory.getCurrentSession().update(dishConfigGroup);
	}

	@Override
	public void delete(DishConfigGroup dishConfigGroup) {
		sessionFactory.getCurrentSession().delete(dishConfigGroup);
	}

	@Override
	public DishConfigGroup getDishConfigGroupById(int id) {
		String hql = "from DishConfigGroup where id="+id;
		return (DishConfigGroup) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<DishConfigGroup> getAllDishConfigGroup() {
		String hql = "from DishConfigGroup";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public List<DishConfigGroup> getDishConfigGroupByDishId(int dishId) {
		String sql = "select * from DishConfigGroup where dishId="+dishId;
		return sessionFactory.getCurrentSession().createQuery(sql).list();
	}
}
