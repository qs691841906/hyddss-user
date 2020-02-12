
package com.sinosoft.ddss.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.SysResourceQuery;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.IRoleResourceService;
import com.sinosoft.ddss.service.ISysResourceService;
import com.sinosoft.ddss.utils.FastJsonUtil;

@RestController
public class ResourceController {
	private static Logger LOGGER = LoggerFactory.getLogger(ResourceController.class);
	@Autowired
	private ISysResourceService sysResourceService;
	@Autowired
	private IRoleResourceService roleResourceService;
	@Autowired
	private JedisClient jedisClient;
	
		/** <pre>listSysResource(初始化加载权限列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月21日 上午11:25:43    
		 * 修改人：宫森      
		 * 修改时间：2018年3月21日 上午11:25:43    
		 * 修改备注： 
		 * @param sysResource
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/resource/listSysResource")
	public String listSysResource(@RequestBody SysResourceQuery sysResource, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = sysResourceService.listResourceInit(sysResource);
			map.put("status", true);
			map.put("msg", "Query sysResource list successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query sysResource list has exception");
			LOGGER.error("Query sysResource list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	@RequestMapping(value = "/oauth/resource/getResourcePidsById")
	public String getResourcePidsById(@RequestBody String id){
		SysResourceQuery sysResource = new SysResourceQuery();
		sysResource.setResourceid(BigDecimal.valueOf(Long.valueOf(id)));
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			SysResource psysResource =	sysResourceService.getResourceById(sysResource);
			BigDecimal pid = psysResource.getPid();
			map.put("pid", pid);
			if(pid==BigDecimal.valueOf(0)){
				map.put("p_pid", pid);
			}else{
				SysResourceQuery sysResource2 = new SysResourceQuery();
				sysResource2.setResourceid(pid);
				SysResource psysResource2 =	sysResourceService.getResourceById(sysResource2);
				map.put("p_pid", psysResource2.getPid());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query sysResource list has exception");
			LOGGER.error("Query sysResource list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		
		return JsonUtils.objectToJson(map);
		
	}
	
	
	/**
	 * 获取用户对应权限列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/resource/listOauthMenu")
	public String listAuothMenu(String token) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", "Token is null");
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", "Token already invalid, Please login");
				return JsonUtils.objectToJson(map);
			}
			User bean = FastJsonUtil.toBean(is, User.class);
			List<SysResource> sysSource = bean.getSysSource();
			
			map.put("status", true);
			map.put("data", sysSource);
			map.put("msg", "Query successed");
			map.put("userName", bean.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query exception");
			LOGGER.error("Query exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 获取二级权限列表 （已经没用了）
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/resource/listSonMenu")
	public String listSonMenu(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = request.getParameter(Constant.TOKEN);
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", "Token is null");
				return JsonUtils.objectToJson(map);
			}
//			String type = request.getParameter("type");
//			if(StringUtils.isBlank(type)){
//				map.put("status", false);
//				map.put("msg", "Type is null");
//				return JsonUtils.objectToJson(map);
//			}
//			String pid = request.getParameter("pid");
//			if(StringUtils.isBlank(pid)){
//				map.put("status", false);
//				map.put("msg", "Pid is null");
//				return JsonUtils.objectToJson(map);
//			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", "Token already invalid, Please login");
				return JsonUtils.objectToJson(map);
			}
			User bean = FastJsonUtil.toBean(is, User.class);
			List<SysResource> sysSource = bean.getSysSource();
//			List<SysResource> returnList = new ArrayList<>();
//			for (SysResource sysResource : sysSource) {
//				if(type.equals(sysResource.getType().toString()) && pid.equals(sysResource.getPid().toString())){
//					returnList.add(sysResource);
//				}
//			}
//			List<SysResource> listAuothMenu = sysResourceService.listAuothMenu(bean.getUserId().toString(),type);
			map.put("status", true);
			map.put("data", sysSource);
			map.put("msg", "Query successed");
			map.put("userName", bean.getUserName());
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query exception");
			LOGGER.error("Query exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/oauth/resource/getAllMenuResources")
	public String getAllMenuResources() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<SysResource> allMenuResources = sysResourceService.getAllMenuResources();
			map.put("data", allMenuResources);
			map.put("status", true);
			map.put("msg", "Query sysResource list successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query sysResource list has exception");
			LOGGER.error("Query sysResource list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
}
