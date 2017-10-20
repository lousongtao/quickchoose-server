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

@Entity
@Table(name="dishchoose_popinfo")
public class DishChoosePopinfo implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "popinfo_cn", nullable = false)
	private String popInfoCN;
	
	@Column(name = "popinfo_en", nullable = false)
	private String popInfoEN;
	
	@JsonIgnore
	@ManyToOne
	private Dish dish;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getPopInfoCN() {
		return popInfoCN;
	}

	public void setPopInfoCN(String popInfoCN) {
		this.popInfoCN = popInfoCN;
	}

	public String getPopInfoEN() {
		return popInfoEN;
	}

	public void setPopInfoEN(String popInfoEN) {
		this.popInfoEN = popInfoEN;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	@Override
	public String toString() {
		return "DishChoosePopinfo [id=" + id + ", dish=" + dish.getChineseName() + ", popInfoCN=" + popInfoCN + "]";
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
		DishChoosePopinfo other = (DishChoosePopinfo) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
