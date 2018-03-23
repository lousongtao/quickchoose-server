package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IMaterialDataAccessor {
	public Session getSession();
	
	public Serializable save(Material material);
	
	public void update(Material material);
	
	public void delete(Material material);
	
	public Material getMaterialById(int id);
	
	public Material getMaterialByName(String name);
	
	public List<Material> getMaterialByCategory(int categoryId);
	
	public List<Material> getAllMaterial();
	
	public List<Material> getAllMaterialWithCategory();
}
