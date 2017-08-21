/**
 * Copyright 2012 - 2013 Fglinxun Studios, Inc.
 * All rights reserved.
 */
package com.shuishou.digitalmenu.account.services;

import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.account.views.GetAccountsResult;
import com.shuishou.digitalmenu.account.views.LoginResult;
import com.shuishou.digitalmenu.views.Result;



public interface IAccountService {
  
  /**
   * create the root user
   * @return UserData 
   */
  UserData createRoot();

  /**
   * create user by using the parameters name and password
   * @param username
   * @param password
   * @return
   */
  UserData createUser(String username, String password);
  
  /**
   * query user info depending on ID
   * @param id
   * @return
   */
  UserData getUserById(long id);

  /**
   * authorise user login
   * @param username
   * @param password
   * @return
   */
  LoginResult auth(String username, String password);
  
  /**
   * check the user session expired time
   * @param userId
   * @param sessionId
   * @return
   */
  boolean checkSession(long userId, String sessionId);

  /**
   * query user record
   * @return
   */
  GetAccountsResult getAccounts();

  /**
   * add user record
   * @param userId         operator user id
   * @param username       name of new record
   * @param password       password of new record
   * @param passwordAgain  password of new record
   * @param permGroupId    belong permission_group
   * @return
   */
  Result addAccount(long userId, String username, String password, String permission);
  
  /**
   * change password for one user record
   * @param userId            target user record for changing password
   * @param oldPassword       old password
   * @param newPassword       new password
   * @param newPasswordAgain  new password again
   * @return
   */
  Result changePassword(long userId, String oldPassword, String newPassword);

  /**
   * modify user record
   * @param userId           target user 
   * @param username         new user name
   * @param password         new password
   * @param passwordAgain    new password again
   * @param permGroupId      new permission group list
   * @return
   */
  Result modifyAccount(long operateUserId, long userId, String username, String permission);

  /**
   * delete user record
   * @param userId     operator id
   * @param id         user record id
   * @return
   */
  Result removeAccount(long userId, long id);

}
