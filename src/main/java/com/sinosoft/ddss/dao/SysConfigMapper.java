package com.sinosoft.ddss.dao;

import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.query.SysConfigQuery;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;

public interface SysConfigMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(BigDecimal id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    int insert(SysConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    int insertSelective(SysConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    SysConfig selectByPrimaryKey(BigDecimal id);
    
    
    /**
     * 根据配置项的key获取配置值
     * @param configKey
     * @return
     */
    SysConfig selectByKey(String configKey);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(SysConfig record);
    int updateList(List<SysConfig> list);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_config
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SysConfig record);

	List<SysConfig> listSysConfig();

	List<SysConfig> listSysConfigById(SysConfig record);

	List<SysConfig> listConfig(SysConfigQuery sysConfigQuery);

	Integer countConfig(SysConfigQuery sysConfigQuery);

	List<SysConfig> sysConfigOfSystem(SysConfigQuery sysConfigQuery);
	
	List<SysConfig> selectByKeyLevel(SysConfig sysConfig);

	Integer getCountByKey(String configKey);

	int updateByKeySelective(SysConfig record);

	List<SysConfig> listSysConfigEmail(SysConfig sysConfig);
}