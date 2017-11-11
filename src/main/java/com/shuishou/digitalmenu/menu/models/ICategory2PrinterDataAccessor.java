package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface ICategory2PrinterDataAccessor {

public Session getSession();
	
	public Serializable save(Category2Printer category2Printer);
	
	public void update(Category2Printer category2Printer);
	
	public void delete(Category2Printer category2Printer);
	
	public Category2Printer getCategory2PrinterById(int id);
	
	public List<Category2Printer> getCategory2PrinterByCategory2Id(int category2id);
}
