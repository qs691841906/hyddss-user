
package com.sinosoft.ddss.controller;

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

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.DedicatedLine;
import com.sinosoft.ddss.common.entity.DedicatedUser;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.DedicatedQuery;
import com.sinosoft.ddss.common.entity.query.UserQuery;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.IDedicatedService;
import com.sinosoft.ddss.service.IUserService;
import com.sinosoft.ddss.utils.FastJsonUtil;

@RestController
public class DedicatedController {
	private static Logger LOGGER = LoggerFactory.getLogger(DedicatedController.class);

	@Autowired
	private IDedicatedService dedicatedService;

	@Autowired
	private IUserService userService;
	
	@Autowired
	private JedisClient jedisClient;
	/**
	 * <pre>
	 * listDedicated(专线列表查询)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 下午3:26:06    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 下午3:26:06    
	 * 修改备注： 
	 * &#64;param dedicatedQuery
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value = "/oauth/dedicated/listDedicated")
	public String listDedicated(@RequestBody DedicatedQuery dedicatedQuery, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start query DedicatedUser list",Constant.SYSTEM);
			map = dedicatedService.listdedicatedInit(dedicatedQuery);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Query DedicatedUser list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * saveDedicatedUser(专线用户保存)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 下午3:34:57    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 下午3:34:57    
	 * 修改备注： 
	 * &#64;param dedicatedLine
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value = "/oauth/dedicated/saveDedicatedUser")
	public String saveDedicatedLine(@RequestBody DedicatedLine dedicatedLine, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start save DedicatedLine User");
			boolean boo = dedicatedService.saveDedicatedUser(dedicatedLine);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Save DedicatedLine User was successed");
				LOGGER.info("Save DedicatedLine User was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Save DedicatedLine User was failed");
				LOGGER.info("Save DedicatedLine User was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Save DedicatedLine User has exception");
			LOGGER.error("Save DedicatedUser list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * queryDedicatedLineById(根据id查看专线用户)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 下午3:42:25    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 下午3:42:25    
	 * 修改备注： 
	 * &#64;param id
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value = "/oauth/dedicated/queryDedicatedLineById")
	public String queryDedicatedLineById(String id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(id)) {
				map.put("status", false);
				map.put("msg", "DedicatedLine id is null");
				return JsonUtils.objectToJson(map);
			}
			DedicatedLine dedicatedLine = dedicatedService.queryDedicatedLineById(id);
			map.put("data", dedicatedLine);
			map.put("status", true);
			map.put("msg", "Query DedicatedLine was successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query DedicatedLine has exception");
			LOGGER.error("Query DedicatedLine has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * updateDedicatedLine(修改专线用户)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 下午3:48:53    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 下午3:48:53    
	 * 修改备注： 
	 * &#64;param dedicatedLine
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value = "/oauth/dedicated/updateDedicatedLine")
	public String updateDedicatedLine(@RequestBody DedicatedLine dedicatedLine, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start update FtpUser");
			boolean boo = dedicatedService.updateDedicatedLine(dedicatedLine);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Update DedicatedLine was successed");
				LOGGER.info("Update DedicatedLine was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Update DedicatedLine was failed");
				LOGGER.info("Update DedicatedLine was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Update DedicatedLine has exception");
			LOGGER.error("Update DedicatedLine has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * deleteDedicatedLine(删除专线)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 下午3:50:35    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 下午3:50:35    
	 * 修改备注： 
	 * &#64;param id
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value = "/oauth/dedicated/deleteDedicatedLine")
	public String deleteDedicatedLine(String id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start delete DedicatedLine");
			if (StringUtils.isBlank(id)) {
				map.put("status", false);
				map.put("msg", "DedicatedLine id is null");
				return JsonUtils.objectToJson(map);
			}
			boolean boo = dedicatedService.deleteDedicatedLine(id);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Delete DedicatedLine was successed");
				LOGGER.info("Delete DedicatedLine was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Delete DedicatedLine was failed");
				LOGGER.info("Delete DedicatedLine was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Delete DedicatedLine has exception");
			LOGGER.error("Delete DedicatedLine has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 专线权限显示
	 * @param userQuery
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/dedicated/showUserAuth")
	public String showUserAuth(@RequestBody UserQuery userQuery, String id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(id)){
				map.put("status", false);
				map.put("msg", "Id is null");
				return JsonUtils.objectToJson(map);
			}
			List<DedicatedUser> showUserAuth = dedicatedService.showUserAuth(id);
			List<User> listUser = userService.listUser(userQuery);
			if(showUserAuth.size()>0){
				for (User user : listUser) {
					for (DedicatedUser du : showUserAuth) {
						if(user.getUserId().equals(du.getUserId())){
							user.setHaveAuth(true);
							break;
						}
					}
				}
			}
			Integer countUser = userService.countUser(userQuery);
			TotalInfo totalInfo = new TotalInfo(countUser, userQuery.getPageSize(), userQuery.getPage(),
					userQuery.getStartNum());
			map.put("data", listUser);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "Query user list successed");
			LOGGER.info("Query user list successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query user list has exception");
			LOGGER.error("Query user list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>checkDedicatedName(校验名字唯一)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月11日 下午5:02:46    
		 * 修改人：宫森      
		 * 修改时间：2018年4月11日 下午5:02:46    
		 * 修改备注： 
		 * @param ftpName
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping("/oauth/dedicated/checkDedicatedName")
	public String checkDedicatedName(String ftpName, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean boo;
		try {
			if(StringUtils.isBlank(ftpName)){
				map.put("status", false);
				map.put("msg", "ftpName id is null");
				return JsonUtils.objectToJson(map);
			}
			boo = dedicatedService.checkDedicatedName(ftpName);
			if (boo) {
				map.put("status", false);
				map.put("msg", "This FtpName already used");
				LOGGER.info("This FtpName already used");
			} else {
				map.put("status", true);
				map.put("msg", "This FtpName can use");
				LOGGER.info("This FtpName can use");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "CheckFtpName has exception");
			LOGGER.error("CheckFtpName has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>updateDedicatedUser(专线分配用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月11日 下午5:24:29    
		 * 修改人：宫森      
		 * 修改时间：2018年4月11日 下午5:24:29    
		 * 修改备注： 
		 * @param id
		 * @param ids
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping("/oauth/dedicated/updateDedicatedUser")
	public String updateDedicatedUser(String id,String ids, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(id)){
				map.put("status", false);
				map.put("msg", "Dedicated line id is null");
				return JsonUtils.objectToJson(map);
			}
			if(StringUtils.isBlank(ids)){
				map.put("status", false);
				map.put("msg", "User ids is null");
				return JsonUtils.objectToJson(map);
			}
			dedicatedService.updateDedicatedUser(id, ids);
			map.put("status", true);
			map.put("msg", "Update Dedicated User was succesed");
			LOGGER.info("Update Dedicated User was succesed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Update Dedicated has exception");
			LOGGER.error("Update Dedicated has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	@RequestMapping("/oauth/dedicated/getDedicatedAuthBuId")
	public String getDedicatedAuthBuId(String token, HttpServletRequest request) {
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
			DedicatedUser dedicatedUser = dedicatedService.getDedicatedAuthBuId(bean.getUserId());
			if(null!=dedicatedUser){
				map.put("dedicated", true);
			} else {
				map.put("dedicated", false);
			}
			map.put("status", true);
			map.put("msg", "Query Dedicated User was succesed");
			LOGGER.info("Query Dedicated User was succesed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query Dedicated has exception");
			LOGGER.error("Query Dedicated has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
}
