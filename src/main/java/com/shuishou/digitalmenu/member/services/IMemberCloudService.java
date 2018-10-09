package com.shuishou.digitalmenu.member.services;

import java.util.Date;

import com.shuishou.digitalmenu.DataCheckException;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface IMemberCloudService {

	ObjectResult addMember(int userId, String name, String memberCard, String address, String postCode, String telephone, Date birth, double discountRate, String password);
	ObjectResult updateMember(int userId, int id, String name, String memberCard, String address, String postCode, String telephone, Date birth,double discountRate);
	ObjectResult updateMemberScore(int userId, int id, double newScore);
	ObjectResult updateMemberBalance(int userId, int id, double newBalance);
	ObjectResult updateMemberPassword(int userId, int id, String oldPassword, String newPassword);
	ObjectResult memberRecharge(int userId, int id, double newBalance, String payway);
	ObjectResult resetMemberPassword111111(int userId, int id);
	ObjectResult deleteMember(int userId, int id);
	ObjectResult recordMemberConsumption(String memberCard, String memberPassword, double consumptionPrice) throws DataCheckException;
	ObjectListResult queryMember(String name, String memberCard, String address, String postCode, String telephone);
	ObjectResult queryMemberByCard(String memberCard);
	ObjectListResult queryAllMember();
	ObjectListResult queryMemberBalance(int memberId);
	ObjectListResult queryMemberBalance(Date startTime, Date endTime, String type);
	ObjectListResult statMemberByTime(Date startTime, Date endTime);
	ObjectListResult queryMemberScore(int memberId);
	/**
	 * query member using a key
	 * key maybe is the name, the telephone, or the code
	 * @param key
	 * @return
	 */
	ObjectListResult queryMemberHazily(String key);
	ObjectResult test10000();
}
