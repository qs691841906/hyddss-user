package com.sinosoft.ddss.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyThreadPool {
	private ExecutorService exe;
	private static final int POOL_SIZE = 4;
	String formateMessage;
	String loggerName;
	String threadName;
	String code;
	String arg0;
	String arg1;
	String arg2;
	String arg3;
	String callerMethod;
	String callerLine;
	public MyThreadPool(String formateMessage){
		this.formateMessage = formateMessage;
		exe = Executors.newFixedThreadPool(POOL_SIZE);
		exe.execute(new MyThread(formateMessage));
	}
}
