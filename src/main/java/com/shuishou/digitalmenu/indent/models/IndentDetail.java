package com.shuishou.digitalmenu.indent.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuishou.digitalmenu.ConstantValue;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class IndentDetail {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@JsonIgnore
	@ManyToOne
	private Indent indent;
	
	@Column(nullable = false, name="dish_id")
	private int dishId;
	
	@Column(nullable = false)
	private int amount;
	
	@Column(nullable = false, name="dish_price", precision = 8, scale = 2)
	private double dishPrice;//单个dish价格, 不考虑amount, 如果有DishConfig影响了价格, 这里记录改变之后的价格
	
	/**
	 * 调整价格, 由于用户选择了某些DishConfig, 导致的价格变化. 该值可正可负
	 * 该价格跟dishPrice并列. 在最后计算整单价格时, 应该使用dishPrice+adjustPrice;
	 * 调整价格只跟份数有关, 如果是按重量购买的, 则订单总价为weight*dishPrice + adjustPrice, 即该价格只使用一次, 不考虑重量.
	 */
//	@Column(nullable = false)
//	private double adjustPrice;
	
	@Column(nullable = false, name="dish_firstlanguagename")
	private String dishFirstLanguageName;
	
	@Column(name="dish_secondlanguagename")
	private String dishSecondLanguageName;
	
	@Column(name="additional_requirements")
	private String additionalRequirements;
	
	@Column(precision = 8, scale = 2)
	private double weight;
	
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone="GMT+8:00")
	@Column
	private Date time;
	
	
	@Column
	private String operator;//记录操作员名称, waiter的名称
	

//	public double getAdjustPrice() {
//		return adjustPrice;
//	}
//
//	public void setAdjustPrice(double adjustPrice) {
//		this.adjustPrice = adjustPrice;
//	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Indent getIndent() {
		return indent;
	}

	public void setIndent(Indent indent) {
		this.indent = indent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getDishPrice() {
		return dishPrice;
	}

	public void setDishPrice(double dishPrice) {
		this.dishPrice = dishPrice;
	}

	public String getAdditionalRequirements() {
		return additionalRequirements;
	}

	public void setAdditionalRequirements(String additionalRequirements) {
		this.additionalRequirements = additionalRequirements;
	}

	public String getDishFirstLanguageName() {
		return dishFirstLanguageName;
	}

	public void setDishFirstLanguageName(String dishFirstLanguageName) {
		this.dishFirstLanguageName = dishFirstLanguageName;
	}

	public String getDishSecondLanguageName() {
		return dishSecondLanguageName;
	}

	public void setDishSecondLanguageName(String dishSecondLanguageName) {
		this.dishSecondLanguageName = dishSecondLanguageName;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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
		IndentDetail other = (IndentDetail) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndentDetail [amount=" + amount + ", dishFirstLanguageName=" + dishFirstLanguageName + "]";
	}

	
}
