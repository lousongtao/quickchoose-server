package com.shuishou.digitalmenu.rawmaterial.services;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialCategoryDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialRecordDataAccessor;
import com.shuishou.digitalmenu.rawmaterial.models.Material;
import com.shuishou.digitalmenu.rawmaterial.models.MaterialCategory;
import com.shuishou.digitalmenu.rawmaterial.models.MaterialRecord;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class MaterialService implements IMaterialService {
	private final static Logger logger = Logger.getLogger(MaterialService.class);
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private IMaterialCategoryDataAccessor materialCategoryDA;
	
	@Autowired
	private IMaterialDataAccessor materialDA;
	
	@Autowired
	private IMaterialRecordDataAccessor materialRecordDA;
	
	@Override
	@Transactional
	public ObjectResult addMaterialCategory(int userId, String name, int sequence) {
		MaterialCategory mc = new MaterialCategory();
		mc.setName(name);
		mc.setSequence(sequence);
		materialCategoryDA.save(mc);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " add MaterialCategory : " + mc);
		return new ObjectResult(Result.OK, true, mc);
	}

	@Override
	@Transactional
	public ObjectResult updateMaterialCategory(int userId, int id, String name, int sequence) {
		MaterialCategory mc = materialCategoryDA.getMaterialCategoryById(id);
		if (mc == null)
			return new ObjectResult("not find Material by id " + id, false);
		mc.setName(name);
		mc.setSequence(sequence);
		materialCategoryDA.save(mc);
		hibernateInitializeCategory(mc);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " update MaterialCategory : " + mc);
		return new ObjectResult(Result.OK, true, mc);
	}

	@Override
	@Transactional
	public ObjectResult deleteMaterialCategory(int userId, int id) {
		MaterialCategory mc = materialCategoryDA.getMaterialCategoryById(id);
		if (mc == null)
			return new ObjectResult("not find Material by id " + id, false);
		materialCategoryDA.delete(mc);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " delete MaterialCategory : " + mc);
		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult addMaterial(int userId, String name, int sequence, double leftAmount, String unit,
			double alarmAmount, int categoryId, String barCode, double price) {
		MaterialCategory mc = materialCategoryDA.getMaterialCategoryById(categoryId);
		if (mc == null)
			return new ObjectResult("cannot find MaterialCategory by id " + categoryId, false);
		Material m = new Material();
		m.setName(name);
		m.setSequence(sequence);
		m.setLeftAmount(leftAmount);
		m.setUnit(unit);
		m.setAlarmStatus(ConstantValue.MATERIAL_ALARMSTATUS_NOALARM);
		m.setAlarmAmount(alarmAmount);
		m.setMaterialCategory(mc);
		m.setBarCode(barCode);
		m.setPrice(price);
		materialDA.save(m);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " add Material : " + m);
		return new ObjectResult(Result.OK, true, m);
	}

	@Override
	@Transactional
	public ObjectResult updateMaterial(int userId, int id, String name, int sequence, String unit,
			double alarmAmount, int categoryId, String barCode, double price) {
		MaterialCategory mc = materialCategoryDA.getMaterialCategoryById(categoryId);
		if (mc == null)
			return new ObjectResult("cannot find MaterialCategory by id " + categoryId, false);
		Material m = materialDA.getMaterialById(id);
		if (m == null)
			return new ObjectResult("not find Material by id " + id, false);
		m.setName(name);
		m.setSequence(sequence);
//		m.setLeftAmount(leftAmount);
		m.setUnit(unit);
		m.setAlarmAmount(alarmAmount);
		m.setMaterialCategory(mc);
		m.setBarCode(barCode);
		m.setPrice(price);
		materialDA.save(m);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " update Material : " + m);
		return new ObjectResult(Result.OK, true, m);
	}
	
	@Override
	@Transactional
	public ObjectResult updateMaterialAmount(int userId, int id, double leftAmount) {
		Material m = materialDA.getMaterialById(id);
		UserData selfUser = userDA.getUserById(userId);
		if (m == null)
			return new ObjectResult("not find Material by id " + id, false);
		m.setLeftAmount(leftAmount);
		materialDA.save(m);
		MaterialRecord mr = new MaterialRecord();
		mr.setMaterial(m);
		mr.setAmount(leftAmount);
		mr.setLeftAmount(leftAmount);
		mr.setOperator(selfUser.getUsername());
		mr.setType(ConstantValue.MATERIALRECORD_TYPE_CHANGEAMOUNT);
		mr.setDate(new Date());
		materialRecordDA.save(mr);
		
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " update Material : " + m);
		return new ObjectResult(Result.OK, true, m);
	}

	@Override
	@Transactional
	public ObjectResult deleteMaterial(int userId, int id) {
		Material m = materialDA.getMaterialById(id);
		if (m == null)
			return new ObjectResult("not find Material by id " + id, false);
		materialDA.delete(m);
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " delete Material : " + m);
		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectListResult queryMaterialCategory() {
		List<MaterialCategory> listMC = materialCategoryDA.getAllMaterialCategory();
		
		hibernateInitializeCategory(listMC);
		return new ObjectListResult(Result.OK, true, listMC);
	}

	@Override
	@Transactional
	public ObjectListResult queryMaterialByCategoryId(int categoryId) {
		List<Material> listM = materialDA.getMaterialByCategory(categoryId);
		hibernateInitializeMaterial(listM);
		return new ObjectListResult(Result.OK, true, listM);
	}

	@Override
	@Transactional
	public ObjectResult queryMaterialById(int id) {
		Material m = materialDA.getMaterialById(id);
		if (m == null)
			return new ObjectResult("not find Material by id " + id, false);
		hibernateInitializeMaterial(m);
		return new ObjectResult(Result.OK, true, m);
	}

	@Override
	@Transactional
	public ObjectResult queryMaterialByName(String name) {
		Material m = materialDA.getMaterialByName(name);
		if (m == null)
			return new ObjectResult("not find Material by name " + name, false);
		hibernateInitializeMaterial(m);
		return new ObjectResult(Result.OK, true, m);
	}
	
	@Transactional
	private void hibernateInitializeCategory(List<MaterialCategory> listMC){
		for (int i = 0; i < listMC.size(); i++) {
			hibernateInitializeCategory(listMC.get(i));
		}
	}
	
	@Transactional
	private void hibernateInitializeCategory(MaterialCategory mc){
		Hibernate.initialize(mc);
		if (mc.getMaterials() != null)
			hibernateInitializeMaterial(mc.getMaterials());
	}

	@Transactional
	private void hibernateInitializeMaterial(List<Material> listM){
		for (int i = 0; i < listM.size(); i++) {
			hibernateInitializeMaterial(listM.get(i));
		}
	}
	
	@Transactional
	private void hibernateInitializeMaterial(Material m){
		Hibernate.initialize(m);
	}

	@Override
	@Transactional
	public ObjectResult purchaseMaterial(int userId, int materialId, double amount) {
		UserData selfUser = userDA.getUserById(userId);
		Material m = materialDA.getMaterialById(materialId);
		if (m == null)
			return new ObjectResult("not find Material by id " + materialId, false);
		m.setLeftAmount(m.getLeftAmount() + amount);
		materialDA.save(m);
		MaterialRecord mr = new MaterialRecord();
		mr.setMaterial(m);
		mr.setAmount(amount);
		mr.setLeftAmount(m.getLeftAmount());
		mr.setOperator(selfUser.getUsername());
		mr.setType(ConstantValue.MATERIALRECORD_TYPE_PURCHASE);
		mr.setDate(new Date());
		materialRecordDA.save(mr);
		logService.write(selfUser, LogData.LogType.MATERIAL_CHANGE.toString(), "User " + selfUser + " purchase Material : " + m.getName());
		return new ObjectResult(Result.OK, true, m);
	}
	
	@Override
	@Transactional
	public ObjectListResult queryMaterialRecordByMaterial(int materialId){
		List<MaterialRecord> records = materialRecordDA.getMaterialRecordByMaterial(materialId);
		return new ObjectListResult(Result.OK, true, records);
	}
}
