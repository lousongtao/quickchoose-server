package com.shuishou.digitalmenu.common.services;

import com.shuishou.digitalmenu.common.views.GetConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.common.views.GetPrinterResult;
import com.shuishou.digitalmenu.views.GridResult;

public interface ICommonService {

	GridResult checkConfirmCode(String code);
	
	GetConfirmCodeResult getConfirmCode();
	
	GridResult saveConfirmCode(long userId, String code);
	
	GetDeskResult getDesks();
	
	GetDeskWithIndentResult getDesksWithIndents();
	
	GridResult saveDesk(long userId, String deskname);
	
	GridResult updateDesk(long userId, int id, String name);
	
	GridResult deleteDesk(long userId, int id);
	
	GetPrinterResult getPrinters();
	
	GridResult savePrinter(long userId, String name, String printerName, int copy, byte printStyle);
	
	GridResult deletePrinter(long userId, int id);
}
