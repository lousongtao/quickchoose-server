package com.shuishou.digitalmenu.rawmaterial.services;

import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface IMaterialService {

	public ObjectListResult queryMaterialCategory();
	public ObjectResult addMaterialCategory(int userId, String name, int sequence);
	public ObjectResult updateMaterialCategory(int userId, int id, String name, int sequence);
	public ObjectResult deleteMaterialCategory(int userId, int id);
	public ObjectResult addMaterial(int userId, String name, int sequence, double leftAmount, String unit, double alarmAmount, int categoryId);
	public ObjectResult updateMaterial(int userId, int id, String name, int sequence, double leftAmount, String unit, double alarmAmount, int categoryId);
	public ObjectResult updateMaterialAmount(int userId, int id, double leftAmount);
	public ObjectResult deleteMaterial(int userId, int id);
	public ObjectListResult queryMaterialByCategoryId(int categoryid);
	public ObjectResult queryMaterialById(int id);
	public ObjectResult queryMaterialByName(String name);
	
}
