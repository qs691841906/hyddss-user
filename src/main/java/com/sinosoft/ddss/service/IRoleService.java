package com.sinosoft.ddss.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.Role;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.query.RoleQuery;


public interface IRoleService {

	List<Role> listRole(Role role) throws Exception;

	Map<String, Object> listRoleInit(RoleQuery role) throws Exception;

	boolean insertRole(Role role) throws Exception;

	Map<String, Object> deleteById(Role role) throws Exception;

	boolean updateRoleById(Role role) throws Exception;

	Role getRoleById(Role role) throws Exception;

	boolean getRoleByName(Role role) throws Exception;

	List<SysResource> getAllResourcesByRole(BigDecimal roleId)throws Exception;

	List<Role> listRoleByIds(Role role)throws Exception;

}
