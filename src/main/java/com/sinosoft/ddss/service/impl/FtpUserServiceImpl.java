package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.FtpUser;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.query.FtpQuery;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.dao.FtpUserMapper;
import com.sinosoft.ddss.dao.SysConfigMapper;
import com.sinosoft.ddss.service.IFtpUserService;

@Service
@Transactional
public class FtpUserServiceImpl implements IFtpUserService {
	private static Logger LOGGER = LoggerFactory.getLogger(FtpUserServiceImpl.class);

	@Autowired
	private FtpUserMapper ftpUserMapper;
	@Autowired
	private SysConfigMapper sysConfigMapper;

	@Override
	public Map<String, Object> listFtpInit(FtpQuery ftpQuery) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// TODO Auto-generated method stub
		try {
			Set<Integer> idSet = new TreeSet<Integer>();
			List<FtpUser> listFtpUser = ftpUserMapper.listFtpUser(ftpQuery);
			for (FtpUser fu : listFtpUser) {
				idSet.add(fu.getCompanyCategory());
			}
			String ids = "";
			if(idSet.size()>1){
				for (Integer i : idSet) {
					ids += ","+i;
				}
				ids = ids.substring(1, ids.length());
			} else {
				for (Integer i : idSet) {
					ids = i.toString();
				}
			}
			SysConfig config = new SysConfig();
			config.setIds(ids);
			List<SysConfig> listConfig = sysConfigMapper.listSysConfigById(config);
			for (FtpUser fu : listFtpUser) {
				for (SysConfig sysConfig : listConfig) {
					// 单位类别
					if (fu.getCompanyCategory().toString().equals(sysConfig.getId().toString())) {
						fu.setCategoryStr(sysConfig.getConfigValue());
					}
				}
			}
			Integer countFtpUser = ftpUserMapper.countFtpUser(ftpQuery);
			TotalInfo totalInfo = new TotalInfo(countFtpUser, ftpQuery.getPageSize(), ftpQuery.getPage(),
					ftpQuery.getStartNum());
			map.put("data", listFtpUser);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "FtpUser list query succeeded");
			LOGGER.info("FtpUser list query succeeded");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error("FtpUser list query exception");
			map.put("status", false);
			map.put("msg", "FtpUser list query exception");
		}
		return map;

	}

	@Override
	public boolean saveFtpUser(FtpUser ftpUser) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		ftpUser.setFtpPwd(ActivityStringUtils.createMd5Str(ftpUser.getFtpPwd(), null));
		int i = ftpUserMapper.insertSelective(ftpUser);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;

	}

	@Override
	public FtpUser queryFtpUserById(String strId) throws Exception {
		// TODO Auto-generated method stub
		BigDecimal id = BigDecimal.valueOf(Long.valueOf(strId));
		FtpUser fu = ftpUserMapper.selectByPrimaryKey(id);
		String s = "";
		s += ","+fu.getCompanyCategory();
		String ids = s.substring(1, s.length());
		SysConfig config = new SysConfig();
		config.setIds(ids);
		List<SysConfig> listConfig = sysConfigMapper.listSysConfigById(config);
		for (SysConfig sysConfig : listConfig) {
			// 单位类别
			if (fu.getCompanyCategory().toString().equals(sysConfig.getId().toString())) {
				fu.setCategoryStr(sysConfig.getConfigValue());
			}
		}
		return fu;

	}

	@Override
	public boolean updateFtpUser(FtpUser ftpUser) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		int i = ftpUserMapper.updateByPrimaryKeySelective(ftpUser);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;

	}

	@Override
	public boolean deleteFtpUser(String strId) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		BigDecimal id = BigDecimal.valueOf(Long.valueOf(strId));
		int i = ftpUserMapper.deleteByPrimaryKey(id);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public boolean checkFtpName(String ftpName) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		FtpUser ftpUser = ftpUserMapper.checkFtpName(ftpName);
		if(null!=ftpUser){
			boo = true;
		} else {
			boo = false;
		}
		return boo;

	}

}
