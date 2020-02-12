package com.sinosoft.ddss.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.FtpData;
import com.sinosoft.ddss.common.entity.HyData;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.IHyDataService;
import com.sinosoft.ddss.service.ISysConfigService;
import com.sinosoft.ddss.utils.FastJsonUtil;

@RestController
public class HyDataController {
	private static Logger LOGGER = LoggerFactory.getLogger(HyDataController.class);
	@Autowired
	private IHyDataService hyDataService;
	@Autowired
	private ISysConfigService sysConfigService;
	@Autowired
	private JedisClient jedisClient;
	/**
	 * 前台动态显示卫星 传感器 级别 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/hydata/selectData")
	public String selectData(String pid) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(pid)){
			map.put("status", false);
			map.put("msg", "Pid is null");
			return JsonUtils.objectToJson(map);
		}
		try {
			HyData hyData = new HyData();
			hyData.setPid(Integer.valueOf(pid));
			List<HyData> selectData = hyDataService.selectData(hyData);
			if(Integer.valueOf(pid)>0){
				for (HyData hyData2 : selectData) {
					hyData2.setPid(hyData2.getId());
					List<HyData> selectData1=hyDataService.selectData(hyData);
					if(selectData1!=null && selectData1.isEmpty()){
						selectData.addAll(selectData1);
					}
				}
			}
			map.put("status", true);
			map.put("data", selectData);
			map.put("msg", "Data list query successed");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Data list query has exception");
			LOGGER.error("Data list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 前台卫星 传感器 级别树节点 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/hydata/selectDataTreeNode")
	public String selectDataTreeNode() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			map.put("status", true);
			//map.put("data", sb.toString());
			map.put("data", hyDataService.selectDataList());
			map.put("msg", "Data list query successed");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Data list query has exception");
			LOGGER.error("Data list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
	/**
	 * 前台卫星 传感器 级别树节点 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/oauth/hydata/selectDataTreeNodeMultistage")
	public String selectDataTreeNodeMultistage() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			HyData hyData0 = new HyData();
			hyData0.setPid(0);
			List<HyData> selectDataList = hyDataService.selectData(hyData0);
			StringBuilder sb=new StringBuilder();
			if(selectDataList!=null && !selectDataList.isEmpty()){
				sb.append("[");
				for (HyData hyData : selectDataList) {
					sb.append("{");
					sb.append("title: '"+hyData.getName()+"',");
					sb.append("key: '"+hyData.getName()+"',");
					hyData.setPid(hyData.getId());
					List<HyData> sensorSelectData = hyDataService.selectData(hyData);
					if(sensorSelectData!=null && !sensorSelectData.isEmpty()){
						sb.append("children: [");
						for (HyData hyData2 : sensorSelectData) {
							sb.append("{title: '"+hyData2.getName()+"',");
							sb.append("key: '"+hyData2.getName()+"',");
							hyData2.setPid(hyData2.getId());
							List<HyData> levelSelectData = hyDataService.selectData(hyData2);
							if(levelSelectData!=null && !levelSelectData.isEmpty()){
								sb.append("children: [");
								for (HyData hyData3 : levelSelectData) {
									sb.append("{ title: '"+hyData3.getName()+"', key: '"+hyData3.getName()+"' },");
								}
								sb=new StringBuilder(sb.substring(0, sb.length()-1));
								sb.append("]},");
							}
							else{
								sb=new StringBuilder(sb.substring(0, sb.length()-1));
								sb.append("},");
							}
						}
						sb=new StringBuilder(sb.substring(0, sb.length()-1));
						sb.append("]},");
					}
					else{
						sb=new StringBuilder(sb.substring(0, sb.length()-1));
						sb.append("},");
					}
				}
				sb=new StringBuilder(sb.substring(0, sb.length()-1));
				sb.append("]");
			}
			String str = sb.toString();//采用toString()方法
			String[] arr = str.split(","); // 用,分割
			System.out.println(Arrays.toString(arr));
			map.put("status", true);
			//map.put("data", sb.toString());
			map.put("data", hyDataService.selectDataList());
			map.put("msg", "Data list query successed");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Data list query has exception");
			LOGGER.error("Data list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	/**
	 * 
	 * 根据级别获取卫星传感器  
	 * 
	 * @param name为空，则查所有级别   name和token都不为空，则根据name传过来的级别查卫星和传感器   LN
	 * @return
	 */
	@RequestMapping(value = "/oauth/hydata/selectLevelNode")
	public String selectLevelNode(@RequestBody FtpData ftpData) {
//		public String selectLevelNode( FtpData ftpData) {
		String name = ftpData.getProductLevel();
		String token = ftpData.getToken();
		String type = ftpData.getWorkType();
		Map<String, Object> map = new HashMap<String, Object>();
		SysConfig sysConfig = new SysConfig();
		sysConfig.setConfigKey("levelCondition");
		if(StringUtils.isBlank(name)){
			try {
				List<SysConfig> listConfig = sysConfigService.selectByKeyLevel(sysConfig);
				// 定制
				if(!StringUtils.isBlank(type)&&type.equals("2")){
					// 查询允许被定制的级别
					SysConfig sysConfig2 = new SysConfig();
					sysConfig2.setConfigKey("customlevel");
					List<SysConfig> customlevel = sysConfigService.selectByKeyLevel(sysConfig2);
					// 取交集
					// listConfig.retainAll(customlevel);
					listConfig = customlevel;
				}else{
					listConfig = sysConfigService.selectByKeyLevel(sysConfig);
				}
				
				map.put("data", listConfig);
				map.put("msg", "query config list was successed");
				map.put("status", true);
			} catch (Exception e) {
				e.printStackTrace();
				map.put("status", false);
				map.put("msg", "Data list query has exception");
				LOGGER.error("Data list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
			}
			return JsonUtils.objectToJson(map);
		}
		try {
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", "Token is null");
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", "Token already invalid, Please login");
				return JsonUtils.objectToJson(map);
			}
			User bean = FastJsonUtil.toBean(is, User.class);
			List<SysResource> sysSource = bean.getSysSource();
			String satelliteName="";
			for (int i = 0; i < sysSource.size(); i++) {
				if(sysSource.get(i).getUrl().equals("satellite")){
					satelliteName += sysSource.get(i).getEnname()+",";
				}
			}
			if(!"".equals(satelliteName)){
				satelliteName=satelliteName.substring(0,satelliteName.length()-1);
			}
//			satelliteName = "HY-2B,HY-2C";
			List<Map<String,Object>> configValue = sysConfigService.selectSallitedSensorByLevel(name,satelliteName,ftpData.getSeries());
//			HyData hyData = new HyData();
//			hyData.setName(levelName);
//			map.put("status", true);
//			map.put("data", hyDataService.selectLevelNode(hyData));
			map.put("data", configValue);
			map.put("msg", "Data list query successed");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Data list query has exception");
			LOGGER.error("Data list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	
	
	
	/**
	 * 
	 * 根据系列获取卫星传感器级别信息
	 * @author josen
	 * @param name为空，则查所有级别   name和token都不为空，则根据name传过来的级别查卫星和传感器   LN
	 * @return
	 */
	@RequestMapping(value = "/oauth/hydata/selectNodeBySeries")
	public String selectNodeBySeries(@RequestBody FtpData ftpData) {
//	public String selectNodeBySeries( FtpData ftpData) {
//		系列
		String series = ftpData.getSeries();
//		用户token
		String token = ftpData.getToken();
//		检索方式（1,订购  2.定制）
		String type = ftpData.getWorkType();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
//		如果token为空返回前台
		if(StringUtils.isBlank(token)){
			map.put("status", false);
			map.put("msg", "Token is null");
			return JsonUtils.objectToJson(map);
		}
//		如果redis里没有该token返回前台
		String is = jedisClient.get(token);
//		权限集合
		String salliteSource = "";
		String sensorSource = "";
		String levelSource = "";
		String pid = "";
		
		User user = new User();
		
		if(StringUtils.isBlank(is)){
			map.put("status", false);
			map.put("msg", "Token already invalid, Please login");
			return JsonUtils.objectToJson(map);
		}else{
//			获取用户的权限
			 user = JsonUtils.jsonToPojo(is, User.class);
			List<SysResource> sysResource_list = user.getSysSource();
			for(SysResource sysResource:sysResource_list){
				String url = sysResource.getUrl();
//				获取卫星权限
				if("satellite".equals(url)){
					salliteSource+=sysResource.getEnname()+",";
				}else if("sensor".equals(url)){
					sensorSource+=sysResource.getEnname()+",";
					if(sysResource.getEnname().equals(ftpData.getSensor())){
						pid = String.valueOf(sysResource.getResourceid());
					}
				}
				if("level".equals(url)){
					if(String.valueOf(sysResource.getPid()).equals(pid)){
						levelSource+=sysResource.getEnname()+",";
					}
				}
				
			}
			salliteSource = salliteSource.substring(0, salliteSource.length()-1);
		}
//		type=3 根据传感器查询级别
		if(!StringUtils.isBlank(type)&&type.equals("3")){
			String sensor = ftpData.getSensor();
			String satellite=ftpData.getSatellite();
			if(!StringUtils.isBlank(sensor)){
				//为了区分海动跟中海的SCA级别
				if(satellite!=null){
					if(satellite.equals("CFOSAT")&& sensor.equals("SCA")){
						sensor=satellite+"_"+sensor;
					}
				}
				String levels = jedisClient.get(sensor);
				List<Object> levelList = new ArrayList<Object>();
				if(null!=levels&&levels.length()>0){
					if(null!=levelSource&&levelSource.length()>0){
					for(String level:levels.split(",")){
							if(levelSource.indexOf(level)>-1){
								
								Map<String,String> level_map = new HashMap<String,String>();
								level_map.put("level", level);
								if(series!=null&&series.indexOf("GF")>-1){//高分默认L1A
									if(level.indexOf("L1A")>-1){
										level_map.put("checked", "true");
									}else{
										level_map.put("checked", "");
									}
								}else if(series!=null&&series.indexOf("WaterJet")>-1){//水色默认L2A
									
									 if(level.indexOf("L2A")>-1){
										level_map.put("checked", "true");
									}else{
										level_map.put("checked", "");
									}
									
									
								}else if(series!=null&&series.indexOf("SeaAction")>-1){//海动系列默认L2B
									
									
									if(sensor.indexOf("ALT")>-1){
										if(level.indexOf("IDR")>-1){
											level_map.put("checked", "true");
										}else{
											level_map.put("checked", "");
										}
									}else if(level.indexOf("L2B")>-1){
										level_map.put("checked", "true");
									}else{
										level_map.put("checked", "");
									}
									
									
//									if(level.indexOf("L2B")>-1){
//										level_map.put("checked", "true");
//									}else{
//										level_map.put("checked", "");
//									}
								}else if(level.indexOf("L2A")>-1){//其他的默认L2A
									level_map.put("checked", "true");
								}else{
									level_map.put("checked", "");
								}
								levelList.add(level_map);
//								System.out.println(levelList);
							}
						}
//						else{
//							Map<String,String> level_map = new HashMap<String,String>();
//							level_map.put("level", level);
//							if(level.indexOf("L2")>-1){
//								level_map.put("checked", "true");
//							}
//							levelList.add(level_map);
//						}
						}else{
							Map<String,String> level_map = new HashMap<String,String>();
							level_map.put("level", null);
							levelList.add(level_map);
						}
					}
					map.put("level", levelList);
					return JsonUtils.objectToJson(map);
					
				
			}
		}
		
		
		
//		从redis中获取系列对应的卫星
		String satelliteds = jedisClient.get(series+"Series");
		if(null!=satelliteds&&satelliteds.length()>0){
			List<Object> satellitedList = new ArrayList<Object>();
			for(String satellited:satelliteds.split(",")){
				if(null!=salliteSource&&salliteSource.length()>0){
					if(salliteSource.indexOf(satellited)>-1){
						Map<String,String> satellitMap = new HashMap<String,String>();
						satellitMap.put("satellite", satellited);
						satellitMap.put("checked", "true");
						satellitedList.add(satellitMap);
					}
				}
//				else{
//					satellitedList.add(satellited);
//				}
				
			}
			map.put("sallited", satellitedList);
		}else{
			map.put("status", false);
			map.put("msg", "Sallited is null");
			return JsonUtils.objectToJson(map);
		}
		
//		从redis中获取系列对应的传感器
//		String sensors = jedisClient.get(series+"Sensor");
//		if(null!=sensors&&sensors.length()>0){
//			List<Object> sensorList = new ArrayList<Object>();
//			for(String sensor:sensors.split(",")){
//				Map<String,Object> sensorMap = new HashMap<String,Object>();
//				sensorMap.put("sensor", sensor);
//				String levels = jedisClient.get(sensor);
//				if(null!=levels&&levels.length()>0){
//					List<String> levelList = new ArrayList<String>();
//					for(String level:levels.split(",")){
//						levelList.add(level);
//					}
//					sensorMap.put("level", levelList);
//				}
//				
//				sensorList.add(sensorMap);
//			}
//			map.put("sensor", sensorList);
//		}else{
//			map.put("status", false);
//			map.put("msg", "Sensor is null");
//			return JsonUtils.objectToJson(map);
//		}
		
//		从redis中获取系列对应的传感器
		String sensors = jedisClient.get(series+"Sensor");
		if(null!=sensors&&sensors.length()>0){
			List<Object> sensorList = new ArrayList<Object>();
			for(String sensor:sensors.split(",")){
				if(null!=sensorSource&&sensorSource.length()>0){
					if(sensorSource.indexOf(sensor)>-1){
						
						Map<String,String> sensorMap = new HashMap<String,String>();
						sensorMap.put("sensor", sensor);
						if(sensor.equals(sensors.split(",")[0])){
							sensorMap.put("checked", "true");
						}else{
							sensorMap.put("checked", "");
						}
						
						sensorList.add(sensorMap);
//					sensorList.add(sensor);
					}
				}
//				else{
//					sensorList.add(sensor);
//				}
			}
			if(ftpData.getSensor()==null){
				ftpData.setSensor(String.valueOf(sensorList.get(0)));
			}
			map.put("sensor", sensorList);
		}else{
			map.put("status", false);
			map.put("msg", "Sensor is null");
			return JsonUtils.objectToJson(map);
		}
		if(type==null||!type.equals("3")){
//			User user = JsonUtils.jsonToPojo(is, User.class);
			List<SysResource> sysResource_list = user.getSysSource();
 			for(SysResource sysResource:sysResource_list){
//				if(sysResource.getEnname().equals(ftpData.getSensor())){
 				if(ftpData.getSensor().indexOf(sysResource.getEnname())>-1){
					pid = String.valueOf(sysResource.getResourceid());
				}
				String url = sysResource.getUrl();
				if("level".equals(url)){
					if(String.valueOf(sysResource.getPid()).equals(pid)){
						levelSource+=sysResource.getEnname()+",";
					}
				}
				
			}
		}

		
//		获取级别
		String[] sensorName = sensors.split(",");
		for(String sensor:sensorName){
			//如果是中法系列 Sca的key是SFOSAT_SCA(为了和海动下的SCA做区别)
			if(series.equals("SinoFrench") && sensor.equals("SCA")){
				sensor="CFOSAT_SCA";
			}
			String levels = jedisClient.get(sensor);
			levelSource = "";
			List<SysResource> sysResource_list = user.getSysSource();
			for(SysResource sysResource:sysResource_list){
				String url = sysResource.getUrl();
//				获取卫星权限
				if("satellite".equals(url)){
					salliteSource+=sysResource.getEnname()+",";
				}else if("sensor".equals(url)){
					sensorSource+=sysResource.getEnname()+",";
					if(sysResource.getEnname().equals(sensor)){
						pid = String.valueOf(sysResource.getResourceid());
					}
				}
				if("level".equals(url)){
					if(String.valueOf(sysResource.getPid()).equals(pid)){
						levelSource+=sysResource.getEnname()+",";
					}
				}
				
			}
			
			if(null!=levels&&levels.length()>0){
				List<Object> levelList = new ArrayList<Object>();
				if(null!=levelSource&&levelSource.length()>0){
					for(String level:levels.split(",")){
						if(levelSource.indexOf(level)>-1){
							
							Map<String,String> level_map = new HashMap<String,String>();
							level_map.put("level", level);
							if(series!=null&&series.indexOf("GF")>-1){//高分默认L1A
								if(level.indexOf("L1A")>-1){
									level_map.put("checked", "true");
								}else{
									level_map.put("checked", "");
								}
							}else if(series!=null&&series.indexOf("WaterJet")>-1){//水色默认L2A
								
								if(level.indexOf("L2A")>-1){
									level_map.put("checked", "true");
								}else{
									level_map.put("checked", "");
								}
								
								
							}else if(series!=null&&series.indexOf("SeaAction")>-1){//海动系列默认L2B
								
								
								if(sensor.indexOf("ALT")>-1){
									if(level.indexOf("IDR")>-1){
										level_map.put("checked", "true");
									}else{
										level_map.put("checked", "");
									}
								}else if(level.indexOf("L2B")>-1){
									level_map.put("checked", "true");
								}else{
									level_map.put("checked", "");
								}
								
							}else if(level.indexOf("L2A")>-1){//其他的默认L2A
								level_map.put("checked", "true");
							}else{
								level_map.put("checked", "");
							}
							levelList.add(level_map);
							
							
							
							
//							Map<String,String> level_map = new HashMap<String,String>();
//							level_map.put("level", level);
//							
//							if(series!=null&&series.indexOf("GF")>-1){
//								if(level.indexOf("L1A")>-1){
//									level_map.put("checked", "true");
//								}else{
//									level_map.put("checked", "");
//								}
//							}else if(level.indexOf("L2A")>-1){
//								level_map.put("checked", "true");
//							}else{
//								level_map.put("checked", "");
//							}
//							levelList.add(level_map);
//							System.out.println(levelList);
						}
					}
				}else{
						Map<String,String> level_map = new HashMap<String,String>();
						level_map.put("level", null);
						levelList.add(level_map);
					}
				if(sensor.equals("CFOSAT_SCA")){
					sensor="SCA";
				}
				map.put(sensor, levelList);
			}
		}
//		return JsonUtils.objectToJson(map).replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
		return JsonUtils.objectToJson(map);
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
