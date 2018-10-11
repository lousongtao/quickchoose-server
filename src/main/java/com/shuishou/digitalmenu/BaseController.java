package com.shuishou.digitalmenu;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
public class BaseController {

	private Logger logger = Logger.getLogger(BaseController.class);
	
	@ExceptionHandler(value = Throwable.class)
    public void defaultErrorHandler(Throwable e)  {
		logger.error(ConstantValue.DFYMDHMS.format(new Date()));
        logger.error("", e);
        e.printStackTrace();
    }
}
