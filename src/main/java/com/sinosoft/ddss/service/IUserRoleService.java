package com.sinosoft.ddss.service;

import java.math.BigDecimal;
import java.util.List;

import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.UserRole;


public interface IUserRoleService {

	List<UserRole> listOfUserRoleById(BigDecimal userId);
}
