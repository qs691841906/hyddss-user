package com.sinosoft.ddss.utils;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class FastJsonUtil {
	private static final SerializeConfig config;
	private static String dateFormat;
	static {  
        config = new SerializeConfig();  
        dateFormat = "yyyy-MM-dd HH:mm:ss";  
        config.put(java.util.Date.class, new SimpleDateFormatSerializer(dateFormat)); // 使用和json-lib兼容的日期输出格式  
        config.put(java.sql.Date.class, new SimpleDateFormatSerializer(dateFormat)); // 使用和json-lib兼容的日期输出格式  
    } 
	private static final SerializerFeature[] features = {
		SerializerFeature.PrettyFormat,
		SerializerFeature.WriteMapNullValue, 		// 输出空置字段  
        SerializerFeature.WriteNullListAsEmpty, 	// list字段如果为null，输出为[]，而不是null  
        SerializerFeature.WriteNullNumberAsZero, 	// 数值字段如果为null，输出为0，而不是null  
        SerializerFeature.WriteNullBooleanAsFalse,  // Boolean字段如果为null，输出为false，而不是null  
        SerializerFeature.WriteNullStringAsEmpty 	// 字符类型字段如果为null，输出为""，而不是null  
//        SerializerFeature.DisableCircularReferenceDetect //禁止循环引用
        
	};
	/***
	 * 对象转JSON 将空值赋值为“”
	 * @param object
	 * @return
	 */
	public static String toJSONString(Object object) {  
        return JSON.toJSONString(object, config, features);  
    }  
    /****
     * 对象转JSON
     * @param object
     * @return
     */
    public static String toJSONNoFeatures(Object object) {  
        return JSON.toJSONString(object, config);  
    }  
    /****
     *  JSON 转对象 
     * @param text
     * @return
     */
    public static Object toBean(String text) {  
        return JSON.parse(text);  
    }  
    /****
     * JSON 转实体Bean
     * @param text
     * @param clazz
     * @return
     */
    public static <T> T toBean(String text, Class<T> clazz) {  
        return JSON.parseObject(text, clazz);  
    }  
    /***
     * 转换为数组  
     * @param text
     * @return
     */
    public static <T> Object[] toArray(String text) {  
        return toArray(text, null);  
    }  
    /***
     * 转换为数组  
     * @param text
     * @param clazz
     * @return
     */
    public static <T> Object[] toArray(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz).toArray();  
    }  
    /***
     * 转换为List  
     * @param text
     * @param clazz
     * @return
     */
    public static <T> List<T> toList(String text, Class<T> clazz) {  
        return JSON.parseArray(text, clazz);  
    }   
    /**  
     * 将string转化为序列化的json字符串  
     * @param keyvalue  
     * @return  
     */  
    public static Object textToJson(String text) {  
        Object objectJson  = JSON.parse(text);  
        return objectJson;  
    }  
      
    /**  
     * json字符串转化为map  
     * @param s  
     * @return  
     */  
    public static Map<String,Object> stringToCollect(String s) {  
        Map<String, Object> m = JSONObject.parseObject(s);  
        return m;  
    }  
      
    /**  
     * 将map转化为string  
     * @param m  
     * @return  
     */  
    public static String collectToString(Map<Object,Object> m) {  
        String s = JSONObject.toJSONString(m);  
        return s;  
    }  
    /***
     * 判断一个字符串是否是合法的JSON字符串
     * @param test
     * @return
     */
    public final static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    public final static boolean isJSONObjectValid(Object o) {
        try {
            JSONObject.toJSON(o);
        } catch (JSONException ex) {
            try {
                JSONObject.toJSON(o);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
