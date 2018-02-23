package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shuishou.digitalmenu.ConstantValue;

@Entity
@Table
public class DishConfigGroup implements Serializable{

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(name = "first_language_name", nullable = false)
	private String firstLanguageName;
	
	@Column(name = "second_language_name")
	private String secondLanguageName;
	
	/**
	 * 给管理员提供的标识名称, 因为不同的ConfigGroup, 在界面上显示的信息可能是相同的, 会导致管理员无法区分.
	 * 程序和数据库都不限制该字段的唯一性约束, 完全由系统管理员根据需要填写. 必填项
	 */
	@Column(nullable = false)
	private String uniqueName;
	
	@Column(nullable = false)
	private int sequence;
	
	@JsonIgnore
	@ManyToMany(mappedBy="configGroups")
	private List<Dish> dishes = new ArrayList<>();
	/**
	 * 必须选择的数量
	 */
	@Column(nullable = false)
	private int requiredQuantity;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="group")
	private List<DishConfig> dishConfigs;
	
	/**
	 * 是否允许重复选择DishConfig
	 * 比如火锅底, 用户可以选择两个相同的口味
	 */
	@Column(nullable = false)
	private boolean allowDuplicate = false;
	
	
	public List<Dish> getDishes() {
		return dishes;
	}

	public void setDishes(List<Dish> dishes) {
		this.dishes = dishes;
	}

	public void setDishConfigs(List<DishConfig> dishConfigs) {
		this.dishConfigs = dishConfigs;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public boolean isAllowDuplicate() {
		return allowDuplicate;
	}

	public void setAllowDuplicate(boolean allowDuplicate) {
		this.allowDuplicate = allowDuplicate;
	}

	public List<DishConfig> getDishConfigs() {
		return dishConfigs;
	}

	public void setDishConfigs(ArrayList<DishConfig> dishConfigs) {
		this.dishConfigs = dishConfigs;
	}

	public void addDishConfig(DishConfig dc){
		if (dishConfigs == null){
			dishConfigs = new ArrayList<>();
		}
		dishConfigs.add(dc);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRequiredQuantity() {
		return requiredQuantity;
	}

	public void setRequiredQuantity(int requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
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
		return "DishConfigGroup [firstLanguageName=" + firstLanguageName + ", secondLanguageName=" + secondLanguageName + "]";
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
		DishConfigGroup other = (DishConfigGroup) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
