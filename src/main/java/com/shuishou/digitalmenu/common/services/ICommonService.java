package com.shuishou.digitalmenu.common.services;

import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.common.views.CheckConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetConfirmCodeResult;
import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.common.views.GetDiscountTemplateResult;
import com.shuishou.digitalmenu.common.views.GetPrinterResult;
import com.shuishou.digitalmenu.views.ObjectResult;
import com.shuishou.digitalmenu.views.Result;

public interface ICommonService {

	CheckConfirmCodeResult checkConfirmCode(String code);
	
	GetConfirmCodeResult getConfirmCode();
	
	ObjectResult saveConfirmCode(long userId, String code);
	
	GetDeskResult getDesks();
	
	GetDeskWithIndentResult getDesksWithIndents();
	
	ObjectResult saveDesk(long userId, String deskname, int sequence);
	
	ObjectResult updateDesk(long userId, int id, String name);
	
	ObjectResult deleteDesk(long userId, int id);
	
	GetPrinterResult getPrinters();
	
	ObjectResult savePrinter(long userId, String name, String printerName, int copy, byte printStyle);
	
	ObjectResult deletePrinter(long userId, int id);
	
	GetDiscountTemplateResult getDiscountTemplates();
	
	ObjectResult saveDiscountTemplate(long userId, String name, double rate);
	
	ObjectResult deleteDiscountTemplate(long userId, int id);
	
	GetDeskWithIndentResult mergeDesks(int userId, int mainDeskId, String subDesksId);
	
	ObjectResult uploadErrorLog(String machineCode, MultipartFile logfile);
}
