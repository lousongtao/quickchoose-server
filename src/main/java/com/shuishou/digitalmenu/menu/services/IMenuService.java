package com.shuishou.digitalmenu.menu.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.menu.models.DishChoosePopinfo;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

public interface IMenuService {

	public ObjectResult addCategory1(int userId, String firstLanguageName, String secondLanguageName, int sequence);
	public ObjectResult addCategory2(int userId, String firstLanguageName, String secondLanguageName, int sequence, int category1Id, JSONArray jaPrinter);
	public ObjectResult addDish(int userId, String firstLanguageName, String secondLanguageName, int sequence,double price, boolean isNew, 
			boolean isSpecial, int hotLevel, String abbreviation, MultipartFile image, int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			boolean autoMerge, int purchaseType, boolean allowFlavor, 
			String description_1stlang, String description_2ndlang);
	public ObjectResult addFlavor(int userId, String firstLanguageName, String secondLanguageName);
	public ObjectResult addDishConfig(int userId, String firstLanguageName, String secondLanguageName, int sequence, double price, int groupId);
	public ObjectResult addDishConfigGroup(int userId, String firstLanguageName, String secondLanguageName, String uniqueName, int sequence, int requiredQuantity, boolean allowDuplicate);
	
	public ObjectResult updateCategory1(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence);
	public ObjectResult updateCategory2(int userId, int id, String firstLanguageName, String secondLanguageName, int printStyle, int category1Id, JSONArray jaPrinter);
	public ObjectResult updateDish(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence, double price, boolean isNew, 
			boolean isSpecial, byte hotLevel, String abbreviation, int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			boolean autoMerge, int purchaseType, boolean allowFlavor, 
			String description_1stlang, String description_2ndlang);
	public ObjectResult updateFlavor(int userId, int id, String firstLanguageName, String secondLanguageName);
	public ObjectResult updateDishConfig(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence, double price);
	public ObjectResult updateDishConfigGroup(int userId, int id, String firstLanguageName, String secondLanguageName, String uniqueName, int sequence, int requiredQuantity, boolean allowDuplicate);
	
	public ObjectResult changeDishPrice(int userId, int id, double newprice);
	public ObjectResult changeDishPicture(int userId, int id, MultipartFile image);
	public Result changeDishSpecial(int userId, int id, boolean isSpecial);
	public Result changeDishNewProduct(int userId, int id, boolean isNew);
	public Result changeDishSoldOut(int userId, int id, boolean isSoldOut);
	public Result changeDishPromotion(int userId, int id, double promotionPrice);
	public Result cancelDishPromotion(int userId, int id);
	public Result changeDishConfigSoldout(int userId, int configId, boolean isSoldOut);
	
	public ObjectListResult queryAllMenu();
	public ObjectResult queryDishById(int dishId);
	public ObjectListResult queryDishByIdList(ArrayList<Integer> dishIdList);
	public ObjectResult queryDishByName(String dishName);
	public ObjectResult queryCategory1ById(int id);
	public ObjectResult queryCategory2ById(int id);
	public ObjectResult queryDishConfigGroupById(int id);
	public ObjectResult queryDishConfigById(int id);
	public ObjectListResult queryFlavor();
	public ObjectListResult queryDishConfig();
	public ObjectListResult queryDishConfigGroup();
	public ObjectListResult queryDishConfigByIdList(ArrayList<Integer> dishConfigIdList);
	
	public Result deleteCategory1(int userId, int category1Id);
	public Result deleteCategory2(int userId, int category2Id);
	public Result deleteDish(int userId, int dishId);
	public Result deleteFlavor(int userId, int flavorId);
	public Result deleteDishConfig(int userId, int configId);
	public Result deleteDishConfigGroup(int userId, int configGroupId);
	
	public CheckMenuVersionResult checkMenuVersion(int versionId);
	public SimpleValueResult getlastMenuVersion();
	
	public ObjectResult moveinConfigGroupForDish(int userId, int dishId, int configGroupId);
	public ObjectResult moveoutConfigGroupForDish(int userId, int dishId, int configGroupId);
//	public ObjectResult moveConfigIntoGroup(int userId, int configId, int configGroupId);
//	public ObjectResult moveConfigOutofGroup(int userId, int configId, int configGroupId);
}
