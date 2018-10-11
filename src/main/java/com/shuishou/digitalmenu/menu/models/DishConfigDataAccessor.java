package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishConfigDataAccessor extends BaseDataAccessor implements IDishConfigDataAccessor {

	@Override
	public Serializable save(DishConfig dishConfig) {
		return sessionFactory.getCurrentSession().save(dishConfig);
	}

	@Override
	public void update(DishConfig dishConfig) {
		sessionFactory.getCurrentSession().update(dishConfig);
	}

	@Override
	public void delete(DishConfig dishConfig) {
		sessionFactory.getCurrentSession().delete(dishConfig);
	}

	@Override
	public DishConfig getDishConfigById(int id) {
		String hql = "from DishConfig where id="+id;
		return (DishConfig) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<DishConfig> getAllDishConfig() {
		String hql = "from DishConfig";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public List<DishConfig> getDishConfigByGroupId(int groupId) {
		String sql = "select * from DishConfig where groupId="+groupId;
		return sessionFactory.getCurrentSession().createQuery(sql).list();
	}
}
