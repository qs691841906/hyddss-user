package com.sinosoft.ddss.dao;

import java.util.List;

import com.sinosoft.ddss.common.entity.LoggingEvent;
import com.sinosoft.ddss.common.entity.query.SysLogQuery;

public interface LoggingEventMapper {
    int deleteByPrimaryKey(Long eventId);

	List<LoggingEvent> listLogByIds(LoggingEvent sysLog);

	List<LoggingEvent> listLog(SysLogQuery sysLogQuery);

	Integer countLog(SysLogQuery sysLogQuery);
}