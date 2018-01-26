package com.shuishou.digitalmenu.menu.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.menu.models.DishChoosePopinfo;
import com.shuishou.digitalmenu.menu.models.DishChooseSubitem;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

public interface IMenuService {

	public ObjectResult addCategory1(long userId, String firstLanguageName, String secondLanguageName, int sequence);
	public ObjectResult addCategory2(long userId, String firstLanguageName, String secondLanguageName, int sequence, int category1Id, JSONArray jaPrinter);
	public ObjectResult addDish(long userId, String firstLanguageName, String secondLanguageName, int sequence,double price, boolean isNew, 
			boolean isSpecial, int hotLevel, String abbreviation, MultipartFile image, int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			ArrayList<DishChooseSubitem> subitems, int subitemAmount, boolean autoMerge, int purchaseType, boolean allowFlavor, 
			String description_1stlang, String description_2ndlang);
	public ObjectResult addFlavor(long userId, String firstLanguageName, String secondLanguageName);
	
	
	public ObjectResult updateCategory1(long userId, int id, String firstLanguageName, String secondLanguageName, int sequence);
	public ObjectResult updateCategory2(long userId, int id, String firstLanguageName, String secondLanguageName, int printStyle, int category1Id, JSONArray jaPrinter);
	public ObjectResult updateDish(long userId, int id, String firstLanguageName, String secondLanguageName, int sequence, double price, boolean isNew, 
			boolean isSpecial, byte hotLevel, String abbreviation, int category2Id, int chooseMode, DishChoosePopinfo popinfo, 
			ArrayList<DishChooseSubitem> subitems, int subitemAmount, boolean autoMerge, int purchaseType, boolean allowFlavor, 
			String description_1stlang, String description_2ndlang);
	public ObjectResult updateFlavor(long userId, int id, String firstLanguageName, String secondLanguageName);
	
	public ObjectResult changeDishPrice(long userId, int id, double newprice);
	public ObjectResult changeDishPicture(long userId, int id, MultipartFile image);
	public Result changeDishSpecial(long userId, int id, boolean isSpecial);
	public Result changeDishNewProduct(long userId, int id, boolean isNew);
	public Result changeDishSoldOut(long userId, int id, boolean isSoldOut);
	public Result changeDishPromotion(long userId, int id, double promotionPrice);
	public Result cancelDishPromotion(long userId, int id);
	
//	public GetMenuResult queryMenu(String node);
	public ObjectListResult queryAllMenu();
//	public GetCategory1Result queryAllCategory1();
//	public GetCategory2Result queryAllCategory2(int category1Id);
//	public ObjectListResult queryAllDish(int category2Id);
	public ObjectResult queryDishById(int dishId);
	public ObjectListResult queryDishByIdList(ArrayList<Integer> dishIdList);
	public ObjectResult queryDishByName(String dishName);
	
	public Result deleteCategory1(long userId, int category1Id);
	public Result deleteCategory2(long userId, int category2Id);
	public Result deleteDish(long userId, int dishId);
	public Result deleteFlavor(long userId, int flavorId);
	
	public CheckMenuVersionResult checkMenuVersion(int versionId);
	public SimpleValueResult getlastMenuVersion();
	
	public ObjectListResult queryFlavor();
}
