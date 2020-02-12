package com.sinosoft.ddss.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.HyData;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.UserCount;
import com.sinosoft.ddss.common.entity.UserRole;
import com.sinosoft.ddss.common.entity.UserTask;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.dao.HyDataMapper;
import com.sinosoft.ddss.dao.UserMapper;
import com.sinosoft.ddss.dao.UserRoleMapper;
import com.sinosoft.ddss.dataDao.GeoMapper;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.model.ConfigPo;
import com.sinosoft.ddss.model.Place;
import com.sinosoft.ddss.service.GeoService;
import com.sinosoft.ddss.service.ISysConfigService;
import com.sinosoft.ddss.service.ISysResourceService;
import com.sinosoft.ddss.service.IUserCountService;
import com.sinosoft.ddss.service.IUserService;
import com.sinosoft.ddss.service.IUserWebService;
import com.sinosoft.ddss.utils.FastJsonUtil;
import com.sinosoft.ddss.utils.LoggerUtils;
import com.sinosoft.ddss.utils.Propertie;
import com.sinosoft.ddss.utils.ReturnDDSSIXmlUtils;
import com.sinosoft.ddss.utils.ReturnSSPXmlUtils;
import com.sinosoft.ddss.utils.ReturnXMLUtil;
import com.sinosoft.ddss.utils.ValidatorXML;
import com.sinosoft.ddss.utils.XmlToMapUtils;

@WebService(serviceName = "userWebService", // 与接口中指定的name一致
		targetNamespace = "http://service.ddss.sinosoft.com/", // 与接口中的命名空间一致,一般是接口的包名倒
		endpointInterface = "com.sinosoft.ddss.service.IUserWebService"// 接口地址
)
@Service
@Transactional
@Component
public class UserWebServiceImpl implements IUserWebService {

	private static Logger LOGGER = LoggerFactory.getLogger(UserWebServiceImpl.class);
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private JedisClient jedisClient;
	@Autowired
	private IUserService userService;
	@Autowired
	private GeoMapper geoMapper;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	private ISysResourceService sysResourceService;
	
	@Autowired
	private IUserCountService userCountService;
	
	@Autowired
	private HyDataMapper hyDataMapper;
	
	@Autowired
	private ISysConfigService sysConfigService;
	
	@Autowired
	private GeoService geoService;
	/*
	 * 与共享 用户注册
	 */
	@Override
	public String SSP_DDSS_USERREGIST(String xmlInfo) {
		LOGGER.info("*********SSP DDSS USERREGIST**********");
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		
		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_USERREGIST");
		
		if(result.contains(Constant.REPLYINFO_FAIL)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		
		// 获取参数
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		// 根据id判断是注册还是修改 为空是注册 非空是修改
		// get userPwd userTel userEmail workArea workType userUnit country
		// realName
		String userId = (String) paramMap.get("userId");
		String userName = (String) paramMap.get("userName");
		String userPwd = (String) paramMap.get("userPwd");
		String userTel = (String) paramMap.get("userTel");
		String userEmail = (String) paramMap.get("userEmail");
		String userMobile = (String) paramMap.get("userMobile");
		String workArea = (String) paramMap.get("workArea");
		String workType = (String) paramMap.get("workType");
		String userUnit = (String) paramMap.get("userUnit");
//		String country = (String) paramMap.get("country");
		String country = "1";
//		国家默认中国
		if(country == null || "".equals(country)){
			country = "1";
		}
		
		String realName = (String) paramMap.get("realName");
		// 注册
		if (StringUtils.isBlank(userId)) {
			// 先校验用户名是否唯一
			// 判断用户名为空
			if (StringUtils.isBlank(userName)) {
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User name is null");
			}
			// userPwd
			if (StringUtils.isBlank(userPwd)) {
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User password is null");
			}
			if (StringUtils.isBlank(workType)) {
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User workType is null");
			}
			if (StringUtils.isBlank(workArea)) {
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User workArea is null");
			}
			if (StringUtils.isBlank(userUnit)) {
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User userUnit is null");
			}
//			if (StringUtils.isBlank(country)) {
//				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
//						"User country is null");
//			}
			User checkUserName = userMapper.checkUserName(userName);
			if (null != checkUserName) {
				// 已被使用 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"This user name is already used");
			} else {
				// 可以使用
				User user = new User();
				// setting userName userPwd userTel userEmail workArea workType
				// userUnit country realName
				user.setUserName(userName);
				user.setUserPwd(ActivityStringUtils.createMd5Str(userPwd, null));
				user.setWorkTel(userTel);
				user.setUserEmail(userEmail);
				user.setWorkArea(Integer.valueOf(workArea));
				user.setWorkType(Integer.valueOf(workType));
				user.setWorkUnit(userUnit);
				user.setCountry(Integer.valueOf(country));
				user.setRealName(realName);
				user.setUserMobile(userMobile);
				user.setSource(1);//来源(1：移动，2：PC，3：管理员)
				try {
					BigDecimal id = userMapper.getUsetSeq();
					user.setUserId(id);
					userMapper.insertSelective(user);
//					BigDecimal id = user.getUserId();
					LOGGER.info(id.toString());
					// 保存用户角色
					UserRole userrole = new UserRole();
					userrole.setRoleId(BigDecimal.valueOf(1));// 设置默认角色id
					userrole.setUserId(id);// 设置新增用户返回id
					userRoleMapper.insertSelective(userrole);
					String xmlResult = ReturnXMLUtil.xmlResult(true, messageType, messageID, recipientAddress, originatorAddress,"");
					System.err.println(xmlResult);
					return xmlResult;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"Register user failed"+e.getMessage());
				}
			}
		} else {// 修改
			// 可以使用
			// setting userId userName userPwd userTel userEmail workArea
			// workType userUnit country realName
			// userPwd
			User user = new User();
			user.setUserId(BigDecimal.valueOf(Long.valueOf(userId)));
			if (StringUtils.isNotBlank(userPwd)) {
				user.setUserPwd(ActivityStringUtils.createMd5Str(userPwd, null));
			}
			if (StringUtils.isNotBlank(userTel)) {
				user.setWorkTel(userTel);
			}
			if (StringUtils.isNotBlank(userEmail)) {
				user.setUserEmail(userEmail);
			}
			if (StringUtils.isNotBlank(userMobile)) {
				user.setUserMobile(userMobile);
			}
			if (StringUtils.isNotBlank(workArea)) {
				user.setWorkArea(Integer.valueOf(workArea));
			}
			if (StringUtils.isNotBlank(workType)) {
				user.setWorkType(Integer.valueOf(workType));
			}
			if (StringUtils.isNotBlank(userUnit)) {
				user.setWorkUnit(userUnit);
			}
			if (StringUtils.isNotBlank(country)) {
				user.setCountry(Integer.valueOf(country));
			}
			if (StringUtils.isNotBlank(realName)) {
				user.setRealName(realName);
			}
			System.out.println(user);
			try {
				int i = userMapper.updateByPrimaryKeySelective(user);
				if (i > 0) {
					return ReturnXMLUtil.xmlResult(true, messageType, messageID, recipientAddress, originatorAddress,"");
				} else {
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"No data changed");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"Failure");
			}
		}
	}

	/**
	 * 与共享 配置信息获取
	 * 
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_CONFIGINFO(String xmlInfo) {
		// TODO Auto-generated method stub
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		
//		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_CONFIGINFO");
		// webservice测试设为true
		String result = Constant.REPLYINFO_SUCCESS;
		if(result.contains(Constant.REPLYINFO_FAIL)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		try {
			// 直接获取配置表里面的 行业类别 
			String workArea = jedisClient.get("workArea");
			String parse1 = (String) JSON.parse(workArea);
			List<ConfigPo> workArealist = FastJsonUtil.toList(parse1, ConfigPo.class);
			//单位类别 
			String workType = jedisClient.get("workType");
			String parse2= (String) JSON.parse(workType);
			List<ConfigPo> workTypelist = FastJsonUtil.toList(parse2, ConfigPo.class);
			//用户国别
			List<Place> placeByCode = geoMapper.getPlaceByCode("world");
			return ReturnSSPXmlUtils.SSP_DDSS_CONFIGINFO(messageType, messageID, recipientAddress, originatorAddress,
					workArealist, workTypelist, placeByCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,e.getMessage());
		}
	}

	/**
	 * 用户登录验证
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_USERLOGIN(String xmlInfo) {
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		
		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_USERLOGIN");
		
		if(result.contains(Constant.REPLYINFO_FAIL)){
			LOGGER.error(LoggerUtils.logFormatMessage("用户登录验证"),LoggerUtils.logObjectArray(Constant.SERVICE,Constant.SSP,xmlInfo,result));
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		// 获取参数
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		String userName = (String)paramMap.get("userName");
		String userPwd = (String)paramMap.get("userPwd");
		if(StringUtils.isBlank(userName)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User name is null");
		}
		if(StringUtils.isBlank(userPwd)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User password is null");
		}
		try {
			//检验用户名
			User checkUserName = userMapper.checkUserName(userName);
			String pwd = ActivityStringUtils.createMd5Str(userPwd, null);
			if (null == checkUserName) {
				// 用户名不存在 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User does not exist");
			} else if(!checkUserName.getUserPwd().equals(pwd)){ //比较密码
				// 密码错误 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User password is wrong");
			} else {
				Integer status = checkUserName.getStatus();
				if(status==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user is locked");
				}
				Integer auditStatus = checkUserName.getAuditStatus();
				if(auditStatus==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user need audit");
				} else if(auditStatus==2){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user audited failed");
				}
				checkUserName.setUserPwd(null);
				//查询权限
				List<SysResource> sysResourceList = sysResourceService.listSysResource(checkUserName.getUserId());
				checkUserName.setSysSource(sysResourceList);
				//认证
				String token = restTemplate.getForObject(Propertie.key("OAUTH_URL")+"/token/authentication", String.class);
//				String token = restTemplate.getForObject("http://172.16.25.27:9001/token/authentication", String.class);
				if (null != token && !"".equals(token)) {
					String objectToJson = JsonUtils.objectToJson(checkUserName);
					token = "token"+token;
					jedisClient.set(token, objectToJson);
					jedisClient.expire(token, 300);
				}
				//记录用户登陆时间
				UserCount uc = new UserCount();
				uc.setUserName(userName);
				userCountService.insertLoginInfo(uc);
				String xmlResult = ReturnSSPXmlUtils.SSP_DDSS_USERLOGIN(messageType, messageID, recipientAddress, originatorAddress,userName,token);
				LOGGER.error(Constant.LOGGER,LoggerUtils.logObjectArray(Constant.SERVICE,Constant.SSP,xmlInfo,xmlResult));
				return xmlResult;
			}
		} catch (Exception e) {
			String xmlResult = ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					e.getMessage());
			LOGGER.error(Constant.LOGGER,LoggerUtils.logObjectArray(Constant.SERVICE,Constant.SSP,xmlInfo,xmlResult));
			return xmlResult;
		}
	}
	
	/**
	 * 与共享 用户信息查询
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_USERINFO(String xmlInfo) {
		// TODO Auto-generated method stub
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
//		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_USERINFO");
		String result= Constant.REPLYINFO_SUCCESS;
		if(result.contains(Constant.REPLYINFO_FAIL)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		// 获取参数
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		String userName = (String)paramMap.get("userName");
		if(StringUtils.isBlank(userName)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User name is null");
		}
		try {
			User userinfo = userMapper.checkUserName(userName);
			if (null == userinfo) {
				// 用户名不存在 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User does not exist");
			} else {
				String s = "";
				if(userinfo.getWorkArea() != null){
					s += "," + userinfo.getWorkArea();
				}
				if(userinfo.getApplicationArea() != null){
					s += "," + userinfo.getApplicationArea();
				}
				if(userinfo.getWorkType() != null){
					s += "," + userinfo.getWorkType();
				}
//				ids +=  userinfo.getWorkArea() + "," + userinfo.getApplicationArea() + "," + userinfo.getWorkType();
				String ids = s.substring(1, s.length());
				// 设置配置表信息
				List<SysConfig> listSysConfig = sysConfigService.listSysConfigById(ids);
				for (SysConfig sysConfig : listSysConfig) {
					if (userinfo.getWorkArea() != null && userinfo.getWorkArea().toString().equals(sysConfig.getId().toString())) {// 用户行业
						userinfo.setWorkAreaStr(userinfo.getWorkArea()+"_"+sysConfig.getConfigValue());
					} else if (userinfo.getApplicationArea() != null && userinfo.getApplicationArea().toString().equals(sysConfig.getId().toString())) {// 应用领域
						userinfo.setApplicationAreaStr(userinfo.getApplicationArea()+"_"+sysConfig.getConfigValue());
					} else if (userinfo.getWorkType() != null && userinfo.getWorkType().toString().equals(sysConfig.getId().toString())) { // 单位类别
						userinfo.setWorkTypeStr(userinfo.getWorkType()+"_"+sysConfig.getConfigValue());
					}
				}
				// setting place
				List<Place> province = geoMapper.getPlaceByCode("world");
				for (Place place : province) {
					if(place.getGid().equals(userinfo.getCountry())){
						userinfo.setProvinceStr(userinfo.getCountry()+"_"+place.getNamechn());
						break;
					}
				}
				return ReturnSSPXmlUtils.SSP_DDSS_USERINFO(messageType, messageID, recipientAddress, originatorAddress,userinfo);
			}
		} catch (Exception e) {
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					e.getMessage());
		}
	}
	/**
	 * 与共享 数据集信息检索与反馈
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_DATASETRETRIEVAL(String xmlInfo) {
		// TODO Auto-generated method stub
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		//接收方
		String creationTime = (String) headMap.get(Constant.CREATION_TIME);
		
		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_DATASETRETRIEVAL");
		
		if(result.contains(Constant.REPLYINFO_FAIL)){
			LOGGER.error(LoggerUtils.logFormatMessage("数据集信息检索与反馈"),LoggerUtils.logObjectArray(Constant.SERVICE,Constant.SSP,xmlInfo,result));
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
//		从xml中获取用户名
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		String userName = (String) paramMap.get("userName");
		User user = new User();
		user.setUserName(userName);
		try {
			user = userService.getUserByName(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<SysResource> sysResourceList = new ArrayList<SysResource>();
		try {
			sysResourceList = sysResourceService.listSysResource(user.getUserId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		权限集合
		String salliteSource = "";
		String sensorSource = "";
		String levelSource = "";
		String seriesSource = "";
		for(SysResource sysResource:sysResourceList){
			String url = sysResource.getUrl();
//			获取卫星权限
			if("satellite".equals(url)){
				salliteSource+=sysResource.getEnname()+",";
			}else if("sensor".equals(url)){
				sensorSource+=sysResource.getEnname()+",";
			}else if("level".equals(url)){
				levelSource+=sysResource.getEnname()+",";
			}else if("series".equals(url)){
				seriesSource+=sysResource.getEnname()+",";
			}
		}
		if(salliteSource.length()>0){
			salliteSource = salliteSource.substring(0, salliteSource.length()-1);
		}
		if(sensorSource.length()>0){
			sensorSource = sensorSource.substring(0, sensorSource.length()-1);
		}
		if(levelSource.length()>0){
			levelSource = levelSource.substring(0, levelSource.length()-1);
		}
		
		
//		根据用户名查询用户所有的系列和传感器级别和卫星（新方法-20181213）
		String xml_sensor = "";
		String serieses = jedisClient.get("Series");
		if(null!=serieses&&serieses.length()>0){
			for(String series:serieses.split(",")){
				if(seriesSource.indexOf(series)>-1){
					xml_sensor += "<SeriesList>\n";
					xml_sensor+="<Series>"+series+"</Series>\n";
//		从redis中获取系列对应的卫星
					String satelliteds = jedisClient.get(series+"Series");
					if(null!=satelliteds&&satelliteds.length()>0){
						xml_sensor+="<SatList>\n";
						for(String satellited:satelliteds.split(",")){
							if(salliteSource.indexOf(satellited)>-1){
								xml_sensor+="<satelliteID>"+satellited+"</satelliteID>\n";
							}
						}
						xml_sensor+="</SatList>\n";
					}
					
//		从redis中获取系列对应的传感器
					String sensors = jedisClient.get(series+"Sensor");
					if(null!=sensors&&sensors.length()>0){
						for(String sensor:sensors.split(",")){
							xml_sensor+="<SensorList>\n";
							if(sensorSource.indexOf(sensor)>-1){
								xml_sensor+="<sensorID>"+sensor+"</sensorID>\n";
								String levels = jedisClient.get(sensor);
								if(null!=levels&&levels.length()>0){
									xml_sensor+="<LevelList>\n";
									for(String level:levels.split(",")){
										if(levelSource.indexOf(level)>-1){
											xml_sensor+="<productLevel>"+level+"</productLevel>\n";
										}
									}
									xml_sensor+="</LevelList>\n";
								}
							}
							xml_sensor+="</SensorList>\n";
						}
					}
					xml_sensor+="</SeriesList>\n";
					
				}
			}
		}
		
		
	

		StringBuffer xml = new StringBuffer();
		
		
		/*旧方法-20181213bk*/
/*		// 所有卫星
		Set<String> allSatellite = new HashSet<String>();
		Map<String,String> satelliteSensorLevel = new HashMap<String, String>();
		Map<String,String[]> satelliteMap = new HashMap<String, String[]>();
		// 获取所有级别
		String allLevel = jedisClient.get("levelCondition");
		if(StringUtils.isNotBlank(allLevel)){
			// 根据级别获取各个级别下的卫星
			String[] levels = allLevel.split(",");
			for (String level : levels) {
				// 根据级别获取卫星
				String levelSatellite = jedisClient.get(level);
				if(StringUtils.isNotBlank(levelSatellite)){
					String[] levelSatellites = levelSatellite.split(",");
					// 卫星map
					for (String satellite : levelSatellites) {
						allSatellite.add(satellite);
						// 根据级别卫星获取传感器
						String levelSatelliteSensor = jedisClient.get(level+"_"+satellite);
						if(StringUtils.isNotBlank(levelSatelliteSensor)){
							String[] satelliteSensors = levelSatelliteSensor.split(",");
							satelliteMap.put(satellite, satelliteSensors);
							for (String sensor : satelliteSensors) {
								if(satelliteSensorLevel.get(satellite+"_"+sensor)==null){
									satelliteSensorLevel.put(satellite+"_"+sensor, level);
								}else{
									satelliteSensorLevel.put(satellite+"_"+sensor, satelliteSensorLevel.get(satellite+"_"+sensor)+","+level);
								}
							}
						}
					}
				}
			}
		}*/
		
		
		
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<InterfaceFile>\n");
		xml.append("<FileHead>\n");
		xml.append("<messageType>" + messageType + "</messageType>\n");
		xml.append("<messageID>" + messageID + "</messageID>\n");
		xml.append("<originatorAddress>" + originatorAddress + "</originatorAddress>\n");
		xml.append("<recipientAddress>" + recipientAddress + "</recipientAddress>\n");
		xml.append("<creationTime>" + creationTime + "</creationTime>\n");
		xml.append("</FileHead>\n");
		xml.append("<FileBody>\n");
		
//		新方法
		xml.append(xml_sensor);
		
		
		/*旧方法-20181213bk*/
/*		for (String satellite : allSatellite) {
			xml.append("<SatList>\n");
			xml.append("<satelliteID>"+satellite+"</satelliteID>\n");
//			System.out.println("***"+satellite);
			String[] sensors = satelliteMap.get(satellite);
			for (String sensor : sensors) {
				xml.append("<SensorList>\n");
				xml.append("<sensorID>"+sensor+"</sensorID>\n");
//				System.out.println("******"+sensor);
				String levels = satelliteSensorLevel.get(satellite+"_"+sensor);
				if(StringUtils.isNotBlank(levels)){
					String[] levelS = levels.split(",");
					xml.append("<LevelList>\n");
					for (String level : levelS) {
						xml.append("<levelID>"+level+"</levelID>\n");
//						System.out.println("*********"+level);
					}
					xml.append("</LevelList>\n");
				}
				xml.append("</SensorList>\n");
			}
			xml.append("</SatList>\n");
		}*/
		
		
		xml.append("</FileBody>\n");
		xml.append("</InterfaceFile>\n");
		return xml.toString();
	}
	/**
	 * 与21世纪 订单用户增加
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String DDSS_ORDERUSR_ADD(List<UserTask> userTask) {
		return ReturnDDSSIXmlUtils.DDSS_ORDERUSR_ADD(userTask);
	}
	/**
	 * 与21世纪 订单用户修改
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String DDSS_ORDERUSR_MOD(List<UserTask> userTask) {
		return ReturnDDSSIXmlUtils.DDSS_ORDERUSR_MOD(userTask);
	}
	/**
	 * 与21世纪 订单用户删除
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String DDSS_ORDERUSR_DEL(List<UserTask> userTask) {
		return ReturnDDSSIXmlUtils.DDSS_ORDERUSR_DEL(userTask);
	}
	/**
	 * 与共享 忘记密码
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_FORGOTPSW(String xmlInfo) {
		// TODO Auto-generated method stub
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		
		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_FORGOTPSW");
		
		if(result.contains(Constant.REPLYINFO_FAIL)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		// 获取参数
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		String userName = (String)paramMap.get("userName");
		String userEmail = (String)paramMap.get("userEmail");
		if(StringUtils.isBlank(userName)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User name is null");
		}
		if(StringUtils.isBlank(userEmail)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User email is null");
		}
		try {
			//检验用户名
			User checkUserName = userMapper.checkUserName(userName);
			if (null == checkUserName) {
				// 用户名不存在 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User does not exist");
			} else if(!checkUserName.getUserEmail().equals(userEmail)){ //比较密码
				// 密码错误 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User email is wrong");
			} else {
				Integer status = checkUserName.getStatus();
				if(status==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user is locked");
				}
				Integer auditStatus = checkUserName.getAuditStatus();
				if(auditStatus==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user need audit");
				} else if(auditStatus==2){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user audited failed");
				}
				return ReturnXMLUtil.xmlResult(true, messageType, messageID, recipientAddress, originatorAddress,
						"");
			}
		} catch (Exception e) {
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					e.getMessage());
		}
	}
	/**
	 * 与共享 设置新密码
	 * @param xmlInfo
	 * @return
	 */
	@Override
	public String SSP_DDSS_SETNEWPSW(String xmlInfo) {
		// TODO Auto-generated method stub
		LOGGER.info(xmlInfo);
		// 获取Xml头部信息
		Map<String, Object> headMap = XmlToMapUtils.getHeadMap(xmlInfo);
		//接口实体类型
		String messageType = (String) headMap.get(Constant.MESSAGE_TYPE);
		//消息标识号，由接口服务调用方负责填写，递增到上限后可回滚
		String messageID = (String) headMap.get(Constant.MESSAGE_ID);
		//发送方
		String originatorAddress = (String) headMap.get(Constant.ORIGINATOR_ADDRESS);
		//接收方
		String recipientAddress = (String) headMap.get(Constant.RECIPIENT_ADDRESS);
		
		String result=ValidatorXML.validatorXml(xmlInfo, "SSP/SSP_DDSS_SETNEWPSW");
		
		if(result.contains(Constant.REPLYINFO_FAIL)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, originatorAddress, recipientAddress, result);
		}
		// 获取参数
		Map<String, Object> paramMap = XmlToMapUtils.getParamMap(xmlInfo);
		String userName = (String)paramMap.get("userName");
		String userPwd = (String)paramMap.get("userPwd");
		if(StringUtils.isBlank(userName)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User name is null");
		}
		if(StringUtils.isBlank(userPwd)){
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					"User password is null");
		}
		try {
			//检验用户名
			User checkUserName = userMapper.checkUserName(userName);
			if (null == checkUserName) {
				// 用户名不存在 返回失败
				return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
						"User does not exist");
			} else {
				Integer status = checkUserName.getStatus();
				if(status==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user is locked");
				}
				Integer auditStatus = checkUserName.getAuditStatus();
				if(auditStatus==0){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user need audit");
				} else if(auditStatus==2){
					return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
							"This user audited failed");
				}
				User user = new User();
				user.setUserId(checkUserName.getUserId());
				user.setUserPwd(ActivityStringUtils.createMd5Str(userPwd, null));
				userMapper.updateByPrimaryKeySelective(user);
				return ReturnXMLUtil.xmlResult(true, messageType, messageID, originatorAddress, recipientAddress, "");
			}
		} catch (Exception e) {
			return ReturnXMLUtil.xmlResult(false, messageType, messageID, recipientAddress, originatorAddress,
					e.getMessage());
		}
	}
}
