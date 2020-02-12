package com.sinosoft.ddss.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.DecryptToken;
import com.sinosoft.ddss.utils.FastJsonUtil;

@Service
public class DecryptTokenImpl implements DecryptToken{

	@Autowired
	private JedisClient jedisClient;

	@Override
	public User decyptToken(String token) {
		if (StringUtils.isBlank(token)) {
			return null;
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return null;
		}
		User user = FastJsonUtil.toBean(is, User.class);
		return user;
	}

	@Override
	public String decyptTokenStr(String token) {
		if (StringUtils.isBlank(token)) {
			return Constant.TOKEN_NULL;
		}
		String is = jedisClient.get(token);
		if (StringUtils.isBlank(is)) {
			return Constant.TOKEN_INVALID;
		}
		return "SUCCESS";
	}

	@Override
	public User token2User(String is) {
		// TODO Auto-generated method stub
		User user = FastJsonUtil.toBean(is, User.class);
		return user;
	}
}
