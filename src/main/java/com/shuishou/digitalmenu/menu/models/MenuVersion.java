package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="menu_version")
public class MenuVersion implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;

//	@Column(nullable=false)
//	private int version;
	
	@Column(nullable=false)
	private int dishId;
	
	@Column(nullable=false)
	private int type; 

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}

	public int getDishId() {
		return dishId;
	}

	public void setDishId(int dishId) {
		this.dishId = dishId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
		MenuVersion other = (MenuVersion) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
