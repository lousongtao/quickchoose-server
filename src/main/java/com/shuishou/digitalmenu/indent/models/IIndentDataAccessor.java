package com.shuishou.digitalmenu.indent.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;

public interface IIndentDataAccessor {
	public Session getSession();
	
	public Serializable save(Indent indent);
	
	public void update(Indent indent);
	
	public void delete(Indent indent);
	
	public Indent getIndentById(int id);
	
	public List<Indent> getAllIndent();
	
	public List<Indent> getUnpaidIndent();
	
	public List<Indent> getIndents(int start, int limit, Date starttime, Date endtime, Byte[] status, String deskname, List<String> orderbys);
	
	public int getIndentCount(Date starttime, Date endtime, Byte[] status, String deskname);
	
	public int getMaxSequenceToday();
}
