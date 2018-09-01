package com.shuishou.digitalmenu.member.views;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuishou.digitalmenu.ConstantValue;

public class MemberBalanceInfo {

	public int id;
	
	public double amount;
	
	public double newValue;
	
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
	public Date date;
	
	public String place;
	
	public int type;
	
	public String payway;
	
	public int memberId;
	
	public String memberCard;
	
	public String memberName;
}
