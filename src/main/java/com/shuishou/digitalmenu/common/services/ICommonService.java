package com.shuishou.digitalmenu.common.services;

import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.common.views.GetDeskResult;
import com.shuishou.digitalmenu.common.views.GetDeskWithIndentResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface ICommonService {

//	CheckConfirmCodeResult checkConfirmCode(String code);
//	
//	GetConfirmCodeResult getConfirmCode();
//	
	ObjectResult saveConfirmCode(long userId, String oldCode, String code);
	
	ObjectResult saveOpenCashdrawerCode(long userId, String oldCode, String code);
	
	ObjectResult saveLanguageSet(long userId, int amount, String firstName, String secondName);
	
	GetDeskResult getDesks();
	
	GetDeskWithIndentResult getDesksWithIndents();
	
	ObjectResult saveDesk(long userId, String deskname, int sequence);
	
	ObjectResult updateDesk(long userId, int id, String name, int sequence);
	
	ObjectResult deleteDesk(long userId, int id);
	
	ObjectListResult getPrinters();
	
	ObjectResult savePrinter(long userId, String name, String printerName, int type);
	
	ObjectResult deletePrinter(long userId, int id);
	
	ObjectResult testPrinterConnection(int id);
	
	ObjectListResult getDiscountTemplates();
	
	ObjectResult saveDiscountTemplate(long userId, String name, double rate);
	
	ObjectResult deleteDiscountTemplate(long userId, int id);
	
	ObjectListResult getPayWays();
	
	ObjectResult savePayWay(long userId, String name);
	
	ObjectResult deletePayWay(long userId, int id);
	
	GetDeskWithIndentResult mergeDesks(int userId, int mainDeskId, String subDesksId);
	
	ObjectResult uploadErrorLog(String machineCode, MultipartFile logfile);
	
	ObjectResult queryConfigMap();
}
