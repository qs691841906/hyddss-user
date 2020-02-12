package com.sinosoft.ddss.service;

import com.sinosoft.ddss.common.entity.User;

public interface DecryptToken {

	public User decyptToken(String token);
	public String decyptTokenStr(String token);
	public User token2User(String is);
}
