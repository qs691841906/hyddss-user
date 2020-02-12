package com.sinosoft.ddss.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.entity.EmailTask;
import com.sinosoft.ddss.dao.EmailTaskMapper;
import com.sinosoft.ddss.service.IEmailTaskService;
@Service
@Transactional
public class EmailTaskService implements IEmailTaskService {

	@Autowired
	private EmailTaskMapper emailTaskMapper;
	@Override
	public boolean saveEmail(EmailTask emailTask) throws Exception {
		// TODO Auto-generated method stub
		int i = emailTaskMapper.insertSelective(emailTask);
		if(i>0){
			return true;
		}
		return false;
	}

}
