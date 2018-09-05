package com.shuishou.digitalmenu.validatelicense.services;

import com.shuishou.digitalmenu.views.ObjectResult;

public interface IValidateService {

	void validateLicense(String customerName, String key);
}
