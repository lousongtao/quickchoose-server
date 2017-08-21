package com.shuishou.digitalmenu.common.models;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class ConfirmCodeDataAccessor extends BaseDataAccessor implements IConfirmCodeDataAccessor {

	@Override
	public ConfirmCode getCode() {
		Query query = sessionFactory.getCurrentSession().createQuery("from ConfirmCode");
		List codes = query.list();
		if (codes == null || codes.isEmpty())
			return null;
		return (ConfirmCode)codes.get(0);
	}

	@Override
	public void saveCode(ConfirmCode code) {
		sessionFactory.getCurrentSession().save(code);
	}

	@Override
	public void deleteCode() {
		sessionFactory.getCurrentSession().createQuery("delete from ConfirmCode").executeUpdate();
	}

}
