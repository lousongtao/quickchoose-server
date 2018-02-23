package com.shuishou.digitalmenu.menu.services;

import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

public interface IDishMaterialConsumeService {

	public ObjectResult addDishMaterialConsume(int userId, int dishId, int materialId, double amount);
	
	public ObjectResult updateDishMaterialConsume(int userId, int id, double amount);
	
	public ObjectListResult queryDishMaterialConsume();
	
	public ObjectListResult queryDishMaterialConsumeByDish(int dishId);
	
	public Result deleteDishMaterialConsume(int userId, int id);
	
}
