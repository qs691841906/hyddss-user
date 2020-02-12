package com.sinosoft.ddss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.dataDao.GeoMapper;
import com.sinosoft.ddss.model.Place;
import com.sinosoft.ddss.service.GeoService;

/**
 * 
 * @author sanyo
 * @since 2018年3月17日 下午9:44:10
 * @version 1.0.0
 * @TODO
 */
@Service
public class GeoServiceImpl implements GeoService {

	@Autowired
	private GeoMapper geoMapper;

	@Override
	public List<Place> getPlaceByCode(String type) {
		// TODO Auto-generated method stub
		List<Place> listPlace = geoMapper.getPlaceByCode(type);
		return listPlace;
	}

}
