package com.sinosoft.ddss.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.entity.UserCount;
import com.sinosoft.ddss.dao.UserCountMapper;
import com.sinosoft.ddss.service.IUserCountService;
@Service
@Transactional
public class UserCountServiceImpl implements IUserCountService {
	private static Logger LOGGER = LoggerFactory.getLogger(UserCountServiceImpl.class);

	@Autowired
	private UserCountMapper userCountMapper;
	@Override
	public void insertLoginInfo(UserCount userCount) {
		// TODO Auto-generated method stub
		userCountMapper.insertSelective(userCount);
	}

}
