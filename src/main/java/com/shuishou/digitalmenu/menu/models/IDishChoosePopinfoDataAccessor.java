package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishChoosePopinfoDataAccessor {
	public Session getSession();
	
	public Serializable save(DishChoosePopinfo dishChoosePopinfo);
	
	public void update(DishChoosePopinfo dishChoosePopinfo);
	
	public void delete(DishChoosePopinfo dishChoosePopinfo);
	
	public DishChoosePopinfo getDishChoosePopinfoById(int id);
	
	public List<DishChoosePopinfo> getAllDishChoosePopinfo();
}
