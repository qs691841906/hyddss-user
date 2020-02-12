package com.sinosoft.ddss.service;

import java.math.BigDecimal;
import java.util.List;

import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;

public interface IUserUtil {
	String getCurrentToken();

	BigDecimal getCurrentPrinciple();

	String getCurrentName();

	User getCurrentAuthentication();

	List<SysResource> getCurrentAuthorities();
}
