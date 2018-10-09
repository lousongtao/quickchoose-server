package com.shuishou.digitalmenu.validatelicense.services;

import java.util.Calendar;
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
import com.shuishou.digitalmenu.account.views.LoginResult;
import com.shuishou.digitalmenu.member.services.HttpResult;
import com.shuishou.digitalmenu.member.services.HttpUtil;
import com.shuishou.digitalmenu.validatelicense.ValidateLicense;
import com.shuishou.digitalmenu.validatelicense.models.IValidateLicenseHistoryDataAccessor;
import com.shuishou.digitalmenu.validatelicense.models.ValidateLicenseHistory;
import com.shuishou.digitalmenu.validatelicense.view.ValidateResult;
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
	
	/**
	 * 校验license是否过期, 读取最后一条验证信息his, (注意, 如果从网络上能够获得到验证结果, 即使是过期的license, 也会标记为成功; 标记失败只有两种可能, 网络连接异常或者用户名key值不匹配.)
	 * 1. 读取不到验证信息, 返回正确结果. 这种情况应该是验证server出错, 为不影响业务, 返回正确结果.
	 * 2. his的错误次数超过上限, 返回错误结果, 并给予提示. 
	 * 3. his的错误次数不超过上限, 返回正确结果, 并附带一个告警提示.
	 * 4. his的错误次数不超过上限, 但是expire date过期超过上限值, 返回错误结果, 附带提示信息.
	 * 5. his的错误次数不超过上限, 但是当前日期已接近expire date, 返回正确结果, 附带提示信息.
	 * 6. expire date为空, 不比较expire date, 这种情况下只比较错误次数.
	 * 7. 其他情况, 返回正确结果.
	 * 考虑到网络连接失败或者服务器停机, 最后得到的验证信息没有expire date, 要注意避免空指针异常, 同时要跳过这个日期检查.
	 * 
	 */
	@Transactional
	@Override
	public ValidateResult getValidateResult(){
		ValidateLicenseHistory his = vlhDA.getLastRecord();
		if (his != null){
			if (his.getFailureTimes() > ConstantValue.VALIDATELICENSE_FAILEDTIMES){
				return new ValidateResult(false, "Too many failed validations for license.");
			} else if (his.getFailureTimes() > 0){
				return new ValidateResult(true, "License validation processor occurs exceptions recently. To keep software run perfectly, please report the phenomenon to administrator. The last error is " + his.getFailureReason());
			}
			
			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			
			//如果expire date是空, c1就是当前日期, c1==c2. 此时通过日期无法判断license是否过期, 只能等失败次数积累到权限值以后, 由失败次数判断license过期.
			if (his.getExpireDate() != null){
				c1.setTime(his.getExpireDate());
			} 
			if ((c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 60 * 60 * 24) > ConstantValue.VALIDATELICENSE_EXPIREDAYS){
				return new ValidateResult(false, "License is expired.");
			} else if (Math.abs((c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 60 * 60 * 24)) < ConstantValue.VALIDATELICENSE_EXPIREDAYS){
				//如果expire date为空, 不做提醒
				if (his.getExpireDate() != null){
					return new ValidateResult(true, "The license will be expired in " + (int)((c2.getTimeInMillis() - c1.getTimeInMillis()) / (1000 * 60 * 60 * 24)) + " days. Please recharge the fee and inform this to administrator.");
				}
			}
		}
		return new ValidateResult(true, null);
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
