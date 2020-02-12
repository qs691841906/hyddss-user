package com.sinosoft.ddss.service;

import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.LoggingEvent;
import com.sinosoft.ddss.common.entity.SysLog;
import com.sinosoft.ddss.common.entity.query.SysLogQuery;


public interface ILogService {

	Map<String, Object> listLogInit(SysLogQuery sysLogQuery) throws Exception;

	void deleteLogById(LoggingEvent sysLog) throws Exception;

	List<LoggingEvent> listLogByIds(LoggingEvent sysLog) throws Exception;

}
