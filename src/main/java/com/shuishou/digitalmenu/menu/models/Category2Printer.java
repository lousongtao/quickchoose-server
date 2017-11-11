package com.shuishou.digitalmenu.menu.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuishou.digitalmenu.common.models.Printer;

@Entity
@Table(name="category2_printer")
public class Category2Printer {
	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private long id;
	
	@JsonIgnore
	@ManyToOne(optional = false)
	private Category2 category2;
	
	@ManyToOne(optional = false)
	private Printer printer;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Category2 getCategory2() {
		return category2;
	}

	public void setCategory2(Category2 category2) {
		this.category2 = category2;
	}

	public Printer getPrinter() {
		return printer;
	}

	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

	@Override
	public String toString() {
		return "Category2Printer [category2=" + category2 + ", printer=" + printer + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Category2Printer other = (Category2Printer) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
