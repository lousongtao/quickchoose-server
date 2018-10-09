package com.shuishou.digitalmenu.validatelicense.services;

import com.shuishou.digitalmenu.validatelicense.view.ValidateResult;

public interface IValidateService {

	void validateLicense(String customerName, String key);
	
	ValidateResult getValidateResult();
}
