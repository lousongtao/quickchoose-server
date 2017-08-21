package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishDataAccessor {

public Session getSession();
	
	public Serializable save(Dish dish);
	
	public void update(Dish dish);
	
	public void delete(Dish dish);
	
	public Dish getDishById(int id);
	
	public List<Dish> getDishesByParentId(int category2id);
}
