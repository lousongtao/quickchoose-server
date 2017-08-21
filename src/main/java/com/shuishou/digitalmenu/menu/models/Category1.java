package com.shuishou.digitalmenu.menu.models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Category1 {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "chinese_name", nullable = false)
	private String chineseName;
	
	@Column(name = "english_name", nullable = false)
	private String englishName;
	
	@Column
	private int sequence;

	@OneToMany
	@JoinColumn(name="category1_id")
	private List<Category2> category2s;
	
	
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

	@Override
	public String toString() {
		return "Category1 [chineseName=" + chineseName + ", englishName=" + englishName + "]";
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
