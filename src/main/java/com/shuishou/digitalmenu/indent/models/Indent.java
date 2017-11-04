package com.shuishou.digitalmenu.indent.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.common.models.Desk;

@Entity
@Table(indexes = {@Index(name = "idx_starttime", columnList = "starttime")})
public class Indent {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name="desk_name")
	private String deskName;
	
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone="GMT+8:00")
	@Column(nullable = false)
	private Date startTime;
	
	@Column(name = "customer_amount", nullable = false)
	private int customerAmount;
	
	@JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone="GMT+8:00")
	@Column
	private Date endTime;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="indent")
	private List<IndentDetail> items;
	
	@Column(name = "total_price", nullable = false)
	private double totalPrice;
	
	@Column(name ="paid_price")
	private double paidPrice;//实际付款金额
	
	@Column(name = "pay_way")
	private String payWay;//付款方式
	
	@Column(name = "member_card")
	private String memberCard;
	
	@Column(nullable = false)
	private byte status = ConstantValue.INDENT_STATUS_OPEN;

	//订单序号, 每日从1开始,
	@Column(nullable = false, name="daily_sequence")
	private int dailySequence = -1;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerAmount() {
		return customerAmount;
	}

	public void setCustomerAmount(int customerAmount) {
		this.customerAmount = customerAmount;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public String getMemberCard() {
		return memberCard;
	}

	public void setMemberCard(String memberCard) {
		this.memberCard = memberCard;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date time) {
		this.startTime = time;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public double getPaidPrice() {
		return paidPrice;
	}

	public String getFormatPaidPrice(){
		return String.format("%.2f", paidPrice);
	}
	
	public void setPaidPrice(double paidPrice) {
		this.paidPrice = paidPrice;
	}

	public List<IndentDetail> getItems() {
		return items;
	}

	public void setItems(List<IndentDetail> items) {
		this.items = items;
	}
	
	public void addItem(IndentDetail detail){
		if (items == null)
			items = new ArrayList<IndentDetail>();
		items.add(detail);
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public String getFormatTotalPrice(){
		return String.format("%.2f", totalPrice);
	}
	
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	
	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Order [desk=" + deskName + ", totalPrice=" + totalPrice + "]";
	}

	
	public String getDeskName() {
		return deskName;
	}

	public void setDeskName(String deskName) {
		this.deskName = deskName;
	}

	public int getDailySequence() {
		return dailySequence;
	}

	public void setDailySequence(int dailySequence) {
		this.dailySequence = dailySequence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Indent other = (Indent) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
