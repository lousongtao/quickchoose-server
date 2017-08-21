/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.log.services;


import java.util.Date;

import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.views.GetLogsResult;


/**
 * @author zhing
 * the log service.
 */
public interface ILogService {

  /**
   * write log to database
   * @param user    operator object
   * @param type    log type
   * @param message log message
   * @return
   */
  LogData write(UserData user, String type, String message);
  
  /**
   * query log record
   * @param start       start number of the query
   * @param limit       limitation of count of record
   * @param username    
   * @param beginTime
   * @param endTime
   * @param type
   * @param message
   * @return
   */
  GetLogsResult queryLog(int start, int limit, String username, Date beginTime, Date endTime, String type, String message);
}
