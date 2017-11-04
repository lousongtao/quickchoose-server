package com.shuishou.digitalmenu.menu.services;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.menu.models.DishChoosePopinfo;
import com.shuishou.digitalmenu.menu.models.DishChooseSubitem;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.menu.views.GetCategory1Result;
import com.shuishou.digitalmenu.menu.views.GetCategory2Result;
import com.shuishou.digitalmenu.menu.views.GetDishResult;
import com.shuishou.digitalmenu.menu.views.GetMenuResult;
import com.shuishou.digitalmenu.menu.views.OperationResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

public interface IMenuService {

	public ObjectResult addCategory1(long userId, String chineseName, String englishName, int sequence);
	public ObjectResult addCategory2(long userId, String chineseName, String englishName, int sequence, int category1Id, int printerId);
	public ObjectResult addDish(long userId, String chineseName, String englishName, int sequence, 
			double price, boolean isNew, boolean isSpecial, int hotLevel, String abbreviation, 
			MultipartFile image, int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			ArrayList<DishChooseSubitem> subitems, int subitemAmount, boolean autoMerge, int purchaseType);
	public ObjectResult addFlavor(long userId, String chineseName, String englishName);
	
	
	public ObjectResult updateCategory1(long userId, int id, String chineseName, String englishName, int sequence);
	public ObjectResult updateCategory2(long userId, int id, String chineseName, String englishName, int sequence, int category1Id, int printerId);
	public ObjectResult updateDish(long userId, int id, String chineseName, String englishName, int sequence, 
			double price, boolean isNew, boolean isSpecial, byte hotLevel, String abbreviation, 
			int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			ArrayList<DishChooseSubitem> subitems, int subitemAmount, boolean autoMerge, int purchaseType);
	public ObjectResult updateFlavor(long userId, int id, String chineseName, String englishName);
	
	public OperationResult changeDishPrice(long userId, int id, double newprice);
	public ObjectResult changeDishPicture(long userId, int id, MultipartFile image);
	public Result changeDishSpecial(long userId, int id, boolean isSpecial);
	public Result changeDishNewProduct(long userId, int id, boolean isNew);
	public Result changeDishSoldOut(long userId, int id, boolean isSoldOut);
	
	public GetMenuResult queryMenu(String node);
	public GetMenuResult queryAllMenu();
	public GetCategory1Result queryAllCategory1();
	public GetCategory2Result queryAllCategory2(int category1Id);
	public ObjectListResult queryAllDish(int category2Id);
	public GetDishResult queryDishById(int dishId);
	public ObjectResult queryDishByName(String dishName);
	
	public Result deleteCategory1(long userId, int category1Id);
	public Result deleteCategory2(long userId, int category2Id);
	public Result deleteDish(long userId, int dishId);
	public Result deleteFlavor(long userId, int flavorId);
	
	public CheckMenuVersionResult checkMenuVersion(int versionId);
	public SimpleValueResult getlastMenuVersion();
	
	public ObjectListResult queryFlavor();
}
