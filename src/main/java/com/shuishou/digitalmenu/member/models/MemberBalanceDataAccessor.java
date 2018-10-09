package com.shuishou.digitalmenu.member.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.ConstantValue;
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

	@Override
	public List<MemberBalance> getMemberBalanceByDate(Date startTime, Date endTime) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MemberBalance.class);
		c.add(Restrictions.eq("type", ConstantValue.MEMBERDEPOSIT_RECHARGE));
		if (startTime != null)
			c.add(Restrictions.ge("date", startTime));
		if (endTime != null)
			c.add(Restrictions.le("date", endTime));
		
		return c.list();
	}

	@Override
	public List<MemberBalance> queryMemberBalance(Date startTime, Date endTime, String type) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(MemberBalance.class);
		ArrayList<Integer> types = new ArrayList<>(); 
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_ADJUST) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_ADJUST);
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_RECHARGE) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_RECHARGE);
		if (type.indexOf(ConstantValue.MEMBERBALANCE_QUERYTYPE_CONSUME) >= 0)
			types.add(ConstantValue.MEMBERDEPOSIT_CONSUM);
		c.add(Restrictions.in("type", types));
		if (startTime != null)
			c.add(Restrictions.ge("date", startTime));
		if (endTime != null)
			c.add(Restrictions.le("date", endTime));
		
		return c.list();
	}
}
