package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class FlavorDataAccessor extends BaseDataAccessor implements IFlavorDataAccessor {

	

	@Override
	public Serializable save(Flavor flavor) {
		return sessionFactory.getCurrentSession().save(flavor);
	}

	@Override
	public void update(Flavor flavor) {
		sessionFactory.getCurrentSession().update(flavor);
	}

	@Override
	public void delete(Flavor flavor) {
		sessionFactory.getCurrentSession().delete(flavor);
	}

	@Override
	public Flavor getFlavorById(int id) {
		String hql = "from Flavor where id="+id;
		return (Flavor) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<Flavor> getAllFlavor() {
		String hql = "from Flavor";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
