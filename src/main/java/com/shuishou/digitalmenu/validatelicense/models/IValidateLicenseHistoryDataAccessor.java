package com.shuishou.digitalmenu.validatelicense.models;

public interface IValidateLicenseHistoryDataAccessor {

	/**
	 * 得到最后一条记录
	 * @return
	 */
	ValidateLicenseHistory getLastRecord();
	
	/**
	 * 插入一条记录
	 */
	void insert(ValidateLicenseHistory his);
	
	
}
