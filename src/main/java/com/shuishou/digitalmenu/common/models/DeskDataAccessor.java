package com.shuishou.digitalmenu.common.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class DeskDataAccessor extends BaseDataAccessor implements IDeskDataAccessor {

	@Override
	public List queryDesks() {
		return sessionFactory.getCurrentSession().createQuery("from Desk order by sequence").list();
	}

	@Override
	public Serializable insertDesk(Desk desk) {
		return sessionFactory.getCurrentSession().save(desk);
	}

	@Override
	public void updateDesk(Desk desk) {
		sessionFactory.getCurrentSession().update(desk);
	}

	@Override
	public void deleteDesk(Desk desk) {
		sessionFactory.getCurrentSession().delete(desk);
	}

	@Override
	public Desk getDeskById(int id) {
		String hql = "from Desk where id = "+ id;
		
		return (Desk) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public Desk getDeskByName(String name) {
		String hql = "from Desk where name = '"+ name+"'";
		
		return (Desk) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}
}
