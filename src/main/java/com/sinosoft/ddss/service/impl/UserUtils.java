package com.sinosoft.ddss.service.impl;


import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.util.HttpUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.IUserUtil;
import com.sinosoft.ddss.utils.FastJsonUtil;

@Component
public class UserUtils implements IUserUtil{
	private static final String TOKEN = "token";
	@Autowired
	private JedisClient jedisClient;
	/**
     * 获取当前请求的token
     * @return
     */
    public String getCurrentToken() {
    	HttpServletRequest httpServletRequest = HttpUtils.getHttpServletRequest();
    	System.err.println("TOKEN>>>"+httpServletRequest.getParameter(TOKEN));
        return HttpUtils.getHeaders(httpServletRequest).get(TOKEN);
    }

    /**
     * 获取当前请求的用户Id
     * @return
     */
    public BigDecimal getCurrentPrinciple() {
    	String token = HttpUtils.getHeaders(HttpUtils.getHttpServletRequest()).get(TOKEN);
    	if (StringUtils.isBlank(token)) {
			return null;
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return null;
		}
		User user = FastJsonUtil.toBean(is, User.class);
		if(null==user){
			return null;
		}
		return user.getUserId();
    }
    /**
     * 获取当前请求的用户名
     * @return
     */
    public String getCurrentName() {
    	HttpServletRequest httpServletRequest = HttpUtils.getHttpServletRequest();
    	String token = HttpUtils.getHeaders(HttpUtils.getHttpServletRequest()).get(TOKEN);
    	token = httpServletRequest.getParameter(TOKEN);
    	if (StringUtils.isBlank(token)) {
			return "";
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return "";
		}
		User user = FastJsonUtil.toBean(is, User.class);
		if(null==user){
			return "";
		}
		return user.getUserName();
    }

    /**
     * 获取当前请求Authentication
     *
     * @return
     */
    public User getCurrentAuthentication() {
    	String token = HttpUtils.getHeaders(HttpUtils.getHttpServletRequest()).get(TOKEN);
    	if (StringUtils.isBlank(token)) {
			return null;
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return null;
		}
		User user = FastJsonUtil.toBean(is, User.class);
		if(null==user){
			return null;
		}
		return user;
    }

    /**
     * 获取当前请求的权限信息
     * @return
     */
    public List<SysResource> getCurrentAuthorities() {
    	String token = HttpUtils.getHeaders(HttpUtils.getHttpServletRequest()).get(TOKEN);
    	if (StringUtils.isBlank(token)) {
			return null;
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return null;
		}
		User user = FastJsonUtil.toBean(is, User.class);
		if(null==user){
			return null;
		}
		return user.getSysSource();
    }
}
