package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.DedicatedLine;
import com.sinosoft.ddss.common.entity.DedicatedUser;
import com.sinosoft.ddss.common.entity.FtpUser;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.DedicatedQuery;
import com.sinosoft.ddss.common.entity.query.UserQuery;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.dao.DedicatedLineMapper;
import com.sinosoft.ddss.dao.DedicatedUserMapper;
import com.sinosoft.ddss.dao.UserMapper;
import com.sinosoft.ddss.service.IDedicatedService;

@Service
@Transactional
public class DedicatedServiceImpl implements IDedicatedService {
	private static Logger LOGGER = LoggerFactory.getLogger(DedicatedServiceImpl.class);

	@Autowired
	private DedicatedLineMapper dedicatedLineMapper;

	@Autowired
	private DedicatedUserMapper dedicatedUserMapper;

	@Autowired
	private UserMapper userMapper;

	@Override
	public Map<String, Object> listdedicatedInit(DedicatedQuery dedicatedQuery) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		// TODO Auto-generated method stub
		try {
			List<DedicatedLine> listDedicatedLine = dedicatedLineMapper.listDedicatedLine(dedicatedQuery);
			Integer countDedicatedLine = dedicatedLineMapper.countDedicatedLine(dedicatedQuery);
			TotalInfo totalInfo = new TotalInfo(countDedicatedLine, dedicatedQuery.getPageSize(),
					dedicatedQuery.getPage(), dedicatedQuery.getStartNum());
			map.put("data", listDedicatedLine);
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
	public boolean saveDedicatedUser(DedicatedLine dedicatedLine) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		dedicatedLine.setFtpPwd(ActivityStringUtils.createMd5Str(dedicatedLine.getFtpPwd(), null));
		int i = dedicatedLineMapper.insertSelective(dedicatedLine);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;

	}

	@Override
	public DedicatedLine queryDedicatedLineById(String strId) throws Exception {
		// TODO Auto-generated method stub
		BigDecimal id = BigDecimal.valueOf(Long.valueOf(strId));
		return dedicatedLineMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean updateDedicatedLine(DedicatedLine dedicatedLine) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		int i = dedicatedLineMapper.updateByPrimaryKeySelective(dedicatedLine);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public boolean deleteDedicatedLine(String strId) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		BigDecimal id = BigDecimal.valueOf(Long.valueOf(strId));
		int i = dedicatedLineMapper.deleteByPrimaryKey(id);
		if (i > 0) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public List<DedicatedUser> showUserAuth(String id) throws Exception {
		// TODO Auto-generated method stub
		// 根据id查出来 该专线对应的用户
		List<DedicatedUser> listDedicated = dedicatedUserMapper.selectByDedicatedId(id);
		// 查出所有的用户 然后比较 进行赋值回显
		return listDedicated;

	}

	@Override
	public boolean checkDedicatedName(String ftpName) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		DedicatedLine ftpUser = dedicatedLineMapper.checkFtpName(ftpName);
		if (null != ftpUser) {
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public void updateDedicatedUser(String id, String ids) throws Exception {
		// TODO Auto-generated method stub
		//先删除
		int i = dedicatedUserMapper.deleteByDedicatedId(id);
		List<DedicatedUser> list = new ArrayList<DedicatedUser>();
		// 一个用户的时候
		if(ids.indexOf(",")==(-1)){
			DedicatedUser du = new DedicatedUser();
			du.setSpecialId(BigDecimal.valueOf(Long.valueOf(id)));
			du.setUserId(BigDecimal.valueOf(Long.valueOf(id)));
			list.add(du);
		} else {//多个用户的时候
			String[] split = ids.split(",");
			for (String uid : split) {
				DedicatedUser du = new DedicatedUser();
				du.setSpecialId(BigDecimal.valueOf(Long.valueOf(id)));
				du.setUserId(BigDecimal.valueOf(Long.valueOf(uid)));
				list.add(du);
			}
		}
		//添加
		int i2 = dedicatedUserMapper.insertByList(list);
	}

	@Override
	public DedicatedUser getDedicatedAuthBuId(BigDecimal userId) throws Exception {
		// TODO Auto-generated method stub
		return dedicatedUserMapper.getDedicatedAuthBuId(userId);
	}
}
