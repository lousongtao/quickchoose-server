package com.shuishou.digitalmenu.menu.controllers;

import java.util.ArrayList;

import org.json.JSONArray;
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
import com.shuishou.digitalmenu.menu.services.IDishMaterialConsumeService;
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
	
	@Autowired
	private IDishMaterialConsumeService dishMaterialConsumeService;
	
	@RequestMapping(value = "/menu/addflavor", method = {RequestMethod.POST})
	public @ResponseBody Result addFlavor(
			@RequestParam(value = "userId", required = true) int userId,
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
			@RequestParam(value = "userId", required = true) int userId,
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
			@RequestParam(value = "userId", required = true) int userId,
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
			@RequestParam(value = "userId", required = true) int userId,
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
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "category1Id", required = true) int category1Id,
			@RequestParam(value = "printers", required = true) String printers) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		JSONArray ja = new JSONArray(printers);
//		ArrayList<Integer> printerIds = new Gson().fromJson(sPrinterId, new TypeToken<ArrayList<Integer>>(){}.getType());
		
		Result result = menuService.addCategory2(userId, firstLanguageName, secondLanguageName, sequence, category1Id, ja);
		
		return result;
	}
	
	@RequestMapping(value="/menu/update_category2", method = {RequestMethod.POST})
	public @ResponseBody Result updateCategory2(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence, 
			@RequestParam(value = "category1Id", required = true) int category1Id,
			@RequestParam(value = "printers", required = true) String printers) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		JSONArray ja = new JSONArray(printers);
//		ArrayList<Integer> printerIds = new Gson().fromJson(sPrinterId, new TypeToken<ArrayList<Integer>>(){}.getType());
		
		Result result = menuService.updateCategory2(userId, id, firstLanguageName, secondLanguageName, sequence, category1Id, ja);
		
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
	 * @param purchaseType
	 * @param category2Id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/menu/add_dish", method = {RequestMethod.POST})
	public @ResponseBody Result addDish(
			@RequestParam(value="userId", required = true) int userId, 
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
			@RequestParam(value = "purchaseType", required = false, defaultValue = "1") int purchaseType,
			@RequestParam(value = "description_1stlang", required = false, defaultValue = "") String description_1stlang,
			@RequestParam(value = "description_2ndlang", required = false, defaultValue = "") String description_2ndlang,
			@RequestParam(value = "category2Id", required = true) int category2Id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		DishChoosePopinfo popinfo = null;
		
		if (sPopInfo != null && sPopInfo.length() > 0){
			popinfo = new Gson().fromJson(sPopInfo, new TypeToken<DishChoosePopinfo>(){}.getType());
		}
		Result result = menuService.addDish(userId, firstLanguageName, secondLanguageName, sequence, price, isNew, 
				isSpecial, hotLevel, abbreviation, null, category2Id, chooseMode, popinfo, 
				autoMerge, purchaseType, allowFlavor, description_1stlang, description_2ndlang);
		
		return result;
	}
	
	@RequestMapping(value="/menu/update_dish", method = {RequestMethod.POST})
	public @ResponseBody Result updateDish(
			@RequestParam(value="userId", required = true) int userId, 
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
			@RequestParam(value = "purchaseType", required = false, defaultValue = "1") int purchaseType,
			@RequestParam(value = "description_1stlang", required = false, defaultValue = "") String description_1stlang,
			@RequestParam(value = "description_2ndlang", required = false, defaultValue = "") String description_2ndlang,
			@RequestParam(value = "category2Id", required = true) int category2Id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		DishChoosePopinfo popinfo = null;
		if (sPopInfo != null && sPopInfo.length() > 0){
			popinfo = new Gson().fromJson(sPopInfo, new TypeToken<DishChoosePopinfo>(){}.getType());
		}
		
		Result result = menuService.updateDish(userId, id, firstLanguageName, secondLanguageName, sequence, price, isNew, isSpecial, hotLevel, 
				abbreviation, category2Id, chooseMode, popinfo, autoMerge, purchaseType, allowFlavor, 
				description_1stlang, description_2ndlang);
		
		return result;
	}
	
	@RequestMapping(value="/menu/changedishpicture", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishPicture(
			@RequestParam(value="userId", required = true) int userId, 
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
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "newPrice", required = true) double newPrice) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishPrice(userId, id, newPrice);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/changedishpromotion", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishPromotion(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "dishId", required = true) int dishId,
			@RequestParam(value = "promotionPrice", required = true) double promotionPrice) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishPromotion(userId, dishId, promotionPrice);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/canceldishpromotion", method = {RequestMethod.POST})
	public @ResponseBody Result cancelDishPromotion(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "dishId", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.cancelDishPromotion(userId, id);
		
		return result;
	}
	
	@RequestMapping(value="/menu/change_dish_newproduct", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishNewProduct(
			@RequestParam(value="userId", required = true) int userId, 
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
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "isSoldOut", required = true) boolean isSoldOut) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishSoldOut(userId, id, isSoldOut);
		
		return result;
	}
	
	@RequestMapping(value="/menu/changedishconfigsoldout", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishConfigSoldOut(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "configId", required = true) int configId,
			@RequestParam(value = "isSoldOut", required = true) boolean isSoldOut) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		
		Result result = menuService.changeDishConfigSoldout(userId, configId, isSoldOut);
		
		return result;
	}
	
	@RequestMapping(value="/menu/checkmenuversion", method = {RequestMethod.POST})
	public @ResponseBody CheckMenuVersionResult checkMenuVersion( 
			@RequestParam(value = "versionId", required = true) int versionId) throws Exception{
		
		CheckMenuVersionResult result = menuService.checkMenuVersion(versionId);
		
		return result;
	}
	
	@RequestMapping(value="/menu/getlastmenuversion", method = RequestMethod.POST)
	public @ResponseBody SimpleValueResult getlastMenuVersion() throws Exception{
		return menuService.getlastMenuVersion();
	}
	
	@RequestMapping(value="/menu/change_dish_special", method = {RequestMethod.POST})
	public @ResponseBody Result changeDishSpecial(
			@RequestParam(value="userId", required = true) int userId, 
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
	
	@RequestMapping(value="/menu/querycategory1byid", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult queryCategory1ById(
			@RequestParam(value = "id", required = true) int id) throws Exception{
		ObjectResult result = menuService.queryCategory1ById(id);
		return result;
	}
	
	@RequestMapping(value="/menu/querycategory2byid", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult queryCategory2ById(
			@RequestParam(value = "id", required = true) int id) throws Exception{
		ObjectResult result = menuService.queryCategory2ById(id);
		return result;
	}
	
	@RequestMapping(value="/menu/querydishconfiggroupbyid", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult queryDishConfigGroupById(
			@RequestParam(value = "id", required = true) int id) throws Exception{
		ObjectResult result = menuService.queryDishConfigGroupById(id);
		return result;
	}
	
	/**
	 * 
	 * @param sIdList Id seperate by comma
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/menu/querydishbyidlist", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryDishByIdList(
			@RequestParam(value = "dishIdList", required = true) String sIdList) throws Exception{
		ArrayList<Integer> idList = new ArrayList<>();
		String[] sIds = sIdList.split(",");
		for (int i = 0; i < sIds.length; i++) {
			idList.add(Integer.parseInt(sIds[i]));
		}
		ObjectListResult result = menuService.queryDishByIdList(idList);
		return result;
	}
	
	/**
	 * 
	 * @param sIdList Id seperate by comma
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/menu/querydishconfigbyidlist", method = {RequestMethod.POST})
	public @ResponseBody ObjectListResult queryDishConfigByIdList(
			@RequestParam(value = "dishConfigIdList", required = true) String sIdList) throws Exception{
		ArrayList<Integer> idList = new ArrayList<>();
		String[] sIds = sIdList.split(",");
		for (int i = 0; i < sIds.length; i++) {
			idList.add(Integer.parseInt(sIds[i]));
		}
		ObjectListResult result = menuService.queryDishConfigByIdList(idList);
		return result;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/menu/querydishconfigbyid", method = {RequestMethod.POST})
	public @ResponseBody ObjectResult queryDishConfigById(
			@RequestParam(value = "id", required = true) int id) throws Exception{
		ObjectResult result = menuService.queryDishConfigById(id);
		return result;
	}
	
	/**
	 * 删除操作, 包括删除目录及dish
	 * @param userId
	 * @param objectType, 由于查询菜单时拼装的ID为"type+ID", 所以前端同样范围该值, 这里需要根据type进行区分.
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/menu/delete", method = RequestMethod.POST)
	public @ResponseBody Result deleteMenu(
			@RequestParam(value="userId", required = true) int userId, 
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
	
	@RequestMapping(value = "/menu/deleteflavor", method = RequestMethod.POST)
	public @ResponseBody Result deleteFlavor(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteFlavor(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_category1", method = RequestMethod.POST)
	public @ResponseBody Result deleteCategory1(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteCategory1(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_category2", method = RequestMethod.POST)
	public @ResponseBody Result deleteCategory2(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteCategory2(userId, id);
	}
	
	@RequestMapping(value = "/menu/delete_dish", method = RequestMethod.POST)
	public @ResponseBody Result deleteDish(
			@RequestParam(value="userId", required = true) int userId, 
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

	@RequestMapping(value="/menu/query_dishconfiggroup", method = {RequestMethod.GET})
	public @ResponseBody Result queryDishConfigGroup() throws Exception{
		ObjectListResult result = menuService.queryDishConfigGroup();
		return result;
	}
	
	@RequestMapping(value="/menu/query_dishconfig", method = {RequestMethod.GET})
	public @ResponseBody Result queryDishConfig() throws Exception{
		ObjectListResult result = menuService.queryDishConfig();
		return result;
	}
	
	@RequestMapping(value="/menu/add_dishconfiggroup", method = {RequestMethod.POST})
	public @ResponseBody Result addDishConfigGroup(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "uniqueName", required = true) String uniqueName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "requiredQuantity", required = true) int requiredQuantity,
			@RequestParam(value = "allowDuplicate", required = true) boolean allowDuplicate) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.addDishConfigGroup(userId, firstLanguageName, secondLanguageName, uniqueName, sequence, requiredQuantity, allowDuplicate);
	}
	
	@RequestMapping(value="/menu/update_dishconfiggroup", method = {RequestMethod.POST})
	public @ResponseBody Result updateDishConfigGroup(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName,
			@RequestParam(value = "uniqueName", required = true) String uniqueName,
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "requiredQuantity", required = true) int requiredQuantity,
			@RequestParam(value = "allowDuplicate", required = true) boolean allowDuplicate) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.updateDishConfigGroup(userId, id, firstLanguageName, secondLanguageName, uniqueName, sequence, requiredQuantity, allowDuplicate);
	}
	
	@RequestMapping(value="/menu/delete_dishconfiggroup", method = {RequestMethod.POST})
	public @ResponseBody Result deleteDishConfigGroup(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteDishConfigGroup(userId, id);
	}
	
	@RequestMapping(value="/menu/movein_configgroup_fordish", method = {RequestMethod.POST})
	public @ResponseBody Result moveinConfigGroupForDish(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="dishId", required = true) int dishId,
			@RequestParam(value="configGroupId", required = true) int configGroupId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.moveinConfigGroupForDish(userId, dishId, configGroupId);
	}
	
	@RequestMapping(value="/menu/moveout_configgroup_fordish", method = {RequestMethod.POST})
	public @ResponseBody Result moveoutConfigGroupForDish(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="dishId", required = true) int dishId,
			@RequestParam(value="configGroupId", required = true) int configGroupId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.moveoutConfigGroupForDish(userId, dishId, configGroupId);
	}
	
	@RequestMapping(value="/menu/add_dishconfig", method = {RequestMethod.POST})
	public @ResponseBody Result addDishConfig(
			@RequestParam(value="userId", required = true) int userId, 
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "groupId", required = true) int groupId,
			@RequestParam(value = "price", required = true) double price) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.addDishConfig(userId, firstLanguageName, secondLanguageName, sequence, price, groupId);
	}
	
	@RequestMapping(value="/menu/update_dishconfig", method = {RequestMethod.POST})
	public @ResponseBody Result updateDishConfig(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id,
			@RequestParam(value = "firstLanguageName", required = true) String firstLanguageName, 
			@RequestParam(value = "secondLanguageName", required = false, defaultValue = "") String secondLanguageName, 
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "price", required = true) double price) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.updateDishConfig(userId, id, firstLanguageName, secondLanguageName, sequence, price);
	}
	
	@RequestMapping(value="/menu/delete_dishconfig", method = {RequestMethod.POST})
	public @ResponseBody Result deleteDishConfig(
			@RequestParam(value="userId", required = true) int userId,
			@RequestParam(value="id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_EDIT_MENU)){
			return new Result("no_permission");
		}
		return menuService.deleteDishConfig(userId, id);
	}
	
	@RequestMapping(value = "/menu/adddishmaterialconsume", method = {RequestMethod.POST})
	public @ResponseBody Result addDishMaterialConsume(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "dishId", required = true) int dishId,
			@RequestParam(value = "amount", required = true) double amount,
			@RequestParam(value = "materialId", required = true) int materialId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new Result("no_permission");
		}
		
		Result result = dishMaterialConsumeService.addDishMaterialConsume(userId, dishId, materialId, amount);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/updatedishmaterialconsume", method = {RequestMethod.POST})
	public @ResponseBody Result updateDishMaterialConsume(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "amount", required = true) double amount) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new Result("no_permission");
		}
		
		Result result = dishMaterialConsumeService.updateDishMaterialConsume(userId, id, amount);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/deletedishmaterialconsume", method = {RequestMethod.POST})
	public @ResponseBody Result deleteDishMaterialConsume(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new Result("no_permission");
		}
		
		Result result = dishMaterialConsumeService.deleteDishMaterialConsume(userId, id);
		
		return result;
	}
	
	@RequestMapping(value = "/menu/querydishmaterialconsume", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result queryAllDishMaterialConsume() throws Exception{
		Result result = dishMaterialConsumeService.queryDishMaterialConsume();
		
		return result;
	}
	
	@RequestMapping(value = "/menu/querydishmaterialconsumebydish", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody Result queryAllDishMaterialConsumeByDish(
			@RequestParam(value = "dishId", required = true) int dishId) throws Exception{
		Result result = dishMaterialConsumeService.queryDishMaterialConsumeByDish(dishId);
		
		return result;
	}
}
