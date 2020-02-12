package com.sinosoft.ddss.service;

import com.sinosoft.ddss.common.entity.EmailTask;

public interface IEmailTaskService {
	boolean saveEmail(EmailTask emailTask)throws Exception;
}
