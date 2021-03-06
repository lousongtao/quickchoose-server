package com.shuishou.digitalmenu.member.models;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MemberScoreDataAccessor extends BaseDataAccessor implements IMemberScoreDataAccessor {

	@Override
	public List<MemberScore> getMemberScoreByMemberId(int memberId) {
		String hql = "select ms from MemberScore ms where ms.member.id = " + memberId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public void save(MemberScore ms) {
		sessionFactory.getCurrentSession().save(ms);
	}

	@Override
	public void delete(MemberScore ms) {
		sessionFactory.getCurrentSession().delete(ms);
	}

	@Override
	public void deleteByMember(int memberId) {
		String hql  = "delete from MemberScore where member.id = "+ memberId;
		sessionFactory.getCurrentSession().createQuery(hql).executeUpdate();
	}


}
