package com.sinosoft.ddss.service;

import java.util.List;

import com.sinosoft.ddss.model.Place;

/**
 * 
 * @author sanyo
 * @since 2018年3月17日 下午9:44:14
 * @version 1.0.0
 * @TODO
 */
public interface GeoService {

	public List<Place> getPlaceByCode(String type);

}
