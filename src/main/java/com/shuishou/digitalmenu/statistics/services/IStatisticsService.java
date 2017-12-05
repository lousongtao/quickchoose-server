package com.shuishou.digitalmenu.statistics.services;

import java.util.Date;

import com.shuishou.digitalmenu.views.ObjectResult;

public interface IStatisticsService {
	ObjectResult statistics(int userId, Date startDate, Date endDate, int dimension, int sellGranularity, int sellByPeriod);
}
