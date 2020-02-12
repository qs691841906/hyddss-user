package com.sinosoft.ddss.service;

import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.HyData;

public interface IHyDataService {
	/**
	 * 
	 * 根据pid联动 卫星传感器级别
	 * @param pid
	 * @return
	 */
	List<HyData> selectData(HyData hyData);
	
	/**
	 * 
	 * 获取所有卫星传感器级别
	 * @param pid
	 * @return
	 */
	List<Map<String,String>> selectDataList();
	/**
	 * 
	 * 获取所有卫星传感器级别
	 * @param pid
	 * @return
	 */
	List<HyData> selectLevelList();
	/**
	 * 
	 * 根据级别得到卫星传感器
	 * @param pid
	 * @return
	 */
	List<HyData> selectLevelNode(HyData hyData);
}
