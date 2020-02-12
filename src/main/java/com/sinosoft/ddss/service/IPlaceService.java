package com.sinosoft.ddss.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.sinosoft.ddss.common.entity.Place;
import com.sinosoft.ddss.common.entity.User;


public interface IPlaceService {

	List<Place> listPlace(Place place) throws Exception;

	List<Place> listPlaceById(Place place) throws Exception;

	List<Place> getPlaceById(Place place)throws Exception;

}
