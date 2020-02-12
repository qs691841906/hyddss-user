//package com.sinosoft.ddss;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.TreeSet;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.sinosoft.ddss.common.entity.DedicatedLine;
//import com.sinosoft.ddss.common.entity.FtpData;
//import com.sinosoft.ddss.common.entity.SysResource;
//import com.sinosoft.ddss.common.entity.User;
//import com.sinosoft.ddss.common.util.JsonUtils;
//import com.sinosoft.ddss.dao.DedicatedLineMapper;
//import com.sinosoft.ddss.jedis.JedisClient;
//import com.sinosoft.ddss.service.ISysConfigService;
//import com.sinosoft.ddss.service.ISysResourceService;
//import com.sinosoft.ddss.service.IUserService;
//////SpringJUnit支持，由此引入Spring-Test框架支持！
//@RunWith(SpringJUnit4ClassRunner.class)
//////指定我们SpringBoot工程的Application启动类
//@SpringApplicationConfiguration(classes = UserApplication.class)
/////由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
//@WebAppConfiguration
//public class UserJUnitTest {
//	
//	@Autowired
//	private JedisClient jedisClient;
//	@Autowired
//	private ISysConfigService sysConfigService;
//	
//	
//	@Test
//	public void testConfig(){
//		FtpData ftpData = new FtpData();
//		ftpData.setSeries("WaterJet");
//		ftpData.setToken("tokena6105bb6fbbb892ec7632fef329c50e0");
//		
//		selectNodeBySeries(ftpData);
//	}
//	
//	
//	public String selectNodeBySeries( FtpData ftpData) {
////		系列
//		String series = ftpData.getSeries();
////		用户token
//		String token = ftpData.getToken();
////		检索方式（1,订购  2.定制）
//		String type = ftpData.getWorkType();
//		
//		Map<String, Object> map = new HashMap<String, Object>();
//		
////		如果token为空返回前台
//		if(StringUtils.isBlank(token)){
//			map.put("status", false);
//			map.put("msg", "Token is null");
//			return JsonUtils.objectToJson(map);
//		}
////		如果redis里没有该token返回前台
//		String is = jedisClient.get(token);
//		if(StringUtils.isBlank(is)){
//			map.put("status", false);
//			map.put("msg", "Token already invalid, Please login");
//			return JsonUtils.objectToJson(map);
//		}
////		从redis中获取系列对应的卫星
//		String satelliteds = jedisClient.get(series+"Series");
//		if(null!=satelliteds&&satelliteds.length()>0){
//			List<String> satellitedList = new ArrayList<String>();
//			for(String satellited:satelliteds.split(",")){
//				satellitedList.add(satellited);
//			}
//			map.put("sallited", satellitedList);
//		}else{
//			map.put("status", false);
//			map.put("msg", "Sallited is null");
//			return JsonUtils.objectToJson(map);
//		}
//		
////		从redis中获取系列对应的传感器
//		String sensors = jedisClient.get(series+"Sensor");
//		if(null!=sensors&&sensors.length()>0){
//			List<String> sensorList = new ArrayList<String>();
//			for(String sensor:sensors.split(",")){
//				sensorList.add(sensor);
//			}
//			map.put("sensor", sensorList);
//		}else{
//			map.put("status", false);
//			map.put("msg", "Sensor is null");
//			return JsonUtils.objectToJson(map);
//		}
////		获取级别
//		String[] sensorName = sensors.split(",");
//		for(String sensor:sensorName){
//			String levels = jedisClient.get(sensor);
//			if(null!=levels&&levels.length()>0){
//				List<String> levelList = new ArrayList<String>();
//				for(String level:levels.split(",")){
//					levelList.add(level);
//				}
//				map.put(sensor, levelList);
//			}
//		}
//		
//		return JsonUtils.objectToJson(map);
//		
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
////	@Test
////	public void testConfig(){
////		// 所有卫星
////		Set<String> allSatellite = new HashSet<String>();
////		Map<String,String> satelliteSensorLevel = new HashMap<String, String>();
////		Map<String,String[]> satelliteMap = new HashMap<String, String[]>();
////		// 获取所有级别
////		String allLevel = jedisClient.get("levelCondition");
////		if(StringUtils.isNotBlank(allLevel)){
////			// 根据级别获取各个级别下的卫星
////			String[] levels = allLevel.split(",");
////			for (String level : levels) {
////				// 根据级别获取卫星
////				String levelSatellite = jedisClient.get(level);
////				if(StringUtils.isNotBlank(levelSatellite)){
////					String[] levelSatellites = levelSatellite.split(",");
////					// 卫星map
////					for (String satellite : levelSatellites) {
////						allSatellite.add(satellite);
////						// 根据级别卫星获取传感器
////						String levelSatelliteSensor = jedisClient.get(level+"_"+satellite);
////						if(StringUtils.isNotBlank(levelSatelliteSensor)){
////							String[] levelSatelliteSensors = levelSatelliteSensor.split(",");
////							satelliteMap.put(satellite, levelSatelliteSensors);
////							for (String sensor : levelSatelliteSensors) {
////								if(satelliteSensorLevel.get(satellite+"_"+sensor)==null){
////									satelliteSensorLevel.put(satellite+"_"+sensor, level);
////								}else{
////									satelliteSensorLevel.put(satellite+"_"+sensor, satelliteSensorLevel.get(satellite+"_"+sensor)+","+level);
////								}
////							}
////						}
////					}
////				}
////			}
////		}
////		
////		for (String satellite : allSatellite) {
////			System.out.println("***"+satellite);
////			String[] sensors = satelliteMap.get(satellite);
////			for (String sensor : sensors) {
////				System.out.println("******"+sensor);
////				String levels = satelliteSensorLevel.get(satellite+"_"+sensor);
////				if(StringUtils.isNotBlank(levels)){
////					String[] levelS = levels.split(",");
////					for (String level : levelS) {
////						System.out.println("*********"+level);
////					}
////				}
////			}
////		}
////	}
////  
////
////	 @Resource
////	 private IUserService userService;
////	 @Autowired
////	 private ISysResourceService sysResourceService;
////	 @Autowired
////	 private DedicatedLineMapper dedicatedLineMapper;
////	 
////	 @Autowired
////	 private ISysConfigService iSysConfigService;
////	 
////   @Test
////   @Ignore
////   public void testGetName(){
////	   HttpServletRequest request = null;
////	   User user = new User();
////	   user.setUserName("gongsen11");
////	   user.setRealName("sengong");
////	   user.setUserPwd("123456");
////	   user.setUserSex(1);
////	   user.setUserEmail("go@");
////	   user.setUserMobile("123456");
////	   user.setWorkArea(1);
////	   user.setWorkType(1);
////	   user.setWorkUnit("1");
////	   user.setWorkTel("1234567890");
////	   user.setIdCard("1231241241");
////	   user.setApplicationArea(1);
////	   user.setAddress("shangdi");
////	   user.setFax("12312");
////	   user.setCountry(1);
////	   user.setCity(2);
////	   user.setProvince(2);
////	   user.setStatus(0); //状态(0:锁定 1:正常 2:删除)
////	   user.setAuditStatus(0);//审核状态(0：待审核，1：成功，2：失败)
////	   user.setSource(1);
////	   user.setFounder("gongsen");
////	   //user.setCreateTime(new Date());
////	   user.setAuditFailReason("fail");
////	   try {
////		Assert.assertEquals("",userService.insertUser(user, request));
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////   }
////   @Test
////   @Ignore
////   public void testCheckName(){
////	   try {
////			boolean boo = userService.checkUserName("alibaba");
////			if(boo){
////				System.out.println("**********************************用户名已存在**********************************");
////			} else {
////				System.out.println("**********************************用户名还未使用**********************************");
////			}
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////   }
////   @Test
////   @Ignore
////   public void login(){
////	   try {
////		   System.out.println("**********************************开始**********************************");
////		   List<SysResource> sysResourceList = sysResourceService.listSysResource(BigDecimal.valueOf(1));
////		   System.out.println(sysResourceList);
////		   System.out.println("**********************************结束**********************************");
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////   }
////   @Test
////   public void listUserInit(){
////	   User user = new User();
////	   Map<String, Object> map = new HashMap<String, Object>();
////   		user.setPage(3);
////   		user.setPageSize(3);
////       try {
////			//Integer countUser = userService.countUser(user);
//////			TotalInfo totalInfo = new TotalInfo(countUser,
//////					user.getPageSize(), user.getPage(),
//////					user.getStartNum());
////		//	List<User> listUser = userService.listUser(user);
//////			map.put("data", listUser);
//////			map.put("totalInfo", totalInfo);
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////       System.out.println("**********************************"+map);
////   }
////   
////   @Test
////   @Ignore
////   public void getPassword(){
////	   User user = new User();
////	   user.setUserName("gongsen");
////	   user.setUserEmail("go@");
////       try {
//////    	   List<User> listUser = userService.listUser(user);
////    	   System.out.println("***********************************************");
////    	   System.out.println("***********************************************");
//////    	   System.out.println(listUser.toString());
////    	   System.out.println("***********************************************");
////    	   System.out.println("***********************************************");
////		} catch (Exception e) {
////			e.printStackTrace();
////		}
////   }
////   @Test
////   public void updateUser() {
////	   try {
////		DedicatedLine record = new DedicatedLine();
////		record.setId(BigDecimal.valueOf(1));
////		record.setFtpIpPort("192.168.6.54");
////		record.setFtpNet(0);
////		System.out.println("***********************************************");
////  	   	System.out.println("***********************************************");
////  	   	dedicatedLineMapper.updateByPrimaryKeySelective(record);
////  	   	System.out.println("***********************************************");
////  	   	System.out.println("***********************************************");
////	} catch (Exception e) {
////		e.printStackTrace();
////	}
////	}
////   
////   @Test
////   public void aa() {
////	   try {
////		iSysConfigService.selectSallitedAndSensorByLevel("L1", "admin");
////	} catch (Exception e) {
////		// TODO Auto-generated catch block
////		e.printStackTrace();
////	}
////		
////	}
////   
//   
//}
