package com.shuishou.digitalmenu.member.models;

import java.util.Date;
import java.util.List;

public interface IMemberBalanceDataAccessor {

	List<MemberBalance> getMemberBalanceByMemberId(int memberId);
	
	void save(MemberBalance mc);
	
	void delete(MemberBalance mc);
	
	void deleteByMember(int memberId);
}
