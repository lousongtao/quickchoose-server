package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IMaterialCategoryDataAccessor {
	public Session getSession();
	
	public Serializable save(MaterialCategory materialCategory);
	
	public void update(MaterialCategory materialCategory);
	
	public void delete(MaterialCategory materialCategory);
	
	public MaterialCategory getMaterialCategoryById(int id);
	
	public List<MaterialCategory> getAllMaterialCategory();
}
