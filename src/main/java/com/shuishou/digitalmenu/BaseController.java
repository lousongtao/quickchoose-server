package com.shuishou.digitalmenu;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shuishou.digitalmenu.common.services.CommonService;

@Controller
public class BaseController {

	private Logger logger = Logger.getLogger(BaseController.class);
	
	@ExceptionHandler(value = Throwable.class)
    public void defaultErrorHandler(Throwable e)  {
        logger.error("", e);
    }
}
