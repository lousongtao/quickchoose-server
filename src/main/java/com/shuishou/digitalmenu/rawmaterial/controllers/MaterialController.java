package com.shuishou.digitalmenu.rawmaterial.controllers;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.services.IPermissionService;
import com.shuishou.digitalmenu.rawmaterial.services.IMaterialService;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

@Controller
public class MaterialController {
	private final static Logger logger = LoggerFactory.getLogger(MaterialController.class);
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IMaterialService materialService;
	
	@RequestMapping(value = "/material/querymaterialcategory", method = {RequestMethod.GET})
	public @ResponseBody ObjectListResult queryMaterialCategory() throws Exception{
		return materialService.queryMaterialCategory();
	}
	
	@RequestMapping(value = "/material/querymaterialbyname", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody ObjectResult queryMaterialByName(
			@RequestParam(value = "name", required = true) String name) throws Exception{
		return materialService.queryMaterialByName(name);
	}
	
	@RequestMapping(value = "/material/querymaterialrecordbymaterial", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody ObjectListResult queryMaterialRecordByMaterial(
			@RequestParam(value = "materialId", required = true) int materialId) throws Exception{
		return materialService.queryMaterialRecordByMaterial(materialId);
	}
	
	@RequestMapping(value = "/material/addmaterialcategory", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult addMaterialCategory(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.addMaterialCategory(userId, name, sequence);
		return result;
	}
	
	@RequestMapping(value = "/material/updatematerialcategory", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult updateMaterialCategory(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "sequence", required = true) int sequence) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.updateMaterialCategory(userId, id, name, sequence);
		return result;
	}
	
	@RequestMapping(value = "/material/deletematerialcategory", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult deleteMaterialCategory(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.deleteMaterialCategory(userId, id);
		return result;
	}
	
	@RequestMapping(value = "/material/addmaterial", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult addMaterial(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "sequence", required = true) int sequence,
			@RequestParam(value = "leftAmount", required = false, defaultValue = "0") double leftAmount,
			@RequestParam(value = "unit", required = true) String unit,
			@RequestParam(value = "alarmAmount", required = false, defaultValue = "0") double alarmAmount,
			@RequestParam(value = "barCode", required = false, defaultValue = "") String barcode,
			@RequestParam(value = "price", required = true) double price,
			@RequestParam(value = "categoryId", required = true) int categoryId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.addMaterial(userId, name, sequence, leftAmount, unit, alarmAmount, categoryId, barcode, price);
		return result;
	}
	
	@RequestMapping(value = "/material/updatematerial", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult updateMaterial(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "sequence", required = true) int sequence,
//			@RequestParam(value = "leftAmount", required = false, defaultValue = "0") double leftAmount,
			@RequestParam(value = "unit", required = true) String unit,
			@RequestParam(value = "alarmAmount", required = false, defaultValue = "0") double alarmAmount,
			@RequestParam(value = "barCode", required = false, defaultValue = "") String barcode,
			@RequestParam(value = "price", required = true) double price,
			@RequestParam(value = "categoryId", required = true) int categoryId) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.updateMaterial(userId, id, name, sequence, unit, alarmAmount, categoryId, barcode, price);
		return result;
	}
	
	@RequestMapping(value = "/material/updatematerialamount", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult updateMaterial(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "leftAmount", required = true) double leftAmount) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.updateMaterialAmount(userId, id, leftAmount);
		return result;
	}
	
	@RequestMapping(value = "/material/purchasematerial", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult purchaseMaterial(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id,
			@RequestParam(value = "amount", required = true) double amount) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.purchaseMaterial(userId, id, amount);
		return result;
	}
	
	@RequestMapping(value = "/material/deletematerial", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectResult deleteMaterial(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "id", required = true) int id) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectResult("no_permission", false);
		}
		ObjectResult result = materialService.deleteMaterial(userId, id);
		return result;
	}
	
	@RequestMapping (value = "/material/statisticsconsume", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody ObjectListResult statisticsConsume(
			@RequestParam(value = "userId", required = true) int userId,
			@RequestParam(value = "startTime", required = true) String sStartTime,
			@RequestParam(value = "usePreDay", required = true) boolean usePreDay,
			@RequestParam(value = "endTime", required = true) String sEndTime) throws Exception{
		if (!permissionService.checkPermission(userId, ConstantValue.PERMISSION_RAWMATERIAL)){
			return new ObjectListResult("no_permission", false);
		}
		Date startTime = ConstantValue.DFYMDHMS.parse(sStartTime);
		Date endTime = ConstantValue.DFYMDHMS.parse(sEndTime);
		ObjectListResult result = materialService.statisticsConsume(startTime, usePreDay, endTime);
		return result;
	}
	
	@RequestMapping (value = "/material/test", method = {RequestMethod.GET})
	public @ResponseBody ObjectResult test() throws Exception{
		ObjectResult result = materialService.test();
		return result;
	}
}
