package com.sinosoft.ddss.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.common.entity.Place;
import com.sinosoft.ddss.dataDao.PlaceMapper;
import com.sinosoft.ddss.service.IPlaceService;


@Service
public class PlaceServiceImpl implements IPlaceService {
    @Autowired
    private PlaceMapper placeMapper;

	@Override
	public List<Place> listPlace(Place place) throws Exception {
		List<Place> listPlace = placeMapper.listPlace(place);
		return listPlace;
	}

	@Override
	public List<Place> listPlaceById(Place place) throws Exception {
		List<Place> listPlaceById = placeMapper.listPlaceById(place);
		return listPlaceById;
	}

	@Override
	public List<Place> getPlaceById(Place place) throws Exception {
		
		return placeMapper.getPlaceById(place);
			
	}

}
