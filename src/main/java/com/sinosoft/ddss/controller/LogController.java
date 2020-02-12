
package com.sinosoft.ddss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.LoggingEvent;
import com.sinosoft.ddss.common.entity.SysLog;
import com.sinosoft.ddss.common.entity.query.SysLogQuery;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.common.util.ExportExcelForShopcar;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.common.util.PropertiesUtils;
import com.sinosoft.ddss.service.ILogService;

@RestController
public class LogController {
	private static Logger LOGGER = LoggerFactory.getLogger(LogController.class);
	@Autowired
	private ILogService logService;
	
	
		/** <pre>listRole(日志列表查询)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月4日 上午9:44:17    
		 * 修改人：宫森      
		 * 修改时间：2018年4月4日 上午9:44:17    
		 * 修改备注： 
		 * @param sysLog
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysLog/listLog")
	public String listLog(@RequestBody SysLogQuery sysLog, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = logService.listLogInit(sysLog);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Query log list has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
		/** <pre>deleteLogByIds(日志删除)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月4日 上午9:52:50    
		 * 修改人：宫森      
		 * 修改时间：2018年4月4日 上午9:52:50    
		 * 修改备注： 
		 * @param sysLog
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysLog/deleteLogById")
	public String deleteLogById(@RequestBody LoggingEvent sysLog, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(null==sysLog.getEventId()){
				map.put("status", false);
				map.put("msg", "Log id is null");
				return JsonUtils.objectToJson(map);
			}
			logService.deleteLogById(sysLog);
			map.put("status", true);
			map.put("msg", "delete log by id was successed");
			LOGGER.info("delete log by id was successed");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "delete log by id has exception");
			LOGGER.error("delete log by id has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
		/** <pre>exportExl(日志导出)   
		 * 创建人：宫森      
		 * 创建时间：2018年4月4日 上午9:53:59    
		 * 修改人：宫森      
		 * 修改时间：2018年4月4日 上午9:53:59    
		 * 修改备注： 
		 * @param role
		 * @param request
		 * @param response
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/sysLog/exportSysLogExl")
	public String exportLogExl(String ids, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		OutputStream out = null;
		try {
			if(StringUtils.isBlank(ids)){
				map.put("status", false);
				map.put("msg", "Log ids is null");
				return JsonUtils.objectToJson(map);
			}
			LoggingEvent sysLog = new LoggingEvent();
			sysLog.setIds(ids);
			List<LoggingEvent> listlog = logService.listLogByIds(sysLog);
			if(listlog==null||listlog.size()<=0){
				map.put("status", true);
				map.put("msg", "No data");
				return JsonUtils.objectToJson(map);
			}
			List<Map<String,String>> logList = new ArrayList<Map<String,String>>();
			for (LoggingEvent log: listlog) {
				//setting field
				Map<String,String> dataMap = new HashMap<String, String>();
				dataMap.put("id", log.getEventId().toString());
				dataMap.put("log_type", log.getArg0());
				dataMap.put("description", log.getFormattedMessage());
				//优先级从高到低分别是 ERROR、WARN、INFO、DEBUG。
				dataMap.put("log_level", log.getLevelString());
				dataMap.put("founder", log.getArg1());
				dataMap.put("create_time", log.getCreateTime());
				logList.add(dataMap);
			}
			String[] head = {"ID","日志类型","日志描述","级别","创建人","创建时间"};
			String[] body = {"id","log_type","description","log_level","founder","create_time"};
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
			ExportExcelForShopcar.exporsOrderXls("日志信息", logList, out,
					DateTimeUtils.YMDHMS,head,body,local);
			out.flush();
			out.close();
			map.put("status", true);
			map.put("msg", "导出角色成功");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			map.put("status", false);
			map.put("msg", "导出角色异常");
			LOGGER.error("Export has exception, caused by "+e.getMessage(),Constant.SYSTEM);
			System.out.println(e);
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
