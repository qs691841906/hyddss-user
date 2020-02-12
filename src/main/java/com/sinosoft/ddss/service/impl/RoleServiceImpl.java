package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.Role;
import com.sinosoft.ddss.common.entity.RoleResource;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.query.RoleQuery;
import com.sinosoft.ddss.dao.RoleMapper;
import com.sinosoft.ddss.dao.RoleResourceMapper;
import com.sinosoft.ddss.dao.SysResourceMapper;
import com.sinosoft.ddss.dao.UserRoleMapper;
import com.sinosoft.ddss.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService {
	private static Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private RoleResourceMapper roleResourceMapper;
	@Autowired
	private SysResourceMapper resourceMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Override
	public List<Role> listRole(Role role) throws Exception {
		return roleMapper.listRole(role);

	}

	@Override
	public Map<String, Object> listRoleInit(RoleQuery roleQuery) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// get list
			List<Role> listRole = roleMapper.listRoleInit(roleQuery);
			// get count
			Integer countRole = roleMapper.countRole(roleQuery);
			TotalInfo totalInfo = new TotalInfo(countRole, roleQuery.getPageSize(), roleQuery.getPage(),
					roleQuery.getStartNum());
			LOGGER.info("角色初始化查询成功");
			map.put("data", listRole);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "角色初始化查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("角色初始化查询异常");
			map.put("status", false);
			map.put("msg", "角色初始化查询异常");
		}
		return map;
	}

	@Override
	public boolean insertRole(Role role) throws Exception {
		boolean boo = false;
		Integer i = roleMapper.insertSelective(role);
		if (i.compareTo(new Integer(1)) == 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public Map<String, Object> deleteById(Role role) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer count = userRoleMapper.getCountByRoleId(role.getRoleId());
		if(count<=0){
			//delete role
			Integer i1 = roleMapper.deleteByPrimaryKey(role.getRoleId());
			//delete roleId in resource&role table
			Integer countByRoleId = roleResourceMapper.getCountByRoleId(role.getRoleId());
			if(countByRoleId>0){
				Integer i2 = roleResourceMapper.deleteByRoleId(role.getRoleId());
			}
			//return success
			map.put("status", true);
			map.put("msg", "角色删除成功");
		} else {
			// return role is used
			map.put("status", false);
			map.put("msg", "该角色已分配，不可删除");
		}
		return map;

	}

	@Override
	public boolean updateRoleById(Role role) throws Exception {
		boolean boo = false;
		//先修改角色信息
		Integer i = roleMapper.updateByPrimaryKey(role);
		if (i.compareTo(new Integer(1)) == 0) {
			//修改角色权限中间表 01 先删除 02 在增加
			BigDecimal roleId = role.getRoleId();
			String sourceIds = role.getSourceIds();
			//01 delete
			Integer i2 = roleResourceMapper.deleteByRoleId(roleId);
			if(!StringUtils.isBlank(sourceIds)){
				String[] split = sourceIds.split(",");
				List<RoleResource> list = new ArrayList<RoleResource>();
				for (String id : split) {
					RoleResource rr = new RoleResource();
					rr.setRoleId(roleId);
					long rid = Long.valueOf(id);
					rr.setResourceId(BigDecimal.valueOf(rid));
					list.add(rr);
				}
				// 02 add
				Integer i3 = roleResourceMapper.insertRoleResourceByList(list);
				boo = true;
			}
		} else {
			boo = false;
		}
		//update redis info ?
		return boo;

	}

	@Override
	public Role getRoleById(Role role) throws Exception {
		Role ro = roleMapper.selectByPrimaryKey(role.getRoleId());
		return ro;

	}

	@Override
//	查询角色名称是否存在
	public boolean getRoleByName(Role role) throws Exception {
		boolean boo = false;
		Role ro = roleMapper.getRoleByName(role);
		if (ro != null) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;

	}

	@Override
	public List<SysResource> getAllResourcesByRole(BigDecimal roleId) throws Exception {
		// 首先，获取当前角色能够访问的资源信息
		List<BigDecimal> roleIdList = new ArrayList<BigDecimal>();
		roleIdList.add(roleId);
		List<RoleResource> roleResourceList = roleResourceMapper.getRoleResourceInfoByRoleIdList(roleIdList);
		List<SysResource> resourcesMenuList = new ArrayList<SysResource>();
		// 获取到资源模块与模块下资源的集合
		List<BigDecimal> resourceIdList = new ArrayList<BigDecimal>();
		for (RoleResource roleResource : roleResourceList) {
			resourceIdList.add(roleResource.getResourceId());
		}
		List<SysResource> resourcesList = resourceMapper.getAllMenuResources();
		// 在系统所有资源的集合中，为每个元素打上标记 表明当前角色是否有权可以访问该资源
		if (roleResourceList != null && roleResourceList.size() > 0) {
			
			
//			Iterator<SysResource> iter = resourcesList.iterator();
//			while(iter.hasNext()){
//				//list.remove(0);
//				SysResource it= iter.next();
//				if(!String.valueOf(roleId).equals("2")&&it.getEnname().equals("UpdateDataStatus")){
//					iter.remove();
//				}
//				for (RoleResource roleRes : roleResourceList) {
//					if (roleRes.getResourceId().compareTo(it.getResourceid()) == 0) {
//						it.setHaveAuth(true);
//					}
//				}
//			}
			
			
			for (SysResource resources : resourcesList) {
				for (RoleResource roleRes : roleResourceList) {
					if (roleRes.getResourceId().compareTo(resources.getResourceid()) == 0) {
						resources.setHaveAuth(true);
					}
				}
			}
			
			for (SysResource resources : resourcesList) {
//				如果用户不是超级管理员就将“修改数据存储状态”权限去掉
				if(!String.valueOf(roleId).equals("2")&&resources.getEnname().equals("UpdateDataStatus")){
					resourcesList.remove(resources);
					break;
				}
			}
			
			resourcesMenuList = this.treeMenuList(resourcesList, BigDecimal.valueOf(0));
		} else {
			resourcesMenuList = this.treeMenuList(resourcesList, BigDecimal.valueOf(0));
		}
		return resourcesMenuList;
	}

	public List<SysResource> treeMenuList(List<SysResource> menuList, BigDecimal parentId) {
		List<SysResource> childMenu = new ArrayList<SysResource>();
		for (SysResource r : menuList) {
			BigDecimal menuId = r.getResourceid();
			BigDecimal pid = r.getPid();
			if (parentId.equals(pid)) {
				List<SysResource> c_node = treeMenuList(menuList, menuId);
				r.setResourceList(c_node);
				childMenu.add(r);
			}
		}
		return childMenu;
	}

	@Override
	public List<Role> listRoleByIds(Role role) throws Exception {
			return roleMapper.listRoleByIds(role);
			
	}
}
