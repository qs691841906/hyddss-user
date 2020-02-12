package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.query.SysResourceQuery;
import com.sinosoft.ddss.dao.SysResourceMapper;
import com.sinosoft.ddss.service.ISysResourceService;

@Service
public class SysResourceServiceImpl implements ISysResourceService{
	private static Logger LOGGER = LoggerFactory.getLogger(RoleResourceServiceImpl.class);
	 @Autowired
	 private SysResourceMapper sysResourceMapper;

	@Override
	public List<SysResource> listSysResource(BigDecimal userId) throws Exception {
		return sysResourceMapper.listSysResource(userId);
	}

	@Override
	public Map<String, Object> listResourceInit(SysResourceQuery sysResource) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//get list
			List<SysResource> listReource = sysResourceMapper.listReource(sysResource);
			//get count
			Integer countSource = sysResourceMapper.countSource(sysResource);
			TotalInfo totalInfo = new TotalInfo(countSource, sysResource.getPageSize(), sysResource.getPage(), sysResource.getStartNum());
			LOGGER.info("权限初始化查询成功");
			map.put("data", listReource);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "权限初始化查询成功");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("权限初始化查询异常");
			map.put("status", false);
			map.put("msg", "权限初始化查询异常");
		}
		return map;
	}

	@Override
	public List<SysResource> listAuothMenu(String userId, String type) throws Exception {
		List<SysResource> listAuothMenu =  sysResourceMapper.listAuothMenu(userId,type);
		return listAuothMenu;
	}

	@Override
	public List<SysResource> getAllMenuResources() throws Exception {
		List<SysResource> resourcesMenuList = new ArrayList<SysResource>();
		List<SysResource> resourcesList = sysResourceMapper.getAllMenuResources();
		resourcesMenuList = this.treeMenuList(resourcesList, BigDecimal.valueOf(0));
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
	public SysResource getResourceById(SysResourceQuery sysResource) throws Exception {
		SysResource psysResource = sysResourceMapper.selectByPrimaryKey(sysResource.getResourceid());
		return psysResource;
	}

	@Override
	public List<SysResource> selectByResourceid(String resourceid) throws Exception {
		List<SysResource> list_sysResource = sysResourceMapper.selectByResourceid(resourceid);
		return list_sysResource;
	}

	@Override
	public List<SysResource> selectByPid(String pid) throws Exception {
		List<SysResource> list_sysResource = sysResourceMapper.selectByPid(pid);
		return list_sysResource;
	}
	 
}
