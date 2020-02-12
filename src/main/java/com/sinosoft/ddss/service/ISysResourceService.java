package com.sinosoft.ddss.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.query.SysResourceQuery;

public interface ISysResourceService {
	List<SysResource> listSysResource(BigDecimal userId) throws Exception;
	Map<String, Object> listResourceInit(SysResourceQuery sysResource) throws Exception;
	List<SysResource> listAuothMenu(String string, String type) throws Exception;
	List<SysResource> selectByResourceid(String resourceid) throws Exception;
	List<SysResource> selectByPid(String pid) throws Exception;
	List<SysResource> getAllMenuResources() throws Exception;
	/**
	 * 根据主键查询权限信息
	 * @author josen
	 * @param sysResource
	 * @return
	 * @throws Exception
	 */
	SysResource getResourceById(SysResourceQuery sysResource) throws Exception;
}
