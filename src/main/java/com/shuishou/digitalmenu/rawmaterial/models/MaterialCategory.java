package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table
public class MaterialCategory implements Serializable {

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private int id;
	
	@Column(nullable = false)
	private String name;
	
	@Column
	private int sequence;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="materialCategory")
	@OrderBy("sequence")
	private List<Material> materials;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MaterialCategory other = (MaterialCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaterialCategory [name=" + name + "]";
	}

	
	
}
