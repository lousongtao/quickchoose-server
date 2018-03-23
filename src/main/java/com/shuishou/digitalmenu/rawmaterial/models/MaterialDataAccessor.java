package com.shuishou.digitalmenu.rawmaterial.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class MaterialDataAccessor extends BaseDataAccessor implements IMaterialDataAccessor {

	@Override
	public Serializable save(Material material) {
		return sessionFactory.getCurrentSession().save(material);
	}

	@Override
	public void update(Material material) {
		sessionFactory.getCurrentSession().update(material);
	}

	@Override
	public void delete(Material material) {
		sessionFactory.getCurrentSession().delete(material);
	}

	@Override
	public Material getMaterialById(int id) {
		String hql = "from Material where id="+id;
		return (Material) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<Material> getAllMaterial() {
		String hql = "from Material";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
	
	@Override
	public List<Material> getAllMaterialWithCategory() {
		String hql = "from Material left join fetch MaterialCategory";
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Override
	public Material getMaterialByName(String name) {
		String hql = "from Material where name='"+name+"'";
		return (Material) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<Material> getMaterialByCategory(int categoryId) {
		String hql = "from Material where category.id="+categoryId;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
