/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.models;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository("sessionDA")
public class SessionDataAccessor extends BaseDataAccessor implements ISessionDataAccessor {

	@Override
	public void persistSession(SessionData session) {
		sessionFactory.getCurrentSession().persist(session);
	}

	@Override
	public void updateSession(SessionData session) {
		sessionFactory.getCurrentSession().update(session);
	}
	@Override
	public void deleteSession(SessionData session) {
		sessionFactory.getCurrentSession().delete(session);
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public Serializable saveSession(SessionData session) {
		return sessionFactory.getCurrentSession().save(session);
	}

	@Override
	public void saveOrUpdateSession(SessionData session) {
		sessionFactory.getCurrentSession().saveOrUpdate(session);
	}

	@Override
	public SessionData getSessionById(UUID id) {
		return (SessionData) sessionFactory.getCurrentSession().get(SessionData.class, id);
	}

	@Override
	public SessionData getSessionByUser(long userId) {
		String hql = "from SessionData where user_Id = " + userId;
		SessionData session = (SessionData) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
		
//		SessionData session = (SessionData) sessionFactory.getCurrentSession().getNamedQuery("getSessionByUser")
//				.setParameter("userId", userId).setCacheable(true).setCacheRegion("Query.Account").uniqueResult();
		return session;
	}

}
