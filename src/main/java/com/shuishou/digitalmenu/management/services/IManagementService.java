package com.shuishou.digitalmenu.management.services;

import java.util.Date;

import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
import com.shuishou.digitalmenu.views.GridResult;

public interface IManagementService {

	CurrentDutyResult getCurrentDuty();
	
	ShiftWorkResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime);
	
	GridResult startShiftWork(int userId);
	
	GridResult endShiftWork(int userId);
}
