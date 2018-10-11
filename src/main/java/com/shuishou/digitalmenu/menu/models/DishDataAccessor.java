package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishDataAccessor extends BaseDataAccessor implements IDishDataAccessor {

	@Override
	public Serializable save(Dish dish) {
		return sessionFactory.getCurrentSession().save(dish);
	}

	@Override
	public void update(Dish dish) {
		sessionFactory.getCurrentSession().update(dish);
	}

	@Override
	public void delete(Dish dish) {
		sessionFactory.getCurrentSession().delete(dish);
	}

	@Override
	public Dish getDishById(int id) {
		String hql = "from Dish where id = "+ id;
		return (Dish) sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.uniqueResult();
	}

	public List<Dish> getDishesByParentId(int category2id){
		String hql = "from Dish where category2.id ="+category2id;
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}
	
	@Override
	public List<Dish> getAllDish(){
		String hql = "from Dish";
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}
}
