package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

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
}
