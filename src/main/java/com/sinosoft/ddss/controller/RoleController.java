package com.sinosoft.ddss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.Role;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.RoleQuery;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.common.util.ExportExcelForShopcar;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.common.util.PropertiesUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.DecryptToken;
import com.sinosoft.ddss.service.IRoleService;
import com.sinosoft.ddss.service.ISysResourceService;
import com.sinosoft.ddss.service.IUserRoleService;

@RestController
public class RoleController {
	private static Logger LOGGER = LoggerFactory.getLogger(RoleController.class);
	@Autowired
	private IRoleService roleService;
	@Autowired
	private IUserRoleService userRoleService;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private DecryptToken decryptToken;
	@Autowired
	private ISysResourceService isysResourceService;
	
		/** <pre>listRole(初始化加载角色列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午4:52:25    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午4:52:25    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/listRole")
	public String listRole(@RequestBody RoleQuery role) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = roleService.listRoleInit(role);
		} catch (Exception e) {
				e.printStackTrace();
			LOGGER.error("Init role list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>insertRole(增加角色)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午5:28:46    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午5:28:46    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/insertRole")
	public String insertRole(@RequestBody Role role) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(!StringUtils.isBlank(role.getRoleName())){
//				查询角色名称是否存在，若存在则返回
				boolean boo = roleService.getRoleByName(role);
				if(boo){
					map.put("status", boo);
					map.put("msg", "This role already exist");
				} else {
					String token = role.getToken();
					if(StringUtils.isBlank(token)){
						map.put("status", false);
						map.put("msg", Constant.TOKEN_NULL);
						return JsonUtils.objectToJson(map);
					}
					String is = jedisClient.get(token);
					if(StringUtils.isBlank(is)){
						map.put("status", false);
						map.put("msg", Constant.TOKEN_INVALID);
						return JsonUtils.objectToJson(map);
					}
//					将token转为用户
					User u = decryptToken.token2User(is);
					role.setFounder(u.getUserName());
					boolean bool = roleService.insertRole(role);
					map.put("status", bool);
					if(bool){
//						List<Role> role_list = roleService.listRole(role);
//						for(Role r_role:role_list){
//							if(r_role.getRoleName().equals(role.getRoleName())){
//								map.put("role_id", role.getRoleId());
//							}
//						}
						map.put("msg", "Add role successed");
					} else {
						map.put("msg", "Add role failed");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Add role has exception");
			LOGGER.error("Add role has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>getRoleByName(根据角色名判断角色是否存在)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午5:29:20    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午5:29:20    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/getRoleByName")
	public String getRoleByName(@RequestBody Role role) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			boolean boo = roleService.getRoleByName(role);
			map.put("status", boo);
			if(boo){
				map.put("msg", "This role already exist");
			} else {
				map.put("msg", "This role can be use");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Get role by name has exception");
			LOGGER.error("Get role by name has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>getRoleById(根据id获取角色信息)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午5:30:36    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午5:30:36    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/getRoleById")
	public String getRoleById(@RequestBody Role role, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Role ro = roleService.getRoleById(role);
			map.put("status", true);
			map.put("data", ro);
			map.put("msg", "Get role by id successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Get role by id has exception");
			LOGGER.error("Get role by id has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>showRoleAuth(查询所有权限)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月21日 下午3:36:23    
		 * 修改人：宫森      
		 * 修改时间：2018年3月21日 下午3:36:23    
		 * 修改备注： 方法改完后把@RequestBody加上   ln
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/showRoleAuth")
	public String showRoleAuth(@RequestBody Role role, HttpServletRequest request) {
//		public String showRoleAuth( Role role, HttpServletRequest request) { 
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<SysResource> resourcesList = roleService.getAllResourcesByRole(role.getRoleId());
			for(int i=0;i<resourcesList.size();i++){
				SysResource sysResource = resourcesList.get(i);
				if(sysResource.getUrl().equals("series")||sysResource.getUrl().equals("satellite")||sysResource.getUrl().equals("sensor")||sysResource.getUrl().equals("level")){
//					如果是系列，卫星，传感器，级别那就是数据权限（1），默认是功能权限（2）
					sysResource.setRoleType(1);
				}
//				如果这个节点是一级标签就让它不选中
				if(sysResource.getPid()==BigDecimal.valueOf(0)){
//					如果是一级标签但下面没有子节点就让选中
					String Resourceid = sysResource.getResourceid().toString();
					List<SysResource> sysResource2 = isysResourceService.selectByPid(Resourceid);
					if(sysResource2.size()>0){
						sysResource.setHaveAuth(false);
					}
				}else{
//					如果这个节点下有子节点就让它不选中
					String Resourceid = sysResource.getResourceid().toString();
					List<SysResource> sysResource2 = isysResourceService.selectByPid(Resourceid);
					if(sysResource2.size()>0){
						sysResource.setHaveAuth(false);
					}
				}
				
				resourcesList.set(i, sysResource);
			}
			map.put("data", resourcesList);
			map.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Get role auth has exception");
			LOGGER.error("Get role auth has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>updateRoleById(修改角色以及角色权限)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午5:30:03    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午5:30:03    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/updateRoleById")
	public String updateRoleById(@RequestBody Role role, HttpServletRequest request) {
//		public String updateRoleById( Role role, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BigDecimal roleId = role.getRoleId();
			if(null == roleId){
				map.put("status", false);
				map.put("msg", "Role id is null");
				JsonUtils.objectToJson(map);
			}
			String sourceIds = role.getSourceIds();
			if(StringUtils.isBlank(sourceIds)){
				map.put("status", false);
				map.put("msg", "SourceIds id is null");
				JsonUtils.objectToJson(map);
			}
//			循环查询所有sourceIds的pid直到pid都为0（一级节点）
			List<SysResource> listSysResource = isysResourceService.selectByResourceid(sourceIds);
			List<String> pids = new ArrayList<String>();
			for(SysResource sysResource:listSysResource){
				String pid = sysResource.getPid().toString();
				if(!"".equals(pid)&&null!=pid&&!"0".equals(pid)){
					pids.add(pid);
				}
			}
			if(pids.size()>0){
				String pids_2 = "";
				for(String pid:pids){
					pids_2+=pid+",";
				}
				pids_2 = pids_2.substring(0, pids_2.length()-1);
				List<SysResource> listSysResource_2 = isysResourceService.selectByResourceid(pids_2);
				for(SysResource sysResource:listSysResource_2){
					String pid = sysResource.getPid().toString();
					if(!"".equals(pid)&&null!=pid&&!"0".equals(pid)){
						pids.add(pid);
					}
				}
			}
//			将所有的sourceIds都放到list中用于去重
			String[] sourceIds_list = sourceIds.split(",");
			for(String sourceids:sourceIds_list){
				pids.add(sourceids);
			}
//			去掉重复的数据
			Stream<String> pids_stream = pids.stream().distinct();
			 List<String> result = pids_stream.collect(Collectors.toList());
			 
//			给sourceIds重新赋值
			sourceIds = "";
			for(String sourceids:result){
				sourceIds+=sourceids+",";
			}
			sourceIds = sourceIds.substring(0, sourceIds.length()-1);
			role.setSourceIds(sourceIds);
			
			LOGGER.info("OrderMaxSize"+role.getOrderMaxSize());
			LOGGER.info("DayMaxSize"+role.getDayMaxSize());
			boolean boo = roleService.updateRoleById(role);
			map.put("status", boo);
			if(boo){
				map.put("msg", "Update role successed");
				LOGGER.info("Update role successed");
			} else {
				map.put("msg", "Update role failed");
				LOGGER.info("Update role failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Update role exception");
			LOGGER.error("Update role has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>deleteById(根据id删除角色)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月20日 下午5:33:12    
		 * 修改人：宫森      
		 * 修改时间：2018年3月20日 下午5:33:12    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/deleteRoleById", method = RequestMethod.POST)
	public String deleteById(@RequestBody Role role) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BigDecimal roleId = role.getRoleId();
			if(null==roleId){
				map.put("status", false);
				map.put("msg", "Role id is null");
				return JsonUtils.objectToJson(map);
			}
			map = roleService.deleteById(role);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Delete role has exception");
			LOGGER.error("Delete role has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>listRoleAll(用户界面查询角色列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月21日 上午9:18:17    
		 * 修改人：宫森      
		 * 修改时间：2018年3月21日 上午9:18:17    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/listRoleAll")
	public String listRoleAll(@RequestBody Role role, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Role> listRole = roleService.listRole(role);
			map.put("data", listRole);
			map.put("status", true);
			map.put("msg", "Role list query successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Role list query has exception");
			LOGGER.error("Role list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>exportExl(导出角色列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午11:09:12    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午11:09:12    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @param response
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/role/exportExl")
	public String exportExl(@RequestBody Role role, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		OutputStream out = null;
		try {
			List<Role> listRole = roleService.listRoleByIds(role);
			if(listRole==null||listRole.size()<=0){
				map.put("status", true);
				map.put("msg", "No data");
				return JsonUtils.objectToJson(map);
			}
			List<Map<String,String>> roleList = new ArrayList<Map<String,String>>();
			for (Role r : listRole) {
				//setting field
				Map<String,String> userMap = new HashMap<String, String>();
				userMap.put("role_id", r.getRoleId().toString());
				userMap.put("roleName", r.getRoleName());
				userMap.put("description", r.getDescription());
				userMap.put("status", r.getStatus()==0?"锁定":"正常");
				userMap.put("create_time", r.getCreateTime());
				roleList.add(userMap);
			}
			String[] head = {"角色ID","角色名","角色描述","状态","创建时间"};
			String[] body = {"role_id","roleName","description","status","create_time"};
			//打开输出流
			out = response.getOutputStream();
			//设置类型
			response.setContentType("octets/stream");
			//设置头部信息
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ DateTimeUtils.getNowStrTimeStemp() + ".xls");
			//获取当前国际化
			String language = request.getParameter("language");
			String local = PropertiesUtils.getLocale(language);
			//封装数据导出
			ExportExcelForShopcar.exporsOrderXls("用户信息", roleList, out,
					DateTimeUtils.YMDHMS,head,body,local);
			out.flush();
			out.close();
			map.put("status", true);
			map.put("msg", "Export successed");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			map.put("status", false);
			map.put("msg", "Export has exception");
			LOGGER.error("Export has exception, caused by "+e.getMessage(),Constant.SYSTEM);
			return JsonUtils.objectToJson(map);
		}finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Export has exception, caused by "+e.getMessage(),Constant.SYSTEM);
			}
		}
	}
}
