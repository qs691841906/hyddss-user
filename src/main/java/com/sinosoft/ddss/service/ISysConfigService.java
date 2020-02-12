
package com.sinosoft.ddss.service;

import java.util.List;
import java.util.Map;

import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.query.SysConfigQuery;

public interface ISysConfigService {
	List<SysConfig> listSysConfig() throws Exception;

	List<SysConfig> listSysConfigById(String ids) throws Exception;

	Map<String, Object> listInit(SysConfigQuery sysConfigQuery) throws Exception;

	SysConfig selectConfigById(SysConfig sysConfig) throws Exception;
	
	/**
	 * 根据配置的key值获取配置值
	 * @author 乔森
	 * @param sysConfig
	 * @return
	 * @throws Exception
	 */
	SysConfig selectConfigBykey(SysConfig sysConfig) throws Exception;
	
	/**
	 * 根据级别和用户查找卫星和传感器
	 * @param level
	 * @param string 
	 * @return
	 * @throws Exception
	 */
	List<Map<String,Object>> selectSallitedSensorByLevel(String level,String satelliteName, String series) throws Exception;
	List<Map<String,Object>> selectSallitedAndSensorByLevel(String level,String satelliteName) throws Exception;

	boolean updateConfigById(List<SysConfig> sysConfig) throws Exception;

	List<SysConfig> sysConfigOfSystem(SysConfigQuery sysConfigQuery) throws Exception;
	List<SysConfig> selectByKeyLevel(SysConfig sysConfig);

	boolean saveConfig(SysConfig sysConfig) throws Exception;

	boolean getCountByKey(String configKey) throws Exception;

	boolean deleteById(String id) throws Exception;

	boolean updateByKeySelective(SysConfig sysConfig)throws Exception;

	List<SysConfig> listSysConfigEmail(SysConfig sysConfig);
	
}
