package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MaterialCategoryDataAccessor extends BaseDataAccessor implements IMaterialCategoryDataAccessor {

	@Override
	public Serializable save(MaterialCategory materialCategory) {
		return sessionFactory.getCurrentSession().save(materialCategory);
	}

	@Override
	public void update(MaterialCategory materialCategory) {
		sessionFactory.getCurrentSession().update(materialCategory);
	}

	@Override
	public void delete(MaterialCategory materialCategory) {
		sessionFactory.getCurrentSession().delete(materialCategory);
	}

	@Override
	public MaterialCategory getMaterialCategoryById(int id) {
		String hql = "from MaterialCategory where id="+id;
		return (MaterialCategory) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<MaterialCategory> getAllMaterialCategory() {
		String hql = "from MaterialCategory";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
