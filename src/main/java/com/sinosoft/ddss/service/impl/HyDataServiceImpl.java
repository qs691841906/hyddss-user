package com.sinosoft.ddss.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.entity.HyData;
import com.sinosoft.ddss.dao.HyDataMapper;
import com.sinosoft.ddss.service.IHyDataService;

@Service
@Transactional
public class HyDataServiceImpl implements IHyDataService {
	@Autowired
	private HyDataMapper hyDataMapper;

	@Override
	public List<HyData> selectData(HyData hyData) {
		// TODO Auto-generated method stub
		List<HyData> selectData = hyDataMapper.selectData(hyData);
		return selectData;
	}

	@Override
	public List<Map<String,String>> selectDataList() {
		// TODO Auto-generated method stub
		List<Map<String,String>> selectDataList = hyDataMapper.selectDataList();
		return selectDataList;
	}

	@Override
	public List<HyData> selectLevelNode(HyData hyData) {
		// TODO Auto-generated method stub
		List<HyData> selectDataList = hyDataMapper.selectLevelNode(hyData);
		return selectDataList;
	}

	@Override
	public List<HyData> selectLevelList() {
		// TODO Auto-generated method stub
		List<HyData> selectDataList = hyDataMapper.selectLevelList();
		return selectDataList;
	}
	

}
