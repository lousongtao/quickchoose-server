package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DishMaterialConsumeDataAccessor extends BaseDataAccessor implements IDishMaterialConsumeDataAccessor {

	@Override
	public Serializable save(DishMaterialConsume dishMaterialConsume) {
		return sessionFactory.getCurrentSession().save(dishMaterialConsume);
	}

	@Override
	public void update(DishMaterialConsume dishMaterialConsume) {
		sessionFactory.getCurrentSession().update(dishMaterialConsume);
	}

	@Override
	public void delete(DishMaterialConsume dishMaterialConsume) {
		sessionFactory.getCurrentSession().delete(dishMaterialConsume);
	}

	@Override
	public DishMaterialConsume getDishMaterialConsumeById(int id) {
		String hql = "from DishMaterialConsume where id="+id;
		return (DishMaterialConsume) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<DishMaterialConsume> getAllDishMaterialConsume() {
		String hql = "from DishMaterialConsume";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public List<DishMaterialConsume> getDishMaterialConsumeByDishId(int dishId) {
		String hql = "from DishMaterialConsume where dish.id="+dishId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
	
	@Override
	public List<DishMaterialConsume> getDishMaterialConsumeByMaterialId(int materialId) {
		String hql = "from DishMaterialConsume where material.id="+ materialId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
