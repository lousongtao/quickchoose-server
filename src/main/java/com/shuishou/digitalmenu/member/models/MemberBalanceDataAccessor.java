package com.shuishou.digitalmenu.member.models;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MemberBalanceDataAccessor extends BaseDataAccessor implements IMemberBalanceDataAccessor {

	@Override
	public List<MemberBalance> getMemberBalanceByMemberId(int memberId) {
		String hql = "select ms from MemberBalance ms where ms.member.id = " + memberId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public void save(MemberBalance mc) {
		sessionFactory.getCurrentSession().save(mc);
	}

	@Override
	public void delete(MemberBalance mc) {
		sessionFactory.getCurrentSession().delete(mc);
	}

	@Override
	public void deleteByMember(int memberId) {
		String hql = "delete from MemberBalance mc where mc.member.id = "+ memberId;
		sessionFactory.getCurrentSession().createQuery(hql).executeUpdate();
	}


}
