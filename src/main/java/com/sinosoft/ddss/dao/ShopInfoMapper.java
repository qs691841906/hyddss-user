package com.sinosoft.ddss.dao;

import com.sinosoft.ddss.common.entity.ShopInfo;
import java.math.BigDecimal;

public interface ShopInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(BigDecimal id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    int insert(ShopInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    int insertSelective(ShopInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    ShopInfo selectByPrimaryKey(BigDecimal id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ShopInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shop_info
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ShopInfo record);
}