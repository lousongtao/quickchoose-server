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
import com.shuishou.digitalmenu.rawmaterial.models.Material;

@Entity
@Table
public class DishMaterialConsume implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn
	private Dish dish;
	
	@ManyToOne
	@JoinColumn
	private Material material;
	
	/**
	 * 一道菜消耗的量
	 */
	@Column(nullable = false)
	private double amount;
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "DishMaterialConsume [id=" + id + ", dish=" + dish + ", material=" + material + ", amount=" + amount
				+ "]";
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
		DishMaterialConsume other = (DishMaterialConsume) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
