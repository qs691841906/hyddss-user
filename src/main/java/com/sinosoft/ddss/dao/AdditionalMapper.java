package com.sinosoft.ddss.dao;

import com.sinosoft.ddss.common.entity.Additional;
import java.math.BigDecimal;

public interface AdditionalMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(BigDecimal additionalId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    int insert(Additional record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    int insertSelective(Additional record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    Additional selectByPrimaryKey(BigDecimal additionalId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Additional record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_additional
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Additional record);
}