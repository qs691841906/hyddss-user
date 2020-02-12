package com.sinosoft.ddss.dataDao;

import java.util.List;

import com.sinosoft.ddss.common.entity.Place;

public interface PlaceMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    int insert(Place record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    int insertSelective(Place record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    Place selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Place record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_shp_place
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Place record);

	List<Place> listPlace(Place place);

	List<Place> listPlaceById(Place place);

	List<Place> getPlaceById(Place place);
}