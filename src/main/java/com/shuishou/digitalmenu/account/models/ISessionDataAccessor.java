/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.models;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.Session;

public interface ISessionDataAccessor {

  Session getSession();

  void persistSession(SessionData session);

  void updateSession(SessionData session);

  void deleteSession(SessionData session);

  Serializable saveSession(SessionData session);

  void saveOrUpdateSession(SessionData session);

  SessionData getSessionById(UUID id);

  SessionData getSessionByUser(long userId);

}
