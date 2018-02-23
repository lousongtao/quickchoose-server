package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishConfigDataAccessor {
	public Session getSession();
	
	public Serializable save(DishConfig dishConfig);
	
	public void update(DishConfig dishConfig);
	
	public void delete(DishConfig dishConfig);
	
	public DishConfig getDishConfigById(int id);
	
	public List<DishConfig> getAllDishConfig();
	
	public List<DishConfig> getDishConfigByGroupId(int groupId);
}
