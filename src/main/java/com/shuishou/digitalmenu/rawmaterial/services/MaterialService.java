package com.shuishou.digitalmenu.rawmaterial.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.shuishou.digitalmenu.rawmaterial.view.MaterialRecordInfo;
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

	/**
	 * 1.	取得所有的material
	 * 2.	遍历material, 根据时间和materialid, 查找对应的record(列表), 并根据record.id排序
	 * 3.	上一步的record列表, 取第一个和最后一个点货记录, 其差值作为消耗量
	 * 4.	统计前述列表中的进货记录, 加上消耗量, 作为最新的消耗量值
	 * 5.	将materialId, 消耗量, 采购量返回客户端
	 * 6.	客户端根据materialId, 组装material和category的信息
	 * 
	 * @param usePreDay 使用前一天的数据作为统计值, 有的店家是晚上盘点, 所以当日消耗要使用前一天的数据作为起始数据
	 * 
	 * TODO: 目前只做usePreDay=false的情况
	 */
	@Override
	@Transactional
	public ObjectListResult statisticsConsume(Date startTime, boolean usePreDay, Date endTime) {
		long l1 = System.currentTimeMillis();
		List<Material> materials = materialDA.getAllMaterial();
		
		ArrayList<MaterialRecordInfo> infolist = new ArrayList<>();
		//1. 查找所有的material, 按循序查找对应的点货记录, 时间范围在startTime和endTime之间
//		List<Material> materials = materialDA.getAllMaterial();
		for (int i = 0; i < materials.size(); i++) {
			Material m = materials.get(i);
			MaterialRecordInfo info = new MaterialRecordInfo(m.getId(), m.getName(), m.getMaterialCategory().getName(), m.getUnit(), m.getPrice());
			infolist.add(info);
			//根据material.id和时间, 查询这段时间内的record, 返回结果已按照id排序
			List<MaterialRecord> recordList = materialRecordDA.getMaterialRecordByTime(m.getId(), startTime, endTime);
			Double firstRecordAmount = null; //使用对象Double, 后面用非空判断, 是否有足够的数据进行统计
			Double endRecordAmount = null; //使用对象Double, 后面用非空判断, 是否有足够的数据进行统计
			double purchase = 0;
			if (recordList != null && !recordList.isEmpty()){
				for (int j = 0; j < recordList.size(); j++) {
					MaterialRecord mr = recordList.get(j);
					if (mr.getType() == ConstantValue.MATERIALRECORD_TYPE_PURCHASE){
						purchase += mr.getAmount();
					}
					if (firstRecordAmount == null){
						//如果第一条是销售的记录, 则其实数量是leftAmount + Amount
						if (mr.getType() == ConstantValue.MATERIALRECORD_TYPE_SELLDISH){
							firstRecordAmount = mr.getAmount() + mr.getLeftAmount();
						} 
						//如果第一条记录是修改数量, 那直接取这个的LeftAmount
						else if (mr.getType() == ConstantValue.MATERIALRECORD_TYPE_CHANGEAMOUNT){
							firstRecordAmount = mr.getLeftAmount();
						}
					} else {
						//当firstRecordAmount不为空时, 后面的记录依次设定到endRecordAmount上面, 这样在循环结束后, 最终的值就是endRecordAmount
						if (mr.getType() == ConstantValue.MATERIALRECORD_TYPE_SELLDISH){
							endRecordAmount = mr.getAmount() + mr.getLeftAmount();
						} else if (mr.getType() == ConstantValue.MATERIALRECORD_TYPE_CHANGEAMOUNT){
							endRecordAmount = mr.getLeftAmount();
						}
					}
				}
			}
			info.purchaseAmount = purchase;
			if (firstRecordAmount != null && endRecordAmount != null){
				info.consumeAmount = firstRecordAmount - endRecordAmount + purchase;
				info.totalPrice = info.consumeAmount * info.price;
			}
		}
		long l2 = System.currentTimeMillis();
		logger.debug("query material records in time " + (l2 - l1));
		
		return new ObjectListResult(Result.OK, true, infolist);
	}
	
	@Override
	@Transactional
	public ObjectResult test() {
		
		return new ObjectResult(Result.OK, true);
	}
}
