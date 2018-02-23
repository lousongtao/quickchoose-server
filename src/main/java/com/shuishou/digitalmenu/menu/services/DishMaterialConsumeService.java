package com.shuishou.digitalmenu.menu.services;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.DishMaterialConsume;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishMaterialConsumeDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.Material;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class DishMaterialConsumeService implements IDishMaterialConsumeService {
	
	private final static Logger logger = Logger.getLogger(DishMaterialConsumeService.class);
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private IDishDataAccessor dishDA;
	
	@Autowired
	private IMaterialDataAccessor materialDA;
	
	@Autowired
	private IDishMaterialConsumeDataAccessor dishMaterialConsumeDA;
	
	@Override
	@Transactional
	public ObjectResult addDishMaterialConsume(int userId, int dishId, int materialId, double amount) {
		DishMaterialConsume dmc = new DishMaterialConsume();
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null){
			return new ObjectResult("cannot find dish object by id "+ dishId, false);
		}
		Material material = materialDA.getMaterialById(materialId);
		if (material == null){
			return new ObjectResult("cannot find material object by id "+ materialId, false);
		}
		dmc.setDish(dish);
		dmc.setAmount(amount);
		dmc.setMaterial(material);
		dishMaterialConsumeDA.save(dmc);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHMATERIALCONSUME_CHANGE.toString(), "User " + selfUser + " add DISH MATERIAL CONSUME : " + dmc);
		return new ObjectResult(Result.OK, true, dmc);
	}


	@Override
	@Transactional
	public ObjectResult updateDishMaterialConsume(int userId, int id, double amount) {
		DishMaterialConsume dmc = dishMaterialConsumeDA.getDishMaterialConsumeById(id);
		if (dmc == null){
			return new ObjectResult("cannot find DishMaterialConsume object by id "+ id, false);
		}		
		double oldamount = dmc.getAmount();
		dmc.setAmount(amount);
		dishMaterialConsumeDA.save(dmc);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHMATERIALCONSUME_CHANGE.toString(), "User " + selfUser + " update DISH MATERIAL CONSUME : " + dmc + ", amount from "+oldamount +" to "+amount);
		return new ObjectResult(Result.OK, true, dmc);
	}


	@Override
	@Transactional
	public ObjectListResult queryDishMaterialConsume() {
		ArrayList<DishMaterialConsume> dmcs = (ArrayList<DishMaterialConsume>) dishMaterialConsumeDA.getAllDishMaterialConsume();
		return new ObjectListResult(Result.OK, true, dmcs);
	}

	@Override
	@Transactional
	public ObjectListResult queryDishMaterialConsumeByDish(int dishId) {
		ArrayList<DishMaterialConsume> dmcs = (ArrayList<DishMaterialConsume>) dishMaterialConsumeDA.getDishMaterialConsumeByDishId(dishId);
		return new ObjectListResult(Result.OK, true, dmcs);
	}

	@Override
	@Transactional
	public Result deleteDishMaterialConsume(int userId, int id) {
		DishMaterialConsume dmc = dishMaterialConsumeDA.getDishMaterialConsumeById(id);
		if (dmc == null){
			return new ObjectResult("cannot find DishMaterialConsume object by id "+ id, false);
		}
		dishMaterialConsumeDA.delete(dmc);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHMATERIALCONSUME_CHANGE.toString(), "User " + selfUser + " delete DISH MATERIAL CONSUME : " + dmc);
		return new ObjectResult(Result.OK, true);
	}
}
