package com.shuishou.digitalmenu.management.services;

import java.util.Date;

import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

public interface IManagementService {

	CurrentDutyResult getCurrentDuty();
	
	ShiftWorkResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime);
	
	CurrentDutyResult startShiftWork(int userId);
	
	CurrentDutyResult endShiftWork(int userId, Date startTime, boolean printShiftTicket);
	
	GridResult printShiftWork(int userId, int shiftWorkId);
}
