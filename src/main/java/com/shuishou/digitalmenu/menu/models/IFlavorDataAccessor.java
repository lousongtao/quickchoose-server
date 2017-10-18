package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IFlavorDataAccessor {
	public Session getSession();
	
	public Serializable save(Flavor flavor);
	
	public void update(Flavor flavor);
	
	public void delete(Flavor flavor);
	
	public Flavor getFlavorById(int id);
	
	public List<Flavor> getAllFlavor();
}
