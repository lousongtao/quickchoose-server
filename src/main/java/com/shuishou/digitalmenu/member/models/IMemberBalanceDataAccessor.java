package com.shuishou.digitalmenu.member.models;

import java.util.Date;
import java.util.List;

public interface IMemberBalanceDataAccessor {

	List<MemberBalance> getMemberBalanceByMemberId(int memberId);
	
	List<MemberBalance> getMemberBalanceByDate(Date startTime, Date endTime);
	
	List<MemberBalance> queryMemberRecharge(Date startTime, Date endTime);
	
	void save(MemberBalance mc);
	
	void delete(MemberBalance mc);
	
	void deleteByMember(int memberId);
}
