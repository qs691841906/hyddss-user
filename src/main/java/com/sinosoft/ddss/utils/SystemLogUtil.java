package com.sinosoft.ddss.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinosoft.ddss.common.entity.SysLog;

/***
 * 通过调用saveErrorLog保存系统日志信息,只需要传三个参数就行.
 * 
 * @author leo
 *
 */
public class SystemLogUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemLogUtil.class);

	public static void savelog(String type, String message, String level, String username,
			Long orderid, Date date) {
		SysLog syslog = new SysLog();

	}
}
