package com.sinosoft.ddss.dao;

import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.HyData;

public interface HyDataMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(HyData record);

	int insertSelective(HyData record);

	HyData selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(HyData record);

	int updateByPrimaryKey(HyData record);

	/**
	 * 联动卫星传感器级别
	 * @param pid
	 * @return
	 */
	List<HyData> selectData(HyData hyData);
	List<Map<String, String>> selectDataList();
	List<HyData> selectLevelList();
	List<HyData> selectLevelNode(HyData hyData);
}