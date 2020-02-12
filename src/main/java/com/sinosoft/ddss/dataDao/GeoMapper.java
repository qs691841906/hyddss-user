package com.sinosoft.ddss.dataDao;

import java.util.List;

import com.sinosoft.ddss.model.Place;

/**
 * 
 * @author sanyo
 * @since 2018年3月17日 下午9:44:05
 * @version 1.0.0
 * @TODO
 */
public interface GeoMapper {

	//根据code 获取省市县
	List<Place> getPlaceByCode(String type);


}
