package com.sinosoft.ddss.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * @author li_jiazhi
 * @create 2018年3月16日上午9:08:03
 * 
 */
public class XmlCreateUtils {
	/**
	 *
	 * @return
	 * @author li_jiazhi
	 * @date  2018年3月13日
	 * @time  下午6:15:49
	 * 观测需求参数
	 */

	public  static String getTestXML(String flag){
    	StringBuffer xml=new StringBuffer();
    	if("1".equals(flag)){//注册
    		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    		xml.append("<Root>\n");
    		xml.append("<FileHead>\n");
    		xml.append("<messageType>SSP_DDSS_USERREGIST</messageType>\n");
    		xml.append("<messageID>666666</messageID>\n");
    		xml.append("<originatorAddress>SSP</originatorAddress>\n");
    		xml.append("<recipientAddress>DDSS</recipientAddress>\n");
    		xml.append("<creationTime>"+getDate(new Date())+"</creationTime>\n");
    		xml.append("</FileHead>\n");
    		xml.append("<FileBody>\n");
    		xml.append("<userName>sspuser</userName>\n");
    		xml.append("<userPwd>123456</userPwd>\n");
    		xml.append("<userTel>15611696203</userTel>\n");
    		xml.append("<userEmail>gongsen95@163.com</userEmail>\n");
    		xml.append("<workArea>9</workArea>\n");
    		xml.append("<workType>1</workType>\n");
    		xml.append("<userUnit>Sinosoft</userUnit>\n");
    		xml.append("<country>1</country>\n");
    		xml.append("<realName>中科软</realName>\n");
    		xml.append("</FileBody>\n");
    		xml.append("</Root>\n");
    	} else if("2".equals(flag)){//登录
    		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    		xml.append("<Root>\n");
    		xml.append("<FileHead>\n");
    		xml.append("<messageType>SSP_DDSS_USERLOGIN</messageType>\n");
    		xml.append("<messageID>666666</messageID>\n");
    		xml.append("<originatorAddress>SSP</originatorAddress>\n");
    		xml.append("<recipientAddress>DDSS</recipientAddress>\n");
    		xml.append("<creationTime>"+getDate(new Date())+"</creationTime>\n");
    		xml.append("</FileHead>\n");
    		xml.append("<FileBody>\n");
    		xml.append("<userName>sspuser</userName>\n");
    		xml.append("<userPwd>123456</userPwd>\n");
    		xml.append("</FileBody>\n");
    		xml.append("</Root>\n");
    	} else if("3".equals(flag)){//登录
    		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    		xml.append("<Root>\n");
    		xml.append("<FileHead>\n");
    		xml.append("<messageType>SSP_DDSS_USERLOGIN</messageType>\n");
    		xml.append("<messageID>666666</messageID>\n");
    		xml.append("<originatorAddress>SSP</originatorAddress>\n");
    		xml.append("<recipientAddress>DDSS</recipientAddress>\n");
    		xml.append("<creationTime>"+getDate(new Date())+"</creationTime>\n");
    		xml.append("</FileHead>\n");
    		xml.append("<FileBody>\n");
    		xml.append("<userName>sspuser</userName>\n");
    		xml.append("<userPwd>123456</userPwd>\n");
    		xml.append("</FileBody>\n");
    		xml.append("</Root>\n");
    	}
		return xml.toString();
    }
	
	/**
	 * @param date
	 * @return
	 * @author li_jiazhi
	 * @date  2018年3月13日
	 * @time  下午6:14:47
	 * 【Date转换String,转换格式:yyyy-MM-dd HH:mm:ss】
	 */
	public static String getDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dates = "";
		if (null != date && !"".equals(date)) {
			dates = df.format(date);
		} else {
			dates = df.format(new Date());
		}
		return dates;
	}
}
