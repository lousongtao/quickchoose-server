package com.shuishou.digitalmenu.indent.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.common.models.Desk;

@Entity
@Table(indexes = {@Index(name = "idx_time", columnList = "time")})
public class Indent {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name="desk_name")
	private String deskName;
	
	@Column(nullable = false)
	private Date time;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="indent_id")
	private List<IndentDetail> items;
	
	@Column(name = "total_price", nullable = false)
	private double totalPrice;
	
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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
