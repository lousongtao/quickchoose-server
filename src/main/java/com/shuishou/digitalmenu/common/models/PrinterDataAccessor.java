package com.shuishou.digitalmenu.common.models;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.shuishou.digitalmenu.models.BaseDataAccessor;

@Repository
public class PrinterDataAccessor extends BaseDataAccessor implements IPrinterDataAccessor {

	@Override
	public List queryPrinters() {
		return sessionFactory.getCurrentSession().createQuery("from Printer order by name").list();
	}

	@Override
	public Serializable insertPrinter(Printer printer) {
		return sessionFactory.getCurrentSession().save(printer);
	}

	@Override
	public void updatePrinter(Printer printer) {
		sessionFactory.getCurrentSession().update(printer);
	}

	@Override
	public void deletePrinter(Printer printer) {
		sessionFactory.getCurrentSession().delete(printer);
	}

	@Override
	public Printer getPrinterById(int id) {
		String hql = "from Printer where id = "+ id;
		
		return (Printer) sessionFactory.getCurrentSession().createQuery(hql).uniqueResult();
	}

}
