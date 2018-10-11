package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MaterialRecordDataAccessor extends BaseDataAccessor implements IMaterialRecordDataAccessor {

	@Override
	public Serializable save(MaterialRecord materialRecord) {
		return sessionFactory.getCurrentSession().save(materialRecord);
	}

	@Override
	public void update(MaterialRecord materialRecord) {
		sessionFactory.getCurrentSession().update(materialRecord);
	}

	@Override
	public void delete(MaterialRecord materialRecord) {
		sessionFactory.getCurrentSession().delete(materialRecord);
	}

	@Override
	public MaterialRecord getMaterialRecordById(int id) {
		String hql = "from MaterialRecord where id="+id;
		return (MaterialRecord) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<MaterialRecord> getMaterialRecordByMaterial(int materialId) {
		String hql = "from MaterialRecord where material.id="+materialId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public List<MaterialRecord> getMaterialRecordByTime(int materialId, Date startTime, Date endTime) {
		String hql = "from MaterialRecord where date >= ? and date <= ? and material.id = " + materialId + " order by id";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		query.setDate(0, startTime);
		query.setDate(1, endTime);
		
		return query.list();
	}
}
