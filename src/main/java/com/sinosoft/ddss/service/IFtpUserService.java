package com.sinosoft.ddss.service;

import java.util.Map;

import com.sinosoft.ddss.common.entity.FtpUser;
import com.sinosoft.ddss.common.entity.query.FtpQuery;

public interface IFtpUserService {

	Map<String, Object> listFtpInit(FtpQuery ftpQuery) throws Exception;

	boolean saveFtpUser(FtpUser ftpUser) throws Exception;

	FtpUser queryFtpUserById(String id) throws Exception;

	boolean updateFtpUser(FtpUser ftpUser) throws Exception;

	boolean deleteFtpUser(String id) throws Exception;

	boolean checkFtpName(String ftpName) throws Exception;


}
