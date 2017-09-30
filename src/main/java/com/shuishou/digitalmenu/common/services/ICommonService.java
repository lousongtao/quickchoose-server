package com.shuishou.digitalmenu.common.services;

import com.shuishou.digitalmenu.common.views.CheckConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.common.views.GetDiscountTemplateResult;
import com.shuishou.digitalmenu.common.views.GetPrinterResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

public interface ICommonService {

	CheckConfirmCodeResult checkConfirmCode(String code);
	
	GetConfirmCodeResult getConfirmCode();
	
	GridResult saveConfirmCode(long userId, String code);
	
	GetDeskResult getDesks();
	
	GetDeskWithIndentResult getDesksWithIndents();
	
	GridResult saveDesk(long userId, String deskname, int sequence);
	
	GridResult updateDesk(long userId, int id, String name);
	
	GridResult deleteDesk(long userId, int id);
	
	GetPrinterResult getPrinters();
	
	GridResult savePrinter(long userId, String name, String printerName, int copy, byte printStyle);
	
	GridResult deletePrinter(long userId, int id);
	
	GetDiscountTemplateResult getDiscountTemplates();
	
	GridResult saveDiscountTemplate(long userId, String name, double rate);
	
	GridResult deleteDiscountTemplate(long userId, int id);
	
	GetDeskWithIndentResult mergeDesks(int userId, int mainDeskId, String subDesksId);
}
