package com.sinosoft.ddss.dao;

import com.sinosoft.ddss.common.entity.OrderInfoOut;
import java.math.BigDecimal;

public interface OrderInfoOutMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(BigDecimal orderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    int insert(OrderInfoOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    int insertSelective(OrderInfoOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    OrderInfoOut selectByPrimaryKey(BigDecimal orderId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(OrderInfoOut record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_orderinfo_out
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(OrderInfoOut record);
}