
package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.query.SysConfigQuery;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.dao.SysConfigMapper;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.ISysConfigService;

@Service
@Transactional
public class SysConfigServiceImpl implements ISysConfigService {
	private static Logger LOGGER = LoggerFactory.getLogger(SysConfigServiceImpl.class);
	@Autowired
	private SysConfigMapper sysConfigMapper;
	@Autowired
	private JedisClient jedisClient;

	@Override
	public List<SysConfig> listSysConfig() {

		return sysConfigMapper.listSysConfig();

	}

	@Override
	public List<SysConfig> listSysConfigById(String ids) {
		SysConfig config = new SysConfig();
		config.setIds(ids);
		return sysConfigMapper.listSysConfigById(config);
	}

	/*     
	*     
	*/

	@Override
	public Map<String, Object> listInit(SysConfigQuery sysConfigQuery) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<SysConfig> listConfig = sysConfigMapper.listConfig(sysConfigQuery);
			Integer count = sysConfigMapper.countConfig(sysConfigQuery);
			TotalInfo totalInfo = new TotalInfo(count, sysConfigQuery.getPageSize(), sysConfigQuery.getPage(),
					sysConfigQuery.getStartNum());
			map.put("data", listConfig);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "SysConfig list query succeeded");
			LOGGER.info("SysConfig list query succeeded");
		} catch (Exception e) {
			// TODO Auto-generated method stub
			e.printStackTrace();
			LOGGER.info("SysConfig list query exception");
			map.put("status", false);
			map.put("SysConfig", "log list query exception");
		}
		return map;
	}

	@Override
	public SysConfig selectConfigById(SysConfig sysConfig) throws Exception {
		// TODO Auto-generated method stub
		return sysConfigMapper.selectByPrimaryKey(sysConfig.getId());
	}

	@Override
	public boolean updateConfigById(List<SysConfig> record) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		// 修改完成 之后修改redis的内容
		int result = sysConfigMapper.updateList(record);
		if (result > 0) {
			boo = true;
			List<SysConfig> listSysConfig = sysConfigMapper.listSysConfig();
			String[] keyArr = new String[listSysConfig.size()];
			for(int i=0;i<keyArr.length;i++){
				keyArr[i] = listSysConfig.get(i).getConfigKey();
			}
			TreeMap<String, Integer> map = new TreeMap<String,Integer>();
			if(null!=listSysConfig&&listSysConfig.size()>0){
				for (int i = 0; i < keyArr.length; i++) {
					if(!map.isEmpty() && map.containsKey(keyArr[i])){
						map.put(keyArr[i], map.get(keyArr[i])+1);
					} else {
						map.put(keyArr[i], 1);
					}
				}
			}
			Set<String> list = new TreeSet<String>();
			for (String key : map.keySet()) {
				if(map.get(key)==1){
					list.add(key);
				}
			}
			Map<Object, Object> idMap = new HashMap<>();
			for (int i = 0; i < listSysConfig.size(); i++) {
				SysConfig sysConfig = listSysConfig.get(i);
				String configKey = sysConfig.getConfigKey();
				if (i == 0) {
					idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
				} else {
					if (configKey.equals(listSysConfig.get(i - 1).getConfigKey())) {
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}else{
						idMap.clear();
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}
				}
				jedisClient.set(configKey, JsonUtils.mapToJson(idMap));
			}
			if(list.size()>0){
				for (int i = 0; i < listSysConfig.size(); i++) {
					for (String s : list) {
						if(s.equals(listSysConfig.get(i).getConfigKey())){
							jedisClient.set(listSysConfig.get(i).getConfigKey(), listSysConfig.get(i).getConfigValue());
						} 
					}
				}
			}
		} else {
			boo = false;
		}
		return boo;

	}

	@Override
	public List<SysConfig> sysConfigOfSystem(SysConfigQuery sysConfigQuery) throws Exception {
		// TODO Auto-generated method stub
		/*sysConfigQuery.setStartNum(null);
		sysConfigQuery.setPageSize(null);
		sysConfigQuery.setType(0);*/
		List<SysConfig> listConfig = sysConfigMapper.sysConfigOfSystem(sysConfigQuery);
		return listConfig;
	}
	@Override
	public List<SysConfig> selectByKeyLevel(SysConfig sysConfig) {
		return sysConfigMapper.selectByKeyLevel(sysConfig);
	}

	@Override
	public boolean saveConfig(SysConfig sysConf) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		int i1 = sysConfigMapper.insertSelective(sysConf);
		if(i1>0){
			boo = true;
			List<SysConfig> listSysConfig = sysConfigMapper.listSysConfig();
			String[] keyArr = new String[listSysConfig.size()];
			for(int i=0;i<keyArr.length;i++){
				keyArr[i] = listSysConfig.get(i).getConfigKey();
			}
			TreeMap<String, Integer> map = new TreeMap<String,Integer>();
			if(null!=listSysConfig&&listSysConfig.size()>0){
				for (int i = 0; i < keyArr.length; i++) {
					if(!map.isEmpty() && map.containsKey(keyArr[i])){
						map.put(keyArr[i], map.get(keyArr[i])+1);
					} else {
						map.put(keyArr[i], 1);
					}
				}
			}
			Set<String> list = new TreeSet<String>();
			for (String key : map.keySet()) {
				if(map.get(key)==1){
					list.add(key);
				}
			}
			Map<Object, Object> idMap = new HashMap<>();
			for (int i = 0; i < listSysConfig.size(); i++) {
				SysConfig sysConfig = listSysConfig.get(i);
				String configKey = sysConfig.getConfigKey();
				if (i == 0) {
					idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
				} else {
					if (configKey.equals(listSysConfig.get(i - 1).getConfigKey())) {
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}else{
						idMap.clear();
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}
				}
				jedisClient.set(configKey, JsonUtils.mapToJson(idMap));
			}
			if(list.size()>0){
				for (int i = 0; i < listSysConfig.size(); i++) {
					for (String s : list) {
						if(s.equals(listSysConfig.get(i).getConfigKey())){
							jedisClient.set(listSysConfig.get(i).getConfigKey(), listSysConfig.get(i).getConfigValue());
						} 
					}
				}
			}
		} else {
			boo = false;
		}
		return boo;
			
	}

	@Override
	public boolean getCountByKey(String configKey) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		Integer countBykey = sysConfigMapper.getCountByKey(configKey);
		if(countBykey>1){
			boo = false;
		} else {
			boo = true;
		}
		return boo;
	}

	@Override
	public boolean deleteById(String id) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		int i = sysConfigMapper.deleteByPrimaryKey(BigDecimal.valueOf(Long.valueOf(id)));
		if(i>0){
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public boolean updateByKeySelective(SysConfig record) throws Exception {
		// TODO Auto-generated method stub
		boolean boo = false;
		// 修改完成 之后修改redis的内容
		int result = sysConfigMapper.updateByKeySelective(record);
		if (result > 0) {
			boo = true;
			List<SysConfig> listSysConfig = sysConfigMapper.listSysConfig();
			String[] keyArr = new String[listSysConfig.size()];
			for(int i=0;i<keyArr.length;i++){
				keyArr[i] = listSysConfig.get(i).getConfigKey();
			}
			TreeMap<String, Integer> map = new TreeMap<String,Integer>();
			if(null!=listSysConfig&&listSysConfig.size()>0){
				for (int i = 0; i < keyArr.length; i++) {
					if(!map.isEmpty() && map.containsKey(keyArr[i])){
						map.put(keyArr[i], map.get(keyArr[i])+1);
					} else {
						map.put(keyArr[i], 1);
					}
				}
			}
			Set<String> list = new TreeSet<String>();
			for (String key : map.keySet()) {
				if(map.get(key)==1){
					list.add(key);
				}
			}
			Map<Object, Object> idMap = new HashMap<>();
			for (int i = 0; i < listSysConfig.size(); i++) {
				SysConfig sysConfig = listSysConfig.get(i);
				String configKey = sysConfig.getConfigKey();
				if (i == 0) {
					idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
				} else {
					if (configKey.equals(listSysConfig.get(i - 1).getConfigKey())) {
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}else{
						idMap.clear();
						idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
					}
				}
				jedisClient.set(configKey, JsonUtils.mapToJson(idMap));
			}
			if(list.size()>0){
				for (int i = 0; i < listSysConfig.size(); i++) {
					for (String s : list) {
						if(s.equals(listSysConfig.get(i).getConfigKey())){
							jedisClient.set(listSysConfig.get(i).getConfigKey(), listSysConfig.get(i).getConfigValue());
						} 
					}
				}
			}
		} else {
			boo = false;
		}
		return boo;
	}

	@Override
	public List<SysConfig> listSysConfigEmail(SysConfig sysConfig) {
		// TODO Auto-generated method stub
		return sysConfigMapper.listSysConfigEmail(sysConfig);
	}

	@Override
	public SysConfig selectConfigBykey(SysConfig sysConfig) throws Exception {
		
		return sysConfigMapper.selectByKey(sysConfig.getConfigKey());
	}

	
	@Override
	public List<Map<String,Object>> selectSallitedSensorByLevel(String level,String satelliteName,String series) throws Exception {
		SysConfig config = new SysConfig();
		config.setConfigKey(level);
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		//根据级别得到级别对应的卫星value
		String satellites = jedisClient.get(level);
//		config = selectConfigBykey(config);
		
		List<String> satellitelist =new ArrayList<String>();
		List<Map<String,String>> mlist = new ArrayList<Map<String,String>>();
		String[] configValue1 = satelliteName.split(",");
		List<String> list2 = Arrays.asList(configValue1);
		List<String> arrList2 = new ArrayList<String>(list2);
		if(!StringUtils.isBlank(satellites)){
			String[] configValue = satellites.split(",");
			List<String> list1 = Arrays.asList(configValue);
			List<String> arrList1 = new ArrayList<String>(list1);
			// 卫星系列
			if(StringUtils.isNotBlank(series)){
				String sericesSatellites = jedisClient.get(series);
				if(StringUtils.isNotBlank(sericesSatellites)){
					String[] sericesSate = sericesSatellites.split(",");
					List<String> sericesSateL = Arrays.asList(sericesSate);
					List<String> arrList3 = new ArrayList<String>(sericesSateL);
					// 系列卫星与级别卫星取交集
					arrList1.retainAll(arrList3);
				}
			}
			//判断用户是否有对应的卫星   
			//取得两个List的交集，retainAll()方法
			arrList1.retainAll(arrList2);
			satellitelist.addAll(arrList1);
			 
			for(String satellite1:satellitelist){
					//拼成级别卫星 key = L0A_HY-2B
				String key = level+"_"+satellite1;
				SysConfig sysConfig = new SysConfig();
				sysConfig.setConfigKey(key);
				String sensors = jedisClient.get(key);
//				sysConfig = selectConfigBykey(sysConfig);
				String sensor="";
				if(!StringUtils.isBlank(sensors)){
					// 卫星对应的传感器    value = RA,SCA,RAD
					 sensor = sensors;
				}
				Map<String,String> map = new HashMap<String,String>();
				map.put("satellite", satellite1);
				map.put("sensor", sensor);
					mlist.add(map);
			}
		}
		List<String> satelist = new ArrayList<String>();
		for(Map<String,String> map:mlist){
			String sat = map.get("satellite");
			satelist.add(sat);
		}
		List<String> satelist2 = removeDuplicate(satelist);
		
		for(String satellite:satelist2){
			Map<String,Object> map = new HashMap<String,Object>();
			List<String> slist = new ArrayList<String>();
			map.put("satellite", satellite);
			for(Map<String,String> map2:mlist){
				String satellite1 = map2.get("satellite");
				String sensor1 = map2.get("sensor");
				if(satellite.equals(satellite1)){
					slist.add(sensor1);
				}
			}
			map.put("sensor", slist);
			rlist.add(map);
		}
		
		return rlist;
		
	}
	@Override
	public List<Map<String,Object>> selectSallitedAndSensorByLevel(String level,String satelliteName) throws Exception {
		
//		根据用户查询该角色的卫星
//		查询系统中配置的卫星
//		筛选系统和角色共有的卫星
//		根据卫星去查询配置项里的卫星和传感器
		
		//根据配置名获取key   config-->Value
		SysConfig config = new SysConfig();
		config.setConfigKey("satelliteCondition");
		config = selectConfigBykey(config);
		String[] configValue = config.getConfigValue().split(",");
		List<String> satellitelist =new ArrayList<String>();
		for (int i = 0; i < configValue.length; i++) {
			satellitelist.add(configValue[i]);
		}
		
		
		List<Map<String,String>> mlist = new ArrayList<Map<String,String>>();
		
		for(String satellite1:satellitelist){
			String key = satellite1+"Condition";
			SysConfig sysConfig = new SysConfig();
			sysConfig.setConfigKey(key);
			sysConfig = selectConfigBykey(sysConfig);
			if(null!=sysConfig){
				String value = sysConfig.getConfigValue();
				//[RA:L0A,L0B,L1,L2,4C, SCA:L1A,L1B,L2A,L2B]
				String[] values = value.split("#");
				
				for(String val :values){
					String[] str = val.split(":");
					String sensor = str[0];
					String levels = str[1];
					String[] levelss = levels.split(",");
					for(String level1:levelss){
//						根据级别筛选卫星和传感器
						if(level1.equals(level)){
							Map<String,String> map = new HashMap<String,String>();
							map.put("satellite", satellite1);
							map.put("sensor", sensor);
							map.put("level", level1);
							mlist.add(map);
						}
					}
					
				}
			}
		}
//		组合数据
		List<String> satelist = new ArrayList<String>();
		for(Map<String,String> map:mlist){
			String sat = map.get("satellite");
			satelist.add(sat);
		}
		List<String> satelist2 = removeDuplicate(satelist);
		List<Map<String,Object>> rlist = new ArrayList<Map<String,Object>>();
		
		for(String satellite:satelist2){
			Map<String,Object> map = new HashMap<String,Object>();
			List<String> slist = new ArrayList<String>();
			map.put("satellite", satellite);
			String level1 ="";
			for(Map<String,String> map2:mlist){
				String satellite1 = map2.get("satellite");
				String sensor1 = map2.get("sensor");
				level1 = map2.get("level");
				if(satellite.equals(satellite1)){
					slist.add(sensor1);
//					map.put("sensor", satellite1+"/"+sensor1);
				}
			}
			map.put("level", level1);
			map.put("sensor", slist);
			rlist.add(map);
		}
		return rlist;
	}
	
	public static List removeDuplicate(List list){  
        List listTemp = new ArrayList();  
        for(int i=0;i<list.size();i++){  
            if(!listTemp.contains(list.get(i))){  
                listTemp.add(list.get(i));  
            }  
        }  
        return listTemp;  
    }
	
	
	
	
}
