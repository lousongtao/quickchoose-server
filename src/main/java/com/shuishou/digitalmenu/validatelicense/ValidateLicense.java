package com.shuishou.digitalmenu.validatelicense;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shuishou.digitalmenu.ServerProperties;
import com.shuishou.digitalmenu.validatelicense.services.IValidateService;

@Component
public class ValidateLicense implements InitializingBean{

	private final static Logger logger = LoggerFactory.getLogger(ValidateLicense.class);
	
	private int delay = 20 * 1000;
	private int interval = 60* 60* 24 * 1000;
	
	@Autowired
	private IValidateService validateService;

	@Override
	public void afterPropertiesSet() throws Exception {
		Timer timer= new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				try{
					logger.debug("start to validate license...");
					validateService.validateLicense(ServerProperties.LICENSECUSTOMERNAME, ServerProperties.LICENSEKEY);
					logger.debug("validate license finish.");
				}catch(Exception e){
					logger.error("", e);
				}
			}}, delay, interval);		
	}
}
