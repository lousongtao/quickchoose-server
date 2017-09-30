package com.shuishou.digitalmenu.management.models;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class ShiftWorkDataAccessor extends BaseDataAccessor implements IShiftWorkDataAccessor {

	@Override
	public ShiftWork getLastShiftWork() {
		String hql = "from ShiftWork l where l.id = (select max(t.id) from ShiftWork t)";
		return (ShiftWork) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}
	
	public void insertShitWork(ShiftWork sw){
		sessionFactory.getCurrentSession().save(sw);
	}
	
	public void save(ShiftWork sw){
		sessionFactory.getCurrentSession().save(sw);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ShiftWork> queryShiftWork(int start, int limit, String shiftName, Date startTime, Date endTime) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(ShiftWork.class);
		if (shiftName != null){
			c.add(Restrictions.ilike("userName", shiftName));
		}
		if (startTime != null){
			c.add(Restrictions.ge("endTime", startTime));
		}
		if (endTime != null){
			c.add(Restrictions.le("startTime", endTime));
		}
		c.addOrder(Order.asc("id"));
		c.setFirstResult(start);
		c.setMaxResults(limit);
		return (List<ShiftWork>)c.list();
	}

	@Override
	public ShiftWork getShiftWorkById(int shiftWorkId) {
		String hql = "from ShiftWork l where l.id = " + shiftWorkId;
		return (ShiftWork) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

}
