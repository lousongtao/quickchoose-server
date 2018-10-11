package com.shuishou.digitalmenu.management.services;

import java.util.Date;

import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;

public interface IManagementService {

	CurrentDutyResult getCurrentDuty();
	
	ObjectListResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime);
	
	CurrentDutyResult startShiftWork(int userId, boolean printLastDutyTicket);
	
	CurrentDutyResult endShiftWork(int userId, boolean printShiftTicket);
	
	ObjectResult printShiftWork(int userId, int shiftWorkId);
}
