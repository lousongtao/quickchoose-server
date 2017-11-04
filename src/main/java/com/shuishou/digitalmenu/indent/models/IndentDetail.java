package com.shuishou.digitalmenu.indent.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
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
	
	@Column(nullable = false, name="dish_price")
	private double dishPrice;//单个dish价格, 不考虑amount
	
	@Column(nullable = false, name="dish_chinesename")
	private String dishChineseName;
	
	@Column(nullable = false, name="dish_englishname")
	private String dishEnglishName;
	
	@Column(name="additional_requirements")
	private String additionalRequirements;
	
	@Column
	private double weight;
	
	

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

	public String getDishChineseName() {
		return dishChineseName;
	}

	public void setDishChineseName(String dishChineseName) {
		this.dishChineseName = dishChineseName;
	}

	public String getDishEnglishName() {
		return dishEnglishName;
	}

	public void setDishEnglishName(String dishEnglishName) {
		this.dishEnglishName = dishEnglishName;
	}

	
	public String getAdditionalRequirements() {
		return additionalRequirements;
	}

	public void setAdditionalRequirements(String additionalRequirements) {
		this.additionalRequirements = additionalRequirements;
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
		return "OrderDetail [amount=" + amount + ", dishChineseName=" + dishChineseName + "]";
	}
	
	
}
