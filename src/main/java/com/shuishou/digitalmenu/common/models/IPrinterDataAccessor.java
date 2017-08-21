package com.shuishou.digitalmenu.common.models;

import java.io.Serializable;
import java.util.List;

public interface IPrinterDataAccessor {

	List<Printer> queryPrinters();
	
	Printer getPrinterById(int id);
	
	Serializable insertPrinter(Printer printer);
	
	void updatePrinter(Printer printer);
	
	void deletePrinter(Printer printer);
}
