package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface IMaterialRecordDataAccessor {
	public Session getSession();
	
	public Serializable save(MaterialRecord materialRecord);
	
	public void update(MaterialRecord materialRecord);
	
	public void delete(MaterialRecord materialRecord);
	
	public MaterialRecord getMaterialRecordById(int id);
	
	public List<MaterialRecord> getMaterialRecordByMaterial(int materialId);
}
