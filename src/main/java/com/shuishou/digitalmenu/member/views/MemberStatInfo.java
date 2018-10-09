package com.shuishou.digitalmenu.member.views;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuishou.digitalmenu.ConstantValue;

public class MemberStatInfo {
	private String memberCard;
	private String memberName;
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
	private Date memberCreateTime;
	private double balance;
	private double recharge;
	private double adjust;
	private double consume;
	private int lastMemberBalanceId;//用于比较memberbalance记录, 如果memberBalance的ID大于该值, 则使用新的balance覆盖本对象的balance值
	public MemberStatInfo(String memberCard, String memberName, Date memberCreateTime){
		this.memberCard = memberCard;
		this.memberName = memberName;
		this.memberCreateTime = memberCreateTime;
	}
	
	public MemberStatInfo(String memberCard, String memberName, double balance, double recharge, double adjust, double consume){
		this.memberCard = memberCard;
		this.memberName = memberName;
		this.balance = balance;
		this.recharge = recharge;
		this.adjust = adjust;
		this.consume = consume;
	}
	
	public void addRecharge(double r){
		recharge += r;
	}
	
	public void addAdjust(double a){
		adjust += a;
	}
	
	public void addConsume(double c){
		consume += c;
	}

	public Date getMemberCreateTime() {
		return memberCreateTime;
	}

	public void setMemberCreateTime(Date memberCreateTime) {
		this.memberCreateTime = memberCreateTime;
	}

	public String getMemberCard() {
		return memberCard;
	}

	public void setMemberCard(String memberCard) {
		this.memberCard = memberCard;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getRecharge() {
		return recharge;
	}

	public void setRecharge(double recharge) {
		this.recharge = recharge;
	}

	public double getAdjust() {
		return adjust;
	}

	public void setAdjust(double adjust) {
		this.adjust = adjust;
	}

	public double getConsume() {
		return consume;
	}

	public void setConsume(double consume) {
		this.consume = consume;
	}

	public int getLastMemberBalanceId() {
		return lastMemberBalanceId;
	}

	public void setLastMemberBalanceId(int lastMemberBalanceId) {
		this.lastMemberBalanceId = lastMemberBalanceId;
	}

	@Override
	public String toString() {
		return "MemberStatInfo [memberCard=" + memberCard + ", memberName=" + memberName + ", balance=" + balance
				+ ", recharge=" + recharge + ", adjust=" + adjust + ", consume=" + consume + ", lastMemberBalanceId="
				+ lastMemberBalanceId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + lastMemberBalanceId;
		result = prime * result + ((memberCard == null) ? 0 : memberCard.hashCode());
		result = prime * result + ((memberName == null) ? 0 : memberName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemberStatInfo other = (MemberStatInfo) obj;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (lastMemberBalanceId != other.lastMemberBalanceId)
			return false;
		if (memberCard == null) {
			if (other.memberCard != null)
				return false;
		} else if (!memberCard.equals(other.memberCard))
			return false;
		if (memberName == null) {
			if (other.memberName != null)
				return false;
		} else if (!memberName.equals(other.memberName))
			return false;
		return true;
	}

	
	
}
