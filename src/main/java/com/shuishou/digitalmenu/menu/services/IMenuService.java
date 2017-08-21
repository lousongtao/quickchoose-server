package com.shuishou.digitalmenu.menu.services;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.menu.views.GetCategory1Result;
import com.shuishou.digitalmenu.menu.views.GetCategory2Result;
import com.shuishou.digitalmenu.menu.views.GetDishResult;
import com.shuishou.digitalmenu.menu.views.GetMenuResult;
import com.shuishou.digitalmenu.menu.views.OperationResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

public interface IMenuService {

	public OperationResult addCategory1(long userId, String chineseName, String englishName, int sequence);
	public OperationResult addCategory2(long userId, String chineseName, String englishName, int sequence, int category1Id);
	public OperationResult addDish(long userId, String chineseName, String englishName, int sequence, double price, boolean isNew, boolean isSpecial, int hotLevel, MultipartFile image, int category2Id);
	
	public OperationResult updateCategory1(long userId, int id, String chineseName, String englishName, int sequence);
	public OperationResult updateCategory2(long userId, int id, String chineseName, String englishName, int sequence, int category1Id);
	public OperationResult updateDish(long userId, int id, String chineseName, String englishName, int sequence, double price, boolean isNew, boolean isSpecial, byte hotLevel, int category2Id);
	public OperationResult changeDishPrice(long userId, int id, double newprice);
	public OperationResult changeDishPicture(long userId, int id, MultipartFile image);
	public Result changeDishSpecial(long userId, int id, boolean isSpecial);
	public Result changeDishNewProduct(long userId, int id, boolean isNew);
	public Result changeDishSoldOut(long userId, int id, boolean isSoldOut);
	
	public GetMenuResult queryMenu(String node);
	public GetMenuResult queryAllMenu();
	public GetCategory1Result queryAllCategory1();
	public GetCategory2Result queryAllCategory2(int category1Id);
	public GetDishResult queryAllDish(int category2Id);
	public GetDishResult queryDishById(int dishId);
	
	public Result deleteCategory1(long userId, int category1Id);
	public Result deleteCategory2(long userId, int category2Id);
	public Result deleteDish(long userId, int dishId);
	
	public CheckMenuVersionResult checkMenuVersion(int versionId);
	public SimpleValueResult getlastMenuVersion();
}
