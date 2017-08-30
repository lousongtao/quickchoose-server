package com.shuishou.digitalmenu.management.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.management.models.IShiftWorkDataAccessor;
import com.shuishou.digitalmenu.management.models.ShiftWork;
import com.shuishou.digitalmenu.management.views.CurrentDutyResult;
import com.shuishou.digitalmenu.management.views.ShiftWorkResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;

@Service
public class ManagementService implements IManagementService{

	@Autowired
	private IShiftWorkDataAccessor shiftWorkDA;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private ILogService logService;
	
	private DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
	
	@Override
	@Transactional
	public CurrentDutyResult getCurrentDuty() {
		ShiftWork sw = shiftWorkDA.getLastShiftWork();
		if (sw == null){
			return new CurrentDutyResult(Result.OK, true);
		} else {
			return new CurrentDutyResult(Result.OK, true, sw.getUserName()); 
		}
	}

	@Override
	@Transactional
	public ShiftWorkResult getShiftWorkList(int userId, int start, int limit, String shiftName, Date startTime, Date endTime) {
		List<ShiftWork> sws = shiftWorkDA.queryShiftWork(start, limit, shiftName, startTime, endTime);
		if (sws == null || sws.isEmpty())
			return new ShiftWorkResult(Result.OK, true);
		/**
		 * 组合交接班记录, 找到一条上班记录, 将swiftName置为该user, 找到一个下班记录, 讲swiftName置空.
		 * 如果swiftName非空但是找到一个下班记录, 则表示数据有错误.
		 * 
		 */
		ShiftWorkResult.ShiftWork swInfo = null;
		List<ShiftWorkResult.ShiftWork> swInfos = new ArrayList<ShiftWorkResult.ShiftWork>();
		
		for (int i = 0; i < sws.size(); i++) {
			ShiftWork sw = sws.get(i);
			if (sw.getStatus() == ConstantValue.SHIFTWORK_ONWORK){
				swInfo = new ShiftWorkResult.ShiftWork();
				swInfo.id = sw.getId();
				swInfo.userName = sw.getUserName();
				swInfo.startTime = df.format(sw.getTime());
				swInfos.add(swInfo);
			} else if (sw.getStatus() == ConstantValue.SHIFTWORK_OFFWORK){
				if (swInfo != null && swInfo.userName.equals(sw.getUserName())){
					swInfo.endTime = df.format(sw.getTime());
				}
			}
		}
		ShiftWorkResult result = new ShiftWorkResult(Result.OK, true);
		result.shiftWorks = swInfos;
		return result;
	}

	@Override
	@Transactional
	public GridResult startShiftWork(int userId) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new GridResult(Result.FAIL, false);
		}
		ShiftWork sw = new ShiftWork();
		sw.setUserName(user.getUsername());
		sw.setStatus(ConstantValue.SHIFTWORK_ONWORK);
		sw.setTime(new Date());
		shiftWorkDA.insertShitWork(sw);
		logService.write(user, LogData.LogType.SHIFTWORK.toString(),
				"User " + user.getUsername() + " start work.");
		return new GridResult(Result.OK, true);
	}

	@Override
	@Transactional
	public GridResult endShiftWork(int userId) {
		UserData user = userDA.getUserById(userId);
		if (user == null){
			return new GridResult(Result.FAIL, false);
		}
		ShiftWork sw = new ShiftWork();
		sw.setUserName(user.getUsername());
		sw.setStatus(ConstantValue.SHIFTWORK_OFFWORK);
		sw.setTime(new Date());
		shiftWorkDA.insertShitWork(sw);
		logService.write(user, LogData.LogType.SHIFTWORK.toString(),
				"User " + user.getUsername() + " end work.");
		return new GridResult(Result.OK, true);
	}
}
