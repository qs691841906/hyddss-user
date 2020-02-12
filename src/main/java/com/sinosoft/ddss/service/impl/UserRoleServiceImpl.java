package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.UserRole;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.dao.UserMapper;
import com.sinosoft.ddss.dao.UserRoleMapper;
import com.sinosoft.ddss.service.IUserRoleService;
import com.sinosoft.ddss.service.IUserService;


@Service
public class UserRoleServiceImpl implements IUserRoleService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
	@Override
	public List<UserRole> listOfUserRoleById(BigDecimal userId) {
		
		return userRoleMapper.listOfUserRoleById(userId);
	}
}
