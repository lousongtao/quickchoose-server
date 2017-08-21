/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


@NamedQueries({
  @NamedQuery(
    name = "getSessionByUser",
    query = "select s from SessionData s where s.user.id = :userId"
  ),
})
@Entity
@Table(name = "session")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "Account")
public class SessionData {
  
  /**
   * the expired time.
   */
  public final static long EXPIRED_TIME = 12 * 60L * 60L * 1000L;
  
  /**
   * the id.
   */
  @Id
  @Type(type = "uuid-binary")
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  @Column(name = "id", unique = true, length = 16, nullable = false)
  private UUID id;
  
  /**
   * the user.
   */
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name="user_id")
  private UserData user;
  
  /**
   * the expired time.
   */
  @Column(name = "expired_time", nullable = false)
  private Date expiredTime = new Date(System.currentTimeMillis() + EXPIRED_TIME);

  /**
   * @return the id
   */
  public UUID getId() {
    return id;
  }

  /**
   * @return the user
   */
  public UserData getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(UserData user) {
    this.user = user;
  }

  /**
   * @return the expiredTime
   */
  public Date getExpiredTime() {
    return expiredTime;
  }

  /**
   * @param expiredTime the expiredTime to set
   */
  public void setExpiredTime(Date expiredTime) {
    this.expiredTime = expiredTime;
  }

}
