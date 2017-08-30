package com.shuishou.digitalmenu.management.models;

import java.util.Date;
import java.util.List;

public interface IShiftWorkDataAccessor {

	ShiftWork getLastShiftWork();
	
	List<ShiftWork> queryShiftWork(int start, int limit,String shiftName, Date startTime, Date endTime);
	
	void insertShitWork(ShiftWork sw);
}
