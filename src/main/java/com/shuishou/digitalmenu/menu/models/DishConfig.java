package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table
public class DishConfig implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "first_language_name", nullable = false)
	private String firstLanguageName;
	
	@Column(name = "second_language_name")
	private String secondLanguageName;
	
	@Column(nullable = false)
	private int sequence;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "group_id")
	private DishConfigGroup group;
	
	@Column(nullable = false, precision = 8, scale = 2)
	private double price;
	
	@Column
	private String pictureName;
	
	//spring mvc can change "isSoldOut" to "soldOut" automatically, so must use JsonProperty
	@JsonProperty("isSoldOut")
	@Column
	private boolean isSoldOut = false;
	
	/**
	 * 给管理员提供的标识名称, 因为不同的ConfigGroup, 在界面上显示的信息可能是相同的, 会导致管理员无法区分.
	 * 程序和数据库都不限制该字段的唯一性约束, 完全由系统管理员根据需要填写. 必填项
	 */
//	@Column(nullable = false)
//	private String uniqueName;
	

//	public String getUniqueName() {
//		return uniqueName;
//	}
//
//	public void setUniqueName(String uniqueName) {
//		this.uniqueName = uniqueName;
//	}

	public String getPictureName() {
		return pictureName;
	}

	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public DishConfigGroup getGroup() {
		return group;
	}

	public void setGroup(DishConfigGroup group) {
		this.group = group;
	}

	public boolean isSoldOut() {
		return isSoldOut;
	}

	public void setSoldOut(boolean isSoldOut) {
		this.isSoldOut = isSoldOut;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}


	public String getFirstLanguageName() {
		return firstLanguageName;
	}

	public void setFirstLanguageName(String firstLanguageName) {
		this.firstLanguageName = firstLanguageName;
	}

	public String getSecondLanguageName() {
		return secondLanguageName;
	}

	public void setSecondLanguageName(String secondLanguageName) {
		this.secondLanguageName = secondLanguageName;
	}

	@Override
	public String toString() {
		return "DishConfig [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		DishConfig other = (DishConfig) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
