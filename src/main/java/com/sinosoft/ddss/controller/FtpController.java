
package com.sinosoft.ddss.controller;

import java.io.File;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.FtpUser;
import com.sinosoft.ddss.common.entity.query.FtpQuery;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.service.IFtpUserService;

@RestController
public class FtpController {
	private static Logger LOGGER = LoggerFactory.getLogger(FtpController.class);
	
	@Autowired
	private IFtpUserService ftpUserService;
	
	
		/** <pre>listFtp(初始化加载ftp用户列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午10:11:55    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午10:11:55    
		 * 修改备注： 
		 * @param ftpQuery
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysFtp/listFtp")
	public String listFtp(@RequestBody FtpQuery ftpQuery, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start query FtpUser list");
			map = ftpUserService.listFtpInit(ftpQuery);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Query FtpUser list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>saveFtpUser(新增ftp用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午10:21:51    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午10:21:51    
		 * 修改备注： 
		 * @param ftpUser
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysFtp/saveFtpUser")
	public String saveFtpUser(@RequestBody FtpUser ftpUser, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start save FtpUser");
			boolean boo = ftpUserService.saveFtpUser(ftpUser);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Save FtpUser was successed");
				LOGGER.info("Save FtpUser was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Save FtpUser was failed");
				LOGGER.info("Save FtpUser was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Save FtpUser has exception");
			LOGGER.error("Save FtpUser has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>queryFtpUserById(根据id查询ftp用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午10:28:32    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午10:28:32    
		 * 修改备注： 
		 * @param id
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysFtp/queryFtpUserById", method = RequestMethod.POST)
	public String queryFtpUserById(String id) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(id)){
				map.put("status", false);
				map.put("msg", "FtpUser id is null");
				return JsonUtils.objectToJson(map);
			}
			FtpUser ftpUser = ftpUserService.queryFtpUserById(id);
			map.put("data", ftpUser);
			map.put("status", true);
			map.put("msg", "Query FtpUser by id was successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", e.getMessage());
			LOGGER.error("Query FtpUser by id has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>updateFtpUser(修改ftp用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午10:46:08    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午10:46:08    
		 * 修改备注： 
		 * @param ftpUser
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysFtp/updateFtpUser")
	public String updateFtpUser(@RequestBody FtpUser ftpUser, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			LOGGER.info("Start update FtpUser");
			boolean boo = ftpUserService.updateFtpUser(ftpUser);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Update FtpUser was successed");
				LOGGER.info("Update FtpUser was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Update FtpUser was failed");
				LOGGER.info("Update FtpUser was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Update FtpUser has exception");
			LOGGER.error("Update FtpUser by id has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>deleteFtpUser(删除用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月8日 上午10:46:58    
		 * 修改人：宫森      
		 * 修改时间：2018年4月8日 上午10:46:58    
		 * 修改备注： 
		 * @param ftpUser
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysFtp/deleteFtpUser")
	public String deleteFtpUser(String id, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			LOGGER.info("Start delete FtpUser");
			if(StringUtils.isBlank(id)){
				map.put("status", false);
				map.put("msg", "FtpUser id is null");
				return JsonUtils.objectToJson(map);
			}
			boolean boo = ftpUserService.deleteFtpUser(id);
			if (boo) {
				map.put("status", true);
				map.put("msg", "Delete FtpUser was successed");
				LOGGER.info("Delete FtpUser was successed");
			} else {
				map.put("status", false);
				map.put("msg", "Delete FtpUser was failed");
				LOGGER.info("Delete FtpUser was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Delete FtpUser has exception");
			LOGGER.error("Delete FtpUser by id has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	@RequestMapping("/oauth/sysFtp/checkFtpName")
	public String checkFtpName(String ftpName, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean boo;
		try {
			if(StringUtils.isBlank(ftpName)){
				map.put("status", false);
				map.put("msg", "ftpName id is null");
				return JsonUtils.objectToJson(map);
			}
			boo = ftpUserService.checkFtpName(ftpName);
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
	
	/**
	 * 获取服务器文件路径
	 * @param filePath
	 * @return
	 */
	@RequestMapping("/oauth/sysFtp/getSystemFilePath")
	public String getSystemFilePath(String filePath){
		if(filePath == null || filePath.equals("")){
			filePath = "/FTP_CACHE";
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		File file  = new File(filePath);
		File[] files = file.listFiles();
		for(File file_:files){
			if(!file_.isFile()){
				String file_name = file_.getName();
				String file_path = file_.getPath();
				Map<String, String> map  = new HashMap<String, String>();
				map.put("fileName", file_name);
				map.put("filePath", file_path);
				list.add(map);
			}
		}
		
		return JsonUtils.objectToJson(list);
	}
}
