package com.shuishou.digitalmenu.statistics.views;

import java.util.Date;

import com.shuishou.digitalmenu.ConstantValue;


public class StatItem {
	public String itemName;
	public double totalPrice;
	public double paidPrice;
	public int soldAmount;
	public double weight;
	public Date startTime;//这个统计单元的时间范围
	public Date endTime;
	public StatItem(Date startTime, Date endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		itemName = ConstantValue.DFYMD.format(startTime) + " - " + ConstantValue.DFYMD.format(endTime);
	}
	public StatItem(String itemName){
		this.itemName = itemName;
	}
}
