package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.Date;
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
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.menu.models.Category2;

@Entity
@Table
public class MaterialRecord implements Serializable {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@ManyToOne
	private Material material;
	
	/**
	 * 操作数量, 可以为负
	 */
	@Column(nullable = false)
	private double amount;
	
	/**
	 * 每次操作后的剩余数量
	 */
	@Column(precision = 8, scale = 2, nullable=false)
	private double leftAmount;
	
	@Column
	private String operator;
	
	/**
	 * 操作类型
	 */
	@Column(nullable = false)
	private int type;
	
	@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone="GMT+8:00")
	@Column(nullable = false)
	private Date date;
	
	@Column
	private int indentDetailId;
	
	
	
	public int getIndentDetailId() {
		return indentDetailId;
	}

	public void setIndentDetailId(int indentDetailId) {
		this.indentDetailId = indentDetailId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getLeftAmount() {
		return leftAmount;
	}

	public void setLeftAmount(double leftAmount) {
		this.leftAmount = leftAmount;
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

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		MaterialRecord other = (MaterialRecord) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Material";
	}

	
	
}
