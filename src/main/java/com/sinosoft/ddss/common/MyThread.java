package com.sinosoft.ddss.common;

import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.sinosoft.ddss.common.entity.Log;
import com.sinosoft.ddss.common.util.JsonUtils;

public class MyThread implements Runnable{

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
	
	public MyThread(String formateMessage) {
		this.formateMessage = formateMessage;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(formateMessage);
		Log log = new Log();
		log.setFormateMessage(formateMessage);
		try {
			getJsonString("http://192.168.200.244:8199/saveInfoLog",JsonUtils.objectToJson(log));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * httpclient调用接口
	 * @param url 调用路径
	 * @param jsonData 传入 json串
	 * @return
	 */
	public static String getJsonString(String url, String jsonData) throws Exception{
		final String CONTENT = "text/json";
		String str = null;
		HttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
		StringEntity stringEntity = new StringEntity(jsonData,"UTF-8");
		stringEntity.setContentType(CONTENT);
		httpPost.setEntity(stringEntity);
		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(httpPost);
		HttpEntity entity = response.getEntity();
		str = EntityUtils.toString(entity,"UTF-8");
		return str;
	}
}
