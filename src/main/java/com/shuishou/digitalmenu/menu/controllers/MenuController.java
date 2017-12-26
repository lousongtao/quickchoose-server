package com.shuishou.digitalmenu.menu.controllers;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuishou.digitalmenu.BaseController;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.services.IAccountService;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.menu.models.DishChoosePopinfo;
import com.shuishou.digitalmenu.menu.models.DishChooseSubitem;
import com.shuishou.digitalmenu.menu.services.IMenuService;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

@Controller
public class MenuController extends BaseController {

	/**
	 * the logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(MenuController.class);
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IMenuService menuService;
	
	@RequestMapping(value = "/menu/addflavor", method = {RequestMethod.POST})
	public @ResponseBody Result addFlavor(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.addFlavor(userId, firstLanguageName, secondLanguageName);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/add_category1", method = {RequestMethod.POST})
	public @ResponseBody Result addCategory1(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.addCategory1(userId, firstLanguageName, secondLanguageName, sequence);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/updateflavor", method = {RequestMethod.POST})
	public @ResponseBody Result updateFlavor(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.updateFlavor(userId, id, firstLanguageName, secondLanguageName);
		
		return result;
		
	}
	
	@RequestMapping(value = "/menu/update_category1", method = {RequestMethod.POST})
	public @ResponseBody Result updateCategory1(
			@RequestParam(value = "userId", required = true) long userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.updateCategory1(userId, id, firstLanguageName, secondLanguageName, sequence);
		
		return result;
		
	}
	
	@RequestMapping(value="/menu/add_category2", method = {RequestMethod.POST})
	public @ResponseBody Result addCategory2(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "printStyle", required = true) int printStyle, 
			@RequestParam(value = "category1Id", required = true) int category1Id,
			@RequestParam(value = "printerIds", required = true) String sPrinterId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		ArrayList<Integer> printerIds = new Gson().fromJson(sPrinterId, new TypeToken<ArrayList<Integer>>(){}.getType());
		
		Result result = menuService.addCategory2(userId, firstLanguageName, secondLanguageName, sequence, printStyle, category1Id, printerIds);
		
		return result;
	}
	
	@RequestMapping(value="/menu/update_category2", method = {RequestMethod.POST})
	public @ResponseBody Result updateCategory2(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence, 
			@RequestParam(value = "printStyle", required = true) int printStyle, 
			@RequestParam(value = "category1Id", required = true) int category1Id,
			@RequestParam(value = "printerIds", required = true) String sPrinterId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		ArrayList<Integer> printerIds = new Gson().fromJson(sPrinterId, new TypeToken<ArrayList<Integer>>(){}.getType());
		
		Result result = menuService.updateCategory2(userId, id, firstLanguageName, secondLanguageName, sequence, printStyle, category1Id, printerIds);
		
		return result;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param firstLanguageName
	 * @param secondLanguageName
	 * @param sequence
	 * @param price
	 * @param isNew
	 * @param isSpecial
	 * @param hotLevel
	 * @param abbreviation
	 * @param autoMerge
	 * @param chooseMode
	 * @param allowFlavor
	 * @param sPopInfo : a string of json format
	 * @param sDishChooseSubitem: a string of json format
	 * @param subitemAmount
	 * @param purchaseType
	 * @param category2Id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/menu/add_dish", method = {RequestMethod.POST})
	public @ResponseBody Result addDish(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "price", required = true) double price,
			@RequestParam(value = "isNew", required = false, defaultValue = "false") boolean isNew,
			@RequestParam(value = "isSpecial", required = false, defaultValue = "false") boolean isSpecial,
			@RequestParam(value = "hotLevel", required = true) int hotLevel,
			@RequestParam(value = "abbreviation", required = true) String abbreviation, 
			@RequestParam(value = "autoMerge", required = true) boolean autoMerge,
			@RequestParam(value = "chooseMode", required = true) int chooseMode,
			@RequestParam(value = "allowFlavor", required = true) boolean allowFlavor,
			@RequestParam(value = "sPopInfo", required = false) String sPopInfo,
			@RequestParam(value = "dishChooseSubitem", required = false) String sDishChooseSubitem,//a json String of list;
			@RequestParam(value = "subitemAmount", required = false, defaultValue = "0") int subitemAmount,
			@RequestParam(value = "purchaseType", required = false, defaultValue = "1") int purchaseType,
			@RequestParam(value = "category2Id", required = true) int category2Id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		DishChoosePopinfo popinfo = null;
		ArrayList<DishChooseSubitem> subitems= null;
		
		if (sPopInfo != null && sPopInfo.length() > 0){
			popinfo = new Gson().fromJson(sPopInfo, new TypeToken<DishChoosePopinfo>(){}.getType());
		}
		if (sDishChooseSubitem != null && sDishChooseSubitem.length() > 0){
			subitems = new Gson().fromJson(sDishChooseSubitem, new TypeToken<ArrayList<DishChooseSubitem>>(){}.getType());
			
		}
		Result result = menuService.addDish(userId, firstLanguageName, secondLanguageName, sequence, price, isNew, 
				isSpecial, hotLevel, abbreviation, null, category2Id, chooseMode, popinfo, subitems, subitemAmount,
				autoMerge, purchaseType, allowFlavor);
		
		return result;
	}
	
	@RequestMapping(value="/menu/update_dish", method = {RequestMethod.POST})
	public @ResponseBody Result updateDish(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "price", required = true) double price,
			@RequestParam(value = "isNew", required = false, defaultValue = "false") boolean isNew,
			@RequestParam(value = "isSpecial", required = false, defaultValue = "false") boolean isSpecial,
			@RequestParam(value = "hotLevel", required = true) byte hotLevel,
			@RequestParam(value = "pictureName", required = false, defaultValue = "") String pictureName,
			@RequestParam(value = "abbreviation", required = true) String abbreviation, 
			@RequestParam(value = "autoMerge", required = true) boolean autoMerge,
			@RequestParam(value = "allowFlavor", required = true) boolean allowFlavor,
			@RequestParam(value = "chooseMode", required = true) int chooseMode,
			@RequestParam(value = "sPopInfo", required = false) String sPopInfo,
			@RequestParam(value = "dishChooseSubitem", required = false) String sDishChooseSubitem,//a json String of list;
			@RequestParam(value = "subitemAmount", required = false, defaultValue = "0") int subitemAmount,
			@RequestParam(value = "purchaseType", required = false, defaultValue = "1") int purchaseType,
			@RequestParam(value = "category2Id", required = true) int category2Id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		DishChoosePopinfo popinfo = null;
		ArrayList<DishChooseSubitem> subitems= null;
		if (sPopInfo != null && sPopInfo.length() > 0){
			popinfo = new Gson().fromJson(sPopInfo, new TypeToken<DishChoosePopinfo>(){}.getType());
		}
		if (sDishChooseSubitem != null && sDishChooseSubitem.length() > 0){
			subitems = new Gson().fromJson(sDishChooseSubitem, new TypeToken<ArrayList<DishChooseSubitem>>(){}.getType());
			
		}
		
		Result result = menuService.updateDish(userId, id, firstLanguageName, secondLanguageName, sequence, price, isNew, isSpecial, hotLevel, 
				abbreviation, category2Id, chooseMode, popinfo, subitems, subitemAmount, autoMerge, purchaseType, allowFlavor);
		
		return result;
	}
	
	@RequestMapping(value="/menu/changedishpicture", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishPicture(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "picture", required = false) MultipartFile image) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishPicture(userId, id, image);
		
		return result;
	}
	
	@RequestMapping(value="/menu/change_dish_price", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishPrice(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newPrice", required = true) double newPrice) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishPrice(userId, id, newPrice);
		
		return result;
	}
	
	@RequestMapping(value="/menu/change_dish_newproduct", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishNewProduct(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "isNew", required = true) boolean isNew) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishNewProduct(userId, id, isNew);
		
		return result;
	}
	
	@RequestMapping(value="/menu/changedishsoldout", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishSoldOut(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "isSoldOut", required = true) boolean isSoldOut) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishSoldOut(userId, id, isSoldOut);
		
		return result;
	}
	
	@RequestMapping(value="/menu/checkmenuversion", method = {RequestMethod.POST})
	public @ResponseBody CheckMenuVersionResult checkMenuVersion( 
			@RequestParam(value = "versionId", required = true) int versionId) throws Exception{
		
		CheckMenuVersionResult result = menuService.checkMenuVersion(versionId);
		
		return result;
	}
	
	@RequestMapping(value="/menu/getlastmenuversion", method = (RequestMethod.POST))
	public @ResponseBody SimpleValueResult getlastMenuVersion() throws Exception{
		return menuService.getlastMenuVersion();
	}
	
	@RequestMapping(value="/menu/change_dish_special", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishSpecial(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "isSpecial", required = true) boolean isSpecial) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishSpecial(userId, id, isSpecial);
		
		return result;
	}
	
	@RequestMapping(value="/menu/querymenu", method = {RequestMethod.GET})
	public @ResponseBody ObjectListResult queryMenu() throws Exception{
		ObjectListResult result = menuService.queryAllMenu();
		return result;
	}
	
	@RequestMapping(value="/menu/querydishbyid", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult queryDishById(
			@RequestParam(value = "dishId", required = true) int dishId) throws Exception{
		ObjectResult result = menuService.queryDishById(dishId);
		return result;
	}
	
	/**
	 * 删除操作, 包括删除目录及dish
	 * @param userId
	 * @param objectTypeId, 由于查询菜单时拼装的ID为"type+ID", 所以前端同样范围该值, 这里需要根据type进行区分.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu/delete", method = (RequestMethod.POST))
	public @ResponseBody Result deleteMenu(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "objectId", required = true) String objectId,
			@RequestParam(value = "objectType", required = true) String objectType) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		int id = Integer.parseInt(objectId);
		if (ConstantValue.TYPE_CATEGORY1INFO.equals(objectType)){
			return menuService.deleteCategory1(userId, id);
		} else if (ConstantValue.TYPE_CATEGORY2INFO.equals(objectType)){
			return menuService.deleteCategory2(userId, id);
		} else if (ConstantValue.TYPE_DISHINFO.equals(objectType)){
			return menuService.deleteDish(userId, id);
		} else {
			return new Result("undefined node type");
		}
	}
	
	@RequestMapping(value = "/menu/deleteflavor", method = (RequestMethod.POST))
	public @ResponseBody Result deleteFlavor(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteFlavor(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_category1", method = (RequestMethod.POST))
	public @ResponseBody Result deleteCategory1(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteCategory1(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_category2", method = (RequestMethod.POST))
	public @ResponseBody Result deleteCategory2(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteCategory2(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_dish", method = (RequestMethod.POST))
	public @ResponseBody Result deleteDish(
			@RequestParam(value="userId", required = true) long userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteDish(userId, id);
	}
	
	@RequestMapping(value="/menu/queryflavor", method = {RequestMethod.GET})
	public @ResponseBody Result queryFlavor() throws Exception{
		ObjectListResult result = menuService.queryFlavor();
		return result;
	}
	
	@RequestMapping(value="/menu/querydishbyname", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result queryDishByName(
			@RequestParam(value = "dishName", required = true) String dishName) throws Exception{
		ObjectResult result = menuService.queryDishByName(dishName);
		return result;
	}
}
