package com.shuishou.digitalmenu.indent.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IIndentDetailDataAccessor {
	public Session getSession();
	
	public Serializable save(IndentDetail indentDetail);
	
	public void update(IndentDetail indentDetail);
	
	public void delete(IndentDetail indentDetail);
	
	public IndentDetail getIndentDetailById(int id);
	
	public IndentDetail getIndentDetailByParent(int indentId, int dishId);
	
	public List<IndentDetail> getIndentDetailByIndentId(int indentId);
	
	public List<IndentDetail> getAllIndentDetail();
}
