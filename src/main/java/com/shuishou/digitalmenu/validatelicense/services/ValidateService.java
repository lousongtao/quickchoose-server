package com.shuishou.digitalmenu.validatelicense.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.ServerProperties;
import com.shuishou.digitalmenu.member.services.HttpResult;
import com.shuishou.digitalmenu.member.services.HttpUtil;
import com.shuishou.digitalmenu.validatelicense.ValidateLicense;
import com.shuishou.digitalmenu.validatelicense.models.IValidateLicenseHistoryDataAccessor;
import com.shuishou.digitalmenu.validatelicense.models.ValidateLicenseHistory;
import com.shuishou.digitalmenu.views.ObjectResult;

@Service
public class ValidateService implements IValidateService{

	private final static Logger logger = LoggerFactory.getLogger(ValidateService.class);
	
	@Autowired
	private IValidateLicenseHistoryDataAccessor vlhDA;
	/**
	 * license 验证结果
	 * 1. 网络连接不通, 记录错误, 错误原因"网络连接不通"
	 * 2. 返回license为null, 可能原因 customerName和key不匹配.
	 * 3. license过期
	 * 
	 * 如果成功, 增加一个验证记录;
	 * 如果失败, 记录失败原因, 同时检查上一条记录, 如果上一条为成功, 标记该记录的失败次数为1; 如果上一条失败, 标记该记录的失败次数增1
	 */
	@Override
	@Transactional
	public void validateLicense(String customerName, String key) {
		logger.debug("enter validate service");
		Map<String, String> params = new HashMap<>();
		params.put("customerName", ServerProperties.LICENSECUSTOMERNAME);
		params.put("key", ServerProperties.LICENSEKEY);
		
		logger.debug("validate service, before cloud service ");
		String response = HttpUtil.getJSONObjectByPost(ServerProperties.LICENSEURL, params);
		logger.debug("validate service, cloud service response " + response);
		if (response == null){
			recordErrorValidate("network exception");
			return;
		}
		Gson gson = new GsonBuilder().setDateFormat(ConstantValue.DATE_PATTERN_YMD).create();
		HttpResult<License> result = gson.fromJson(response, new TypeToken<HttpResult<License>>(){}.getType());
		if (result != null && result.success){
			logger.debug("validate service, result != null && result.success");
			//不管expire date是否过期, 都记录为成功验证
			License l = result.data;
			ValidateLicenseHistory his = new ValidateLicenseHistory();
			his.setValidateDate(new Date());
			his.setStatus(ConstantValue.VALIDATELICENSE_STATUS_SECCESS);
			his.setExpireDate(l.expireDate);
			his.setFailureTimes(0);
			vlhDA.insert(his);
		} else {
			logger.debug("validate service, else ");
			ValidateLicenseHistory lastHis = vlhDA.getLastRecord();
			ValidateLicenseHistory his = new ValidateLicenseHistory();
			his.setValidateDate(new Date());
			his.setStatus(ConstantValue.VALIDATELICENSE_STATUS_FAILED);
			//密码不匹配
			if (result == null){
				recordErrorValidate("Exception occured for connect cloud server.");
			} else if (result.data == null){
				recordErrorValidate("customer and key are not matched");
			}
		}
	}
	
	@Transactional
	private void recordErrorValidate(String reason){
		ValidateLicenseHistory lastHis = vlhDA.getLastRecord();
		ValidateLicenseHistory his = new ValidateLicenseHistory();
		his.setValidateDate(new Date());
		his.setStatus(ConstantValue.VALIDATELICENSE_STATUS_FAILED);
		his.setFailureReason(reason);
		his.setExpireDate(new Date(90, 1,1));
		if (lastHis == null){
			his.setFailureTimes(1);
		} else {
			his.setFailureTimes(lastHis.getFailureTimes() + 1);
		}
		vlhDA.insert(his);
	}
	

	class License{
		public int id;
		
		public String customerName;
		
		public String customerKey;
		
		@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
		public Date createDate;
		
		@JsonFormat(pattern=ConstantValue.DATE_PATTERN_YMDHMS, timezone = "GMT+8:00")
		public Date expireDate;
	}
}
