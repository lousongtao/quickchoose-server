package com.shuishou.digitalmenu.log.views;

import java.util.ArrayList;
import java.util.List;

import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.models.LogData.LogType;
import com.shuishou.digitalmenu.views.ObjectResult;

public class GetLogTypesResult extends ObjectResult {

	public final static class LogTypeInfo {
		public String type;

		public LogTypeInfo(String _type) {
			type = _type;
		}
	}

	public List<LogTypeInfo> data = new ArrayList<LogTypeInfo>();

	public GetLogTypesResult(String result, boolean success) {
		super(result, success);
		LogType[] logTypes = LogData.LogType.values();
		for (LogType lt : logTypes) {
			data.add(new LogTypeInfo(lt.toString()));
		}

	}
}
