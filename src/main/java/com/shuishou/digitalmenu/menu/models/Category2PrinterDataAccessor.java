package com.shuishou.digitalmenu.menu.models;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class Category2PrinterDataAccessor extends BaseDataAccessor implements ICategory2PrinterDataAccessor {

	@Override
	public Serializable save(Category2Printer category2Printer) {
		return sessionFactory.getCurrentSession().save(category2Printer);
	}

	@Override
	public void update(Category2Printer category2Printer) {
		sessionFactory.getCurrentSession().update(category2Printer);
	}

	@Override
	public void delete(Category2Printer category2Printer) {
		sessionFactory.getCurrentSession().delete(category2Printer);
	}

	@Override
	public Category2Printer getCategory2PrinterById(int id) {
		String hql = "from Category2Printer where id = "+ id;
		return (Category2Printer) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

	@Override
	public List<Category2Printer> getCategory2PrinterByCategory2Id(int category2id){
		String hql = "from Category2Printer where category2.id ="+category2id;
		return sessionFactory.getCurrentSession().createQuery(hql).list();
	}
}
