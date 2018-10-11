package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="dishchoose_popinfo")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Menu")
public class DishChoosePopinfo implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "first_language_name", nullable = false)
	private String firstLanguageName;
	
	@Column(name = "second_language_name")
	private String secondLanguageName;
	
	@JsonIgnore
	@ManyToOne
	private Dish dish;

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
		return "DishChoosePopinfo [firstLanguageName=" + firstLanguageName + ", secondLanguageName="
				+ secondLanguageName + "]";
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
