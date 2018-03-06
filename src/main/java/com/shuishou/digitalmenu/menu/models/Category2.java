package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shuishou.digitalmenu.common.models.Printer;

@Entity
@Table
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category2 implements Serializable{

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@Column(name = "first_language_name", nullable = false)
	private String firstLanguageName;
	
	@Column(name = "second_language_name")
	private String secondLanguageName;
	
	@Column(nullable = false)
	private int sequence;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="category2")
	@OrderBy("sequence")
	private List<Dish> dishes;
	
	@JsonIgnore
	@ManyToOne
	private Category1 category1;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="category2")
	private List<Category2Printer> category2PrinterList;
	
	public Category1 getCategory1() {
		return category1;
	}

	public void setCategory1(Category1 category1) {
		this.category1 = category1;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "Category2 [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}
	
	public void addCategory2Printer(Category2Printer cp){
		if (category2PrinterList == null)
			category2PrinterList = new ArrayList<Category2Printer>();
		category2PrinterList.add(cp);
	}

	public List<Category2Printer> getCategory2PrinterList() {
		return category2PrinterList;
	}

	public void setCategory2PrinterList(List<Category2Printer> category2PrinterList) {
		this.category2PrinterList = category2PrinterList;
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
		Category2 other = (Category2) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
