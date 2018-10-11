package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Material implements Serializable {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(nullable = false)
	private String name;
	
	@Column
	private int sequence;
	
	@Column(precision = 8, scale = 2)
	private double leftAmount;
	
	@Column(nullable = false)
	private String unit;
	
	@Column(precision = 8, scale = 2)
	private double alarmAmount;
	
	@Column(columnDefinition="Decimal(10, 2) default '0'")
	private double price;
	
	@Column
	private int alarmStatus;
	
	@Column
	private String barCode;

	@JsonIgnore
	@ManyToOne
	private MaterialCategory materialCategory;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLeftAmount() {
		return leftAmount;
	}

	public void setLeftAmount(double leftAmount) {
		this.leftAmount = leftAmount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public double getAlarmAmount() {
		return alarmAmount;
	}

	public void setAlarmAmount(double alarmAmount) {
		this.alarmAmount = alarmAmount;
	}

	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public MaterialCategory getMaterialCategory() {
		return materialCategory;
	}

	public void setMaterialCategory(MaterialCategory materialCategory) {
		this.materialCategory = materialCategory;
	}

	
	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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
		Material other = (Material) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Material [name=" + name + "]";
	}

	
	
}
