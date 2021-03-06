package com.shuishou.digitalmenu.indent.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class IndentDataAccessor extends BaseDataAccessor implements IIndentDataAccessor {

	@Override
	public Serializable save(Indent indent) {
		return sessionFactory.getCurrentSession().save(indent);
	}

	@Override
	public void update(Indent indent) {
		sessionFactory.getCurrentSession().update(indent);
	}

	@Override
	public void delete(Indent indent) {
		sessionFactory.getCurrentSession().delete(indent);
	}

	@Override
	public Indent getIndentById(int id) {
		String hql = "from Indent where id="+id;
		return (Indent) sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.uniqueResult();
	}

	@Override
	public List<Indent> getAllIndent() {
		String hql = "from Indent";
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}

	@Override
	public int getMaxSequenceToday() {
		//每天4点开始重新计数, 如果现在时间超过了12点, 则比较昨天4点, 否则比较今天4点
		Calendar c = Calendar.getInstance();
		int currenthour = c.get(Calendar.HOUR_OF_DAY);
		c.set(Calendar.HOUR_OF_DAY, 4);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		if (currenthour < 4){
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 1);
		}
		String hql = "select max(dailySequence) from Indent where startTime > :time";
		Object o = sessionFactory.getCurrentSession().createQuery(hql).setTimestamp("time", c.getTime()).uniqueResult();
		if (o == null)
			return 0;
		return Integer.parseInt(o.toString());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Indent> getIndents(int start, int limit, Date starttime, Date endtime, Byte[] status, 
			String deskname, List<String> orderBys, List<String> orderByDescs) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Indent.class);
		if (starttime != null)
			c.add(Restrictions.ge("startTime", starttime));
		if (endtime != null)
			c.add(Restrictions.le("startTime", endtime));
		if (status != null){
			c.add(Restrictions.in("status", status));
		} else {
			c.add(Restrictions.not(Restrictions.in("status", new Byte[]{0})));
		}
		if (deskname != null && deskname.length() > 0)
			c.add(Restrictions.eq("deskName", deskname));
		if (orderBys != null && !orderBys.isEmpty()){
			for (int i = 0; i < orderBys.size(); i++) {
				c.addOrder(Order.asc(orderBys.get(i)));
			}
		}
		if (orderByDescs != null && !orderByDescs.isEmpty()){
			for (int i = 0; i < orderByDescs.size(); i++) {
				c.addOrder(Order.desc(orderByDescs.get(i)));
			}
		}
		c.setFirstResult(start);
		c.setMaxResults(limit);
		return (List<Indent>)c.list();
	}
	
	@Override
	public int getIndentCount(Date starttime, Date endtime, Byte[] status, String deskname) {
		String countStmt = "select count(l) from Indent l";
		List<String> condList = Lists.newArrayList();
		if (starttime != null){
			condList.add("l.startTime >= :starttime");
		}
		if (endtime != null){
			condList.add("l.startTime <= :endtime");
		}
		if (status != null){
			condList.add("l.status in :status");
		} else {
			condList.add("l.status not in :nonstatus");
		}
		if (deskname != null && deskname.length() > 0){
			condList.add("l.deskName = :deskname");
		}
		for (int i = 0; i < condList.size(); i++) {
			countStmt += (i == 0 ? " where " : " and ") + condList.get(i);
		}
		Query query = sessionFactory.getCurrentSession().createQuery(countStmt);
		if (starttime != null){
			query.setTimestamp("starttime", starttime);
		}
		if (endtime != null){
			query.setTimestamp("endtime", endtime);
		}
		if (status != null){
			query.setParameterList("status", status);
		} else {
			query.setParameterList("nonstatus", new Byte[]{0});
		}
		if (deskname != null && deskname.length() > 0){
			query.setParameter("deskname", deskname);
		}
		return (int)(long)query.uniqueResult();
	}

	@Override
	public List<Indent> getUnpaidIndent() {
		String hql = "from Indent where status = " + ConstantValue.INDENT_STATUS_OPEN;
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}
	
	@Override
	public List<Indent> getUnpaidIndent(String deskName) {
		String hql = "from Indent where status = " + ConstantValue.INDENT_STATUS_OPEN + " and deskName='"+deskName+"'";
		return sessionFactory.getCurrentSession().createQuery(hql)
				.setCacheable(true)
				.list();
	}

	/**
	 * query the indent records which are paid between the period
	 * @param starttime cannot be null
	 * @param endtime cannot be null
	 */
	@Override
	public List<Indent> getIndentsByPaidTime(Date starttime, Date endtime) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(Indent.class);
		c.add(Restrictions.eq("status", ConstantValue.INDENT_STATUS_PAID));
		c.add(Restrictions.ge("endTime", starttime));
		c.add(Restrictions.le("endTime", endtime));
		return (List<Indent>)c
				.setCacheable(true)
				.list();
	}

}
