package com.shuishou.digitalmenu.menu.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.shuishou.digitalmenu.common.models.Printer;

@Entity
@Table
public class Category2 {

	@Id
	@GeneratedValue
	@Column(unique = true, nullable = false)
	private int id;
	
	@Column(name="chinese_name", nullable = false)
	private String chineseName;
	
	@Column(name = "english_name", nullable = false)
	private String englishName;
	
	@Column(nullable = false)
	private int sequence;
	
	@OneToMany
	@JoinColumn(name="category2_id")
	private List<Dish> dishes;
	
	@ManyToOne
	private Category1 category1;
	
	@ManyToOne
	private Printer printer;
	
	

	public Category1 getCategory1() {
		return category1;
	}

	public void setCategory1(Category1 category1) {
		this.category1 = category1;
	}

	@Override
	public String toString() {
		return "Category2 [chineseName=" + chineseName + ", englishName=" + englishName + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
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
	
	

	public Printer getPrinter() {
		return printer;
	}

	public void setPrinter(Printer printer) {
		this.printer = printer;
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
