package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishMaterialConsumeDataAccessor {
	public Session getSession();
	
	public Serializable save(DishMaterialConsume dishMaterialConsume);
	
	public void update(DishMaterialConsume dishMaterialConsume);
	
	public void delete(DishMaterialConsume dishMaterialConsume);
	
	public DishMaterialConsume getDishMaterialConsumeById(int id);
	
	public List<DishMaterialConsume> getAllDishMaterialConsume();
	
	public List<DishMaterialConsume> getDishMaterialConsumeByDishId(int dishId);
	
	public List<DishMaterialConsume> getDishMaterialConsumeByMaterialId(int materialId);
}
