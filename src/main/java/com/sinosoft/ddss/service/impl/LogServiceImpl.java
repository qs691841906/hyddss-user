package com.sinosoft.ddss.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.LoggingEvent;
import com.sinosoft.ddss.common.entity.SysLog;
import com.sinosoft.ddss.common.entity.query.SysLogQuery;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.dao.LoggingEventMapper;
import com.sinosoft.ddss.dao.SysLogMapper;
import com.sinosoft.ddss.service.ILogService;

@Service
@Transactional
public class LogServiceImpl implements ILogService {
	private static Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

	@Autowired
	private LoggingEventMapper loggingEventMapper;
	/**
	 * 初始化日志列表 
	 */
	@Override
	public Map<String, Object> listLogInit(SysLogQuery sysLogQuery) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// TODO Auto-generated method stub
		try {
			String startTime = sysLogQuery.getStartTime();
			if(StringUtils.isNotBlank(startTime)){
				sysLogQuery.setStartLong(DateTimeUtils.str2Long(startTime, DateTimeUtils.YMDHMS));
			}
			String endTime = sysLogQuery.getEndTime();
			if(StringUtils.isNotBlank(endTime)){
				sysLogQuery.setEndLong(DateTimeUtils.str2Long(endTime, DateTimeUtils.YMDHMS));
			}
			List<LoggingEvent> listLog = loggingEventMapper.listLog(sysLogQuery);
			Integer count = loggingEventMapper.countLog(sysLogQuery);
			TotalInfo totalInfo = new TotalInfo(count, sysLogQuery.getPageSize(), sysLogQuery.getPage(),
					sysLogQuery.getStartNum());
			map.put("data", listLog);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "log list query succeeded");
			LOGGER.info("log list query succeeded");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error("log list query exception");
			map.put("status", false);
			map.put("msg", "log list query exception");
		}
		return map;
	}
	
	/**
	 * 日志删除
	 */
	@Override
	public void deleteLogById(LoggingEvent sysLog) throws Exception {
		// TODO Auto-generated method stub
		loggingEventMapper.deleteByPrimaryKey(sysLog.getEventId());
	}

	@Override
	public List<LoggingEvent> listLogByIds(LoggingEvent sysLog) throws Exception {
		// TODO Auto-generated method stubrabbitTemplate.convertAndSend(RabbitConfig.queueName, JSON.toJSONString(sysLog));
		return loggingEventMapper.listLogByIds(sysLog);
			
	}
}
