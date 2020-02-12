
package com.sinosoft.ddss.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.DedicatedLine;
import com.sinosoft.ddss.common.entity.DedicatedUser;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.DedicatedQuery;

public interface IDedicatedService {

	Map<String, Object> listdedicatedInit(DedicatedQuery dedicatedQuery) throws Exception;

	boolean saveDedicatedUser(DedicatedLine dedicatedLine) throws Exception;

	DedicatedLine queryDedicatedLineById(String id) throws Exception;

	boolean updateDedicatedLine(DedicatedLine dedicatedLine) throws Exception;

	boolean deleteDedicatedLine(String id) throws Exception;

	List<DedicatedUser> showUserAuth(String id) throws Exception;

	boolean checkDedicatedName(String ftpName) throws Exception;

	void updateDedicatedUser(String id, String ids)throws Exception;

	DedicatedUser getDedicatedAuthBuId(BigDecimal userId)throws Exception;

}
