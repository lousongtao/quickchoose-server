package com.shuishou.digitalmenu.validatelicense.models;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class ValidateLicenseHistoryDataAccessor extends BaseDataAccessor implements IValidateLicenseHistoryDataAccessor{

	@Override
	public ValidateLicenseHistory getLastRecord() {
		String hql = "from ValidateLicenseHistory order by id desc";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setMaxResults(1);
		return (ValidateLicenseHistory)query.uniqueResult();
	}

	@Override
	public void insert(ValidateLicenseHistory his) {
		sessionFactory.getCurrentSession().save(his);
	}

}
