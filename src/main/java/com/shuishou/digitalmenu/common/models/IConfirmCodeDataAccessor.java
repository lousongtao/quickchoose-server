package com.shuishou.digitalmenu.common.models;

public interface IConfirmCodeDataAccessor {

	ConfirmCode getCode();
	
	void saveCode(ConfirmCode code);
	
	void deleteCode();
}
