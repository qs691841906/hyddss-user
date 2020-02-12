package com.sinosoft.ddss.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.dao.SysResourceMapper;
import com.sinosoft.ddss.service.IRoleResourceService;

@Service
public class RoleResourceServiceImpl implements IRoleResourceService{
	private static Logger LOGGER = LoggerFactory.getLogger(RoleResourceServiceImpl.class);
	 @Autowired
	 private SysResourceMapper sysResourceMapper;
	 
}
