package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table
public class Category1 implements Serializable {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "first_language_name", nullable = false, unique = true)
	private String firstLanguageName;
	
	@Column(name = "second_language_name", unique = true)
	private String secondLanguageName;
	
	@Column
	private int sequence;

//	@JsonManagedReference
	@OneToMany(cascade=CascadeType.ALL, mappedBy="category1")
	@OrderBy("sequence")
//	@JoinColumn(name="category1_id")
	private List<Category2> category2s;
	
	public Category1(){}
	
	public List<Category2> getCategory2s() {
		return category2s;
	}

	public void setCategory2s(List<Category2> category2s) {
		this.category2s = category2s;
	}

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
		return "Category1 [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		Category1 other = (Category1) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
