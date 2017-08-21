package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IMenuVersionDataAccessor {
	public Session getSession();
	
	public Serializable save(MenuVersion mv);
	
	public MenuVersion getMenuVersionById(int id);
	
	/**
	 * 得到大于该ID的所有version
	 * @param id
	 * @return
	 */
	public List<MenuVersion> getMenuVersionFromId(int id);
	
	public MenuVersion getLastRecord();
}
