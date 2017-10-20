package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IDishChooseSubitemDataAccessor {
	public Session getSession();
	
	public Serializable save(DishChooseSubitem dishChooseSubitem);
	
	public void update(DishChooseSubitem dishChooseSubitem);
	
	public void delete(DishChooseSubitem dishChooseSubitem);
	
	public DishChooseSubitem getDishChooseSubitemById(int id);
	
	public List<DishChooseSubitem> getAllDishChooseSubitem();
}
