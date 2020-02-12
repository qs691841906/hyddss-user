package com.sinosoft.ddss.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.ddss.common.base.entity.RestfulJSON;
import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.query.SysConfigQuery;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.service.ISysConfigService;

@RestController
public class SysConfigController {
	private static Logger LOGGER = LoggerFactory.getLogger(SysConfigController.class);
	@Autowired
	private ISysConfigService sysConfigService;

	/**
	 * <pre>
	 * listSysConfig(初始化加载配置页面)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月4日 下午5:15:38    
	 * 修改人：宫森      
	 * 修改时间：2018年4月4日 下午5:15:38    
	 * 修改备注： 
	 * &#64;param sysConfigQuery
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping("/oauth/sysconfig/listSysConfig")
	public String listSysConfig(@RequestBody SysConfigQuery sysConfigQuery, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = sysConfigService.listInit(sysConfigQuery);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "SysConfig list query exception");
			LOGGER.error("SysConfig list query exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * querySysConfigById(根据id查看配置)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月4日 下午5:55:31    
	 * 修改人：宫森      
	 * 修改时间：2018年4月4日 下午5:55:31    
	 * 修改备注： 
	 * &#64;param sysConfig
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping("/oauth/sysconfig/querySysConfigById")
	public RestfulJSON querySysConfigById(@RequestBody SysConfig sysConfig) {
		RestfulJSON json = new RestfulJSON();
		try {
			if (null == sysConfig.getId()) {
				// map.put("status", false);
				// map.put("msg", "Config id is null");
				json.setMsg("Config id is null");
				json.setStatus(false);
			}
			SysConfig config = sysConfigService.selectConfigById(sysConfig);
			// map.put("status", true);
			// map.put("data", config);
			// map.put("msg", "Query sysconfig by id was successed");
			json.setMsg("Query sysconfig by id was successed");
			json.setStatus(true);
			json.setData(config);
			LOGGER.info("Query sysconfig by id was successed");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Query sysconfig by id has exception, caused by " + e.getMessage(), Constant.SYSTEM);
			// map.put("status", false);
			// map.put("msg", "Query sysconfig by id has exception");
			json.setMsg("Query sysconfig by id has exception");
			json.setStatus(false);
		}
		return json;
	}

	/**
	 * <pre>
	 * updateConfigById(根据id修改配置)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月4日 下午5:56:08    
	 * 修改人：宫森      
	 * 修改时间：2018年4月4日 下午5:56:08    
	 * 修改备注： 
	 * &#64;param sysConfig
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping("/oauth/sysconfig/updateConfigById")
	public String updateConfigById(@RequestBody SysConfig sysConfig, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			boolean config = false;
			// 配置管理
			if (null != sysConfig.getId() && !StringUtils.isBlank(sysConfig.getId().toString())) {
				List<SysConfig> list = new ArrayList<SysConfig>();
				list.add(sysConfig);
				config = sysConfigService.updateConfigById(list);
			// 系统设置	
			}else if(!StringUtils.isBlank(sysConfig.getIds())){
				String ids = sysConfig.getIds();
				String[] sId = ids.split(",");
				String values = sysConfig.getConfigValue();
				String[] sValue = values.split(",");
				// 如果id跟value不对应直接返回false
				if(sId.length == sValue.length){
					List<SysConfig> list = new ArrayList<SysConfig>();
					for (int i = 0; i < sId.length; i++) {
						SysConfig sysConfig2 = new SysConfig();
						BigDecimal bigDecimal = new BigDecimal(Long.parseLong(sId[i]));
						sysConfig2.setId(bigDecimal);
						sysConfig2.setConfigValue(sValue[i]);
						list.add(sysConfig2);
					}
					config = sysConfigService.updateConfigById(list);
				}else{
					map.put("status", false);
					map.put("msg", "Sysyem settings paramters is wrong");
					return JsonUtils.objectToJson(map);
				}
			// 既不是配置管理，也不是系统设置，返回false
			}else{
				map.put("status", false);
				map.put("msg", "Config id is null");
				return JsonUtils.objectToJson(map);
			}
			// 修改失败，返回false
			if (config) {
				map.put("status", true);
				map.put("data", config);
				map.put("msg", "update sysconfig by id was successed");
			} else {
				map.put("status", false);
				map.put("data", config);
				map.put("msg", "update sysconfig by id was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Update sysconfig by id has exception, caused by " + e.getMessage(), Constant.SYSTEM);
			map.put("status", false);
			map.put("msg", "update sysconfig by id has exception");
		}
		return JsonUtils.objectToJson(map);
	}

	@RequestMapping("/oauth/sysconfig/saveConfig")
	public String saveConfig(@RequestBody SysConfig sysConfig, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			boolean config = sysConfigService.saveConfig(sysConfig);
			if (config) {
				map.put("status", true);
				map.put("data", config);
				map.put("msg", "Save sysconfig was successed");
			} else {
				map.put("status", false);
				map.put("data", config);
				map.put("msg", "Save sysconfig was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Save sysconfig by id has exception");
			LOGGER.error("Save sysconfig by id has exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * sysConfigOfSystem(系统管理页面查询系统配置)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月8日 上午9:16:06    
	 * 修改人：宫森      
	 * 修改时间：2018年4月8日 上午9:16:06    
	 * 修改备注： 
	 * &#64;param sysConfigQuery
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping("/oauth/sysconfig/sysConfigOfSystem")
	public String sysConfigOfSystem(@RequestBody SysConfigQuery sysConfigQuery, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<SysConfig> listConfig = sysConfigService.sysConfigOfSystem(sysConfigQuery);
			map.put("data", listConfig);
			map.put("msg", "query config list was successed");
			map.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "log list query exception");
			LOGGER.error("SysConfig list query exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * 配置删除
	 * 
	 * @param id
	 * @param configKey
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/oauth/sysconfig/deleteSysConfig")
	public String deleteSysConfig(String id, String configKey, HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (StringUtils.isBlank(id)) {
				map.put("msg", "Config id is null");
				map.put("status", false);
				return JsonUtils.objectToJson(map);
			}
			if (StringUtils.isBlank(configKey)) {
				map.put("msg", "Config value is null");
				map.put("status", false);
				return JsonUtils.objectToJson(map);
			}
			boolean boo1 = sysConfigService.getCountByKey(configKey);
			if (boo1) {
				map.put("msg", "This Config is last one, can't delete");
				map.put("status", false);
				return JsonUtils.objectToJson(map);
			} else {
				boolean boo2 = sysConfigService.deleteById(id);
				if (boo2) {
					map.put("msg", "Config delete successed");
					map.put("status", true);
				} else {
					map.put("msg", "Config delete failed");
					map.put("status", false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Delete SysConfig has exception");
			LOGGER.error("Delete SysConfig has exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * updateConfigByKey(根据配置的key修改配置文件)   
	 * Author：sen_kung     
	 * Create date：2018年5月3日 下午7:00:49    
	 * Author：sen_kung      
	 * Update date：2018年5月3日 下午7:00:49    
	 * Description： 
	 * &#64;param sysConfig
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */
	@RequestMapping("/oauth/sysconfig/updateConfigByKey")
	public RestfulJSON updateConfigByKey(@RequestBody SysConfig sysConfig, HttpServletRequest request,
			HttpServletResponse response) {
		RestfulJSON json = new RestfulJSON();
		try {
			if (StringUtils.isBlank(sysConfig.getConfigKey())) {
				json.setStatus(false);
				json.setMsg("Config key is null");
				return json;
			}
			boolean config = sysConfigService.updateByKeySelective(sysConfig);
			if (config) {
				json.setStatus(true);
				json.setMsg("Update sysconfig by key was successed");
			} else {
				json.setStatus(false);
				json.setMsg("Update sysconfig by key was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setStatus(false);
			json.setMsg("Update sysconfig by key has exception");
			LOGGER.error("Update sysconfig by key has exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return json;
	}

	/**
	 * <pre>
	 * listSysConfigEmail(查询导入模板列表)   
	 * Author：sen_kung     
	 * Create date：2018年5月15日 下午8:26:35    
	 * Author：sen_kung      
	 * Update date：2018年5月15日 下午8:26:35    
	 * Description： 
	 * &#64;param sysConfig
	 * &#64;param request
	 * &#64;param response
	 * &#64;return
	 * </pre>
	 */
	@RequestMapping("/oauth/sysconfig/listSysConfigEmail")
	public RestfulJSON listSysConfigEmail(@RequestBody SysConfig sysConfig, HttpServletRequest request,
			HttpServletResponse response) {
		RestfulJSON json = new RestfulJSON();
		try {
			List<SysConfig> list = sysConfigService.listSysConfigEmail(sysConfig);
			json.setStatus(true);
			json.setData(list);
		} catch (Exception e) {
			e.printStackTrace();
			json.setStatus(false);
			json.setMsg("SysConfig email list query exception");
			LOGGER.error("SysConfig email list query exception, caused by " + e.getMessage(), Constant.SYSTEM);
		}
		return json;
	}
}
