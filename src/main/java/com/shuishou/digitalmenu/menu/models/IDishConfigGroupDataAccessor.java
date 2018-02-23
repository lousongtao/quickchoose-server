package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishConfigGroupDataAccessor {
	public Session getSession();
	
	public Serializable save(DishConfigGroup dishConfigGroup);
	
	public void update(DishConfigGroup dishConfigGroup);
	
	public void delete(DishConfigGroup dishConfigGroup);
	
	public DishConfigGroup getDishConfigGroupById(int id);
	
	public List<DishConfigGroup> getAllDishConfigGroup();
	
	public List<DishConfigGroup> getDishConfigGroupByDishId(int dishId);
}
