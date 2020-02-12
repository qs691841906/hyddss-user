package com.sinosoft.ddss.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sinosoft.ddss.common.base.entity.RestfulJSON;
import com.sinosoft.ddss.common.base.entity.TotalInfo;
import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.EmailTask;
import com.sinosoft.ddss.common.entity.Role;
import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.entity.SysResource;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.UserCount;
import com.sinosoft.ddss.common.entity.query.UserQuery;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.common.util.ExportExcelForShopcar;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.common.util.PropertiesUtils;
import com.sinosoft.ddss.dao.UserMapper;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.model.Place;
import com.sinosoft.ddss.service.DecryptToken;
import com.sinosoft.ddss.service.GeoService;
import com.sinosoft.ddss.service.IEmailTaskService;
import com.sinosoft.ddss.service.IRoleService;
import com.sinosoft.ddss.service.ISysConfigService;
import com.sinosoft.ddss.service.ISysResourceService;
import com.sinosoft.ddss.service.IUserCountService;
import com.sinosoft.ddss.service.IUserService;
import com.sinosoft.ddss.utils.CreateEmailUtils;
import com.sinosoft.ddss.utils.CusAccessObjectUtil;
import com.sinosoft.ddss.utils.Propertie;

import net.sf.json.JSONArray;

@RestController
public class UserController{
	private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private IUserService userService;
//	@Autowired
//	private IUserRoleService userRoleService;
	@Autowired
	private ISysResourceService sysResourceService;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	private JedisClient jedisClient;
//	@Autowired
//	private IPlaceService placeService;
	@Autowired
	private ISysConfigService sysConfigService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private GeoService geoService;
	@Autowired
	private IUserCountService userCountService;
	@Autowired
	private DecryptToken decryptToken;
	@Autowired
	private IEmailTaskService emailTaskService;
    @Autowired
    private UserMapper userMapper;
	/**
	 * <pre>
	 * listUserInit(用户列表查询)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:23:05    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:23:05    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */
	@RequestMapping(value="/oauth/user/list", method = RequestMethod.POST)
	public String listUserInit(@RequestBody UserQuery userQuery) {
//		public String listUserInit( UserQuery userQuery) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = userQuery.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
			//如果前台传入的审核状态（AuditStatus）为3，就表示查询删除状态（status=2）的用户
			if(userQuery.getAuditStatus()!=null){
				if(userQuery.getAuditStatus()==3){
					userQuery.setStatus(2);
					userQuery.setAuditStatus(null);
				}
				
			}
			
			
			// 用户不预警，注释预警代码 2018-11-8 02:17:18 石青山
			boolean callPolice = userQuery.isCallPolice();
			callPolice = false;
			List<User> listUser = null;
			Integer countUser = null;
			// 用户预警时间
			String userWaringTime = jedisClient.get("userWaringTime");
			if(callPolice){
				userQuery.setCallPoliceTime(Integer.parseInt(userWaringTime));
				listUser = this.userService.listUser(userQuery);
				countUser = this.userService.countUser(userQuery);
			}else{
				listUser = this.userService.listUser(userQuery);
				countUser = this.userService.countUser(userQuery);
				// 用户不预警，注释预警代码 2018-11-8 02:17:18 石青山
				/*List<User> listUserCallPolice = this.userService.QueryCallPolice(Integer.parseInt(userWaringTime));
				for (int i = 0; i < listUser.size(); i++) {
					User user=listUser.get(i);
					for (int j = 0; j < listUserCallPolice.size(); j++) {
						if(user.getUserId().equals(listUserCallPolice.get(j).getUserId())){
							user.setCallPolice(true);
							break;
						}
					}
					listUser.set(i, user);
				}*/
			}
			
			for(User user :listUser){
				if(user.getStatus()==2){
					user.setAuditStatusName("已删除");
				}else{
					if(user.getAuditStatus()==0){
						user.setAuditStatusName("待审核");
					}else if(user.getAuditStatus()==1){
						user.setAuditStatusName("审核通过");
					}else if(user.getAuditStatus()==2){
						user.setAuditStatusName("审核不通过");
					}else{
						user.setAuditStatusName("其他");
					}
				}
			}
			
			TotalInfo totalInfo = new TotalInfo(countUser, userQuery.getPageSize(), userQuery.getPage(),
					userQuery.getStartNum());
			map.put("data", listUser);
			map.put("totalInfo", totalInfo);
			map.put("status", true);
			map.put("msg", "User list query successed");
			LOGGER.info("User list query successed");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("User list query has exception, caused by "+e.getMessage(),Constant.SYSTEM);
			map.put("status", false);
			map.put("msg", "User list query has exception");
		}
		return JsonUtils.objectToJson(map);
	}

	@RequestMapping("/user/config")
	public Long testConfig(HttpServletRequest request) {
		jedisClient.set("gs", "gongsen");
		long del = jedisClient.del("gs");
		return del;
	}

	/**
	 * <pre>
	 * registerUser(用户注册)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:22:23    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:22:23    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;param request
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(value="/user/userRegister", method = RequestMethod.POST)
	public String registerUser(@RequestBody User user, HttpServletRequest request) {
//		public String registerUser( User user, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		boolean insertUser;
		
		try {
			//非空判断
			//userNam//用户名
			if(!isBlank(map, "UserName",user.getUserName())){
				return JsonUtils.objectToJson(map);
			}else if(user.getUserName().contains("select")||user.getUserName().contains("from")||user.getUserName().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			//userPwd//密码
			if(!isBlank(map, "UserPwd",user.getUserPwd())){
				return JsonUtils.objectToJson(map);
			}else if(user.getUserPwd().contains("select")||user.getUserPwd().contains("from")||user.getUserPwd().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			//userEmail//邮箱
			if(!isBlank(map, "UserEmail",user.getUserEmail())){
				return JsonUtils.objectToJson(map);
			}else if(user.getUserEmail().contains("select")||user.getUserEmail().contains("from")||user.getUserEmail().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			//userSex//性别
			//realName//姓名
			if(!isBlank(map, "User Real Name",user.getRealName())){
				return JsonUtils.objectToJson(map);
			}else if(user.getRealName().contains("select")||user.getRealName().contains("from")||user.getRealName().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			// 2018-10-31 07:54:27
			/*//idCard//身份证号
			if(!isBlank(map, "User id card",user.getIdCard())){
				return JsonUtils.objectToJson(map);
			}*/
			//userMobile//手机
			if(!isBlank(map, "User Mobile",user.getUserMobile())){
				return JsonUtils.objectToJson(map);
			}else if(user.getUserMobile().contains("select")||user.getUserMobile().contains("from")||user.getUserMobile().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			//workTel//固话
			//fax//传真
			
			
			//province//国家
//			国家默认中国
			if(user.getCountry()==null||"".equals(user.getCountry())){
				user.setCountry(1);
			}
			if(!isNull(map, "User Country",user.getCountry())){
				return JsonUtils.objectToJson(map);
			}
			
//			如果是其他非中国国家就取其他省市字段
			if(user.getCountry()!=1){
				//province//省份
				if(!isNull(map, "User Province",user.getOtherProvince())){
					return JsonUtils.objectToJson(map);
				}
				//city//城市
				if(!isNull(map, "User City",user.getOtherCity())){
					return JsonUtils.objectToJson(map);
				}
			}else{
				//province//其他省份
				if(!isNull(map, "User Province",user.getProvince())){
					return JsonUtils.objectToJson(map);
				}
				//city//其他城市
				if(!isNull(map, "User City",user.getCity())){
					return JsonUtils.objectToJson(map);
				}
			}
			
			//address//通讯地址
			if(!isBlank(map, "User Address",user.getAddress())){
				return JsonUtils.objectToJson(map);
			}else if(user.getAddress().contains("select")||user.getAddress().contains("from")||user.getAddress().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
//			//workArea//用户行业
//			if(!isNull(map, "User WorkArea",user.getWorkArea())){
//				return JsonUtils.objectToJson(map);
//			}
			//workType//单位类别
			if(!isNull(map, "User WorkType",user.getWorkType())){
				return JsonUtils.objectToJson(map);
			}
			//workUnit//用户单位
			if(!isBlank(map, "User WorkUnit",user.getWorkUnit())){
				return JsonUtils.objectToJson(map);
			}else if(user.getWorkUnit().contains("select")||user.getWorkUnit().contains("from")||user.getWorkUnit().contains("%")){
				String ip = CusAccessObjectUtil.getIpAddress(request);
				CusAccessObjectUtil.sendEmailToJosen(ip);
				return JsonUtils.objectToJson(map);
			}
			//applicationArea//应用领域
			if(!isNull(map, "User ApplicationArea",user.getApplicationArea())){
				return JsonUtils.objectToJson(map);
			}
			user.setSource(2); //来源(1：移动，2：PC，3：管理员)
			insertUser = userService.insertUser(user, request);
			if (insertUser) {
				map.put("status", true);
				map.put("msg", "Register successed");
				LOGGER.info("User Register successed");
			} else {
				map.put("status", false);
				map.put("msg", "Register failed");
				LOGGER.info("Register failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Register exception");
			LOGGER.error("Register exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}

		return JsonUtils.objectToJson(map);
	}
	
		/** <pre>insertUser(新增用户)   
		 * Author：sen_kung     
		 * Create date：2018年5月23日 下午4:16:19    
		 * Author：sen_kung      
		 * Update date：2018年5月23日 下午4:16:19    
		 * Description： 
		 * @param user
		 * @param request
		 * @return</pre>    
		 */
		 
	@RequestMapping("/oauth/user/insertUser")
	public String insertUser(@RequestBody User user, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean insertUser;
		try {
			//非空判断
			//userNam//用户名
			if(!isBlank(map, "UserName",user.getUserName())){
				return JsonUtils.objectToJson(map);
			}
			//userPwd//密码
			if(!isBlank(map, "UserPwd",user.getUserPwd())){
				return JsonUtils.objectToJson(map);
			}
			//userEmail//邮箱
			if(!isBlank(map, "UserEmail",user.getUserEmail())){
				return JsonUtils.objectToJson(map);
			}
			//userSex//性别
			//realName//姓名
			if(!isBlank(map, "User Real Name",user.getRealName())){
				return JsonUtils.objectToJson(map);
			}
			// 2018-10-31 07:54:53
			/*//idCard//身份证号
			if(!isBlank(map, "User id card",user.getIdCard())){
				return JsonUtils.objectToJson(map);
			}*/
			//userMobile//手机
			if(!isBlank(map, "User Mobile",user.getUserMobile())){
				return JsonUtils.objectToJson(map);
			}
			//workTel//固话
			//fax//传真
			//province//省份
			if(!isNull(map, "User Province",user.getProvince())){
				return JsonUtils.objectToJson(map);
			}
			//city//城市
			if(!isNull(map, "User City",user.getCity())){
				return JsonUtils.objectToJson(map);
			}
			//address//通讯地址
			if(!isBlank(map, "User Address",user.getAddress())){
				return JsonUtils.objectToJson(map);
			}
			//workArea//用户行业
			/*if(!isNull(map, "User WorkArea",user.getWorkArea())){
				return JsonUtils.objectToJson(map);
			}*/
			//workType//单位类别
			if(!isNull(map, "User WorkType",user.getWorkType())){
				return JsonUtils.objectToJson(map);
			}
			//workUnit//用户单位
			if(!isBlank(map, "User WorkUnit",user.getWorkUnit())){
				return JsonUtils.objectToJson(map);
			}
			//applicationArea//应用领域
			if(!isNull(map, "User ApplicationArea",user.getApplicationArea())){
				return JsonUtils.objectToJson(map);
			}
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
			User u = decryptToken.token2User(is);
			user.setFounder(u.getUserName());
			user.setSource(3); //来源(1：移动，2：PC，3：管理员)
			insertUser = userService.insertUser(user, request);
			if (insertUser) {
				map.put("status", true);
				map.put("msg", "Register successed");
				LOGGER.info("User Register successed");
			} else {
				map.put("status", false);
				map.put("msg", "Register failed");
				LOGGER.info("Register failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Register exception");
			LOGGER.error("Register exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}

		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * checkUserName(注册校验用户是否唯一)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:39:51    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:39:51    
	 * 修改备注： 
	 * &#64;param name
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping("/user/checkUserName")
	public Map<String, Object> checkUserName(String userName, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean boo;
		try {
			if(StringUtils.isBlank(userName)){
				map.put("status", false);
				map.put("msg", "User name is null");
				LOGGER.info("User name is null");
				return map;
			}
//			判断用户名是否存在
			boo = userService.checkUserName(userName);
			if (boo) {
				map.put("status", false);
				map.put("msg", "This user name is already used");
				LOGGER.info("This user name is already used");
			} else {
				map.put("status", true);
				map.put("msg", "This user name can use");
				LOGGER.info("This user name can use");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Check User Name has exception");
			LOGGER.error("Check User Name has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return map;
	}

	/**
	 * <pre>
	 * login(用户登录)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:23:23    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:23:23    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */
	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public String login(@RequestBody User user) {
//	@RequestMapping(value = "/user/login")
//	public String login( User user) {
		
		
		String password = user.getUserPwd();
		String token = "";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(null==user.getUserName()||null==user.getUserPwd()){
				map.put("status", false);
				map.put("msg", "User name or user password is null");
				return JsonUtils.objectToJson(map);
			}
		
//		每次收到用户登录请求后先判断Redis中是否有该用户
			String userName = user.getUserName();
			String tokenKey = userName+"_loginCount";
			String loginCount = jedisClient.get(tokenKey);
			if(loginCount==null||loginCount.equals("")){
//		如果没有就存储 userName+"_loginCount"为1，并且存储一分钟
				jedisClient.set(tokenKey, "1");
				//设置session的过期时间 时间单位是秒
				jedisClient.expire(tokenKey, 60);
			}else{
//				如果有的话就先取出Redis中的用户登录次数
				int loginCount_ = Integer.parseInt(loginCount);
//		如果次数大于等于5次就禁止用户登录，否则就允许用户登录
				if(loginCount_>=5){
					map.put("status", false);
					map.put("logStatus", false);
					map.put("msg", "This user logs in frequently");
					return JsonUtils.objectToJson(map);
				}else{
//					如果次数大于等于5次,就将次数加一再保存到Redis中
					jedisClient.set(tokenKey, String.valueOf(loginCount_+1));
					//设置session的过期时间 时间单位是秒
					jedisClient.expire(tokenKey, 60);
				}
				
				
			}
		

//			先把用户密码进行base64解密
			@SuppressWarnings("restriction")
			byte[] pwd_base64 = new sun.misc.BASE64Decoder().decodeBuffer(password);
			password=new String(pwd_base64,"UTF-8");
			user.setUserPwd(password);
			
			String pwd = ActivityStringUtils.createMd5Str(user.getUserPwd(), null);
			User userByName = userService.getUserByName(user);
			if (null != userByName) {
				if (null != userByName.getUserPwd() && !"".equals(userByName.getUserPwd())
						&& pwd.equals(userByName.getUserPwd())) {
					Integer status = userByName.getStatus();
					if(status==0){
						map.put("status", false);
						map.put("msg", "This user is locked");
						return JsonUtils.objectToJson(map);
					}
					Integer auditStatus = userByName.getAuditStatus();
					if(auditStatus==0){
						map.put("status", false);
						map.put("msg", "This user need audit");
						return JsonUtils.objectToJson(map);
					} else if(auditStatus==2){
						map.put("status", false);
						map.put("msg", "This user audited failed");
						return JsonUtils.objectToJson(map);
					}
					
//					用户登录成功存入明文密码
					User userpass = new User();
					userpass.setUserId(userByName.getUserId());
					userpass.setPassword(password);
					userService.updateUserPassword(userpass);
					
//					userByName.setUserPwd(null);
					List<SysResource> sysResourceList = sysResourceService.listSysResource(userByName.getUserId());
					userByName.setSysSource(sysResourceList);
					map.put("status", true);
					map.put("msg", "Login success");
					map.put("userName", userByName.getUserName());
					token = restTemplate.getForObject(Propertie.key("OAUTH_URL")+"/token/authentication", String.class);
					//token = restTemplate.getForObject("http://172.16.25.27:9001/token/authentication", String.class);
					if (null != token && !"".equals(token)) {
						String objectToJson = JsonUtils.objectToJson(userByName);
						token = "token"+token;
//						将token保存到Redis中
						jedisClient.set(token, objectToJson);
						//设置session的过期时间 时间单位是秒
						jedisClient.expire("token"+token, 1800);
						map.put("token", token);
					}
					//记录用户登陆时间
					UserCount uc = new UserCount();
					uc.setUserName(user.getUserName());
					userCountService.insertLoginInfo(uc);
					LOGGER.info("Login success");
				} else {
					map.put("status", false);
					map.put("msg", "User name or user password is wrong");
//					LOGGER.info("User name or user password is wrong");
					return JsonUtils.objectToJson(map);
				}
			} else {
				map.put("status", false);
				map.put("msg", "User name or user password is wrong");
//				LOGGER.info("User name or user password is wrong");
				return JsonUtils.objectToJson(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Login exception");
			LOGGER.error("Login exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * authentication(用户认证)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:38:34    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:38:34    
	 * 修改备注： 
	 * &#64;param u
	 * &#64;return
	 * </pre>
	 */

	/*
	 * @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value
	 * = "/user/authentication") public String authentication(User u) { String
	 * objectToJson = ""; List<SysResource> sysResourceList = new
	 * ArrayList<SysResource>(); try { User user = userService.getUserByName(u);
	 * if (null != u.getUserId()) { sysResourceList =
	 * sysResourceService.listSysResource(u.getUserId()); objectToJson =
	 * JsonUtils.objectToJson(sysResourceList); } else { return objectToJson; }
	 * } catch (Exception e) { e.printStackTrace(); } return objectToJson; }
	 */

	/**
	 * <pre>
	 * getPassword(根据用户名 邮箱获取判断用户是否存在)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:38:53    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:38:53    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/getPassword")
	public String getPassword(@RequestBody UserQuery userQuery, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String userEmail = userQuery.getUserEmail();
			if(StringUtils.isBlank(userEmail)){
				map.put("status", false);
				map.put("msg", "UserEmail is null");
				return JsonUtils.objectToJson(map);
			}
			String userName = userQuery.getUserName();
			
			if(StringUtils.isBlank(userName)){
				map.put("status", false);
				map.put("msg", "UserName is null");
				return JsonUtils.objectToJson(map);
			}
			
			
			
//			每次收到用户忘记密码请求后先判断Redis中是否有该用户
				String tokenKey = userName+"_getPasswordCount";
				String loginCount = jedisClient.get(tokenKey);
				if(loginCount==null||loginCount.equals("")){
//			如果没有就存储 userName+"_loginCount"为1，并且存储一分钟
					jedisClient.set(tokenKey, "1");
					//设置session的过期时间 时间单位是秒
					jedisClient.expire(tokenKey, 60*30);
				}else{
//					如果有的话就先取出Redis中的用户登录次数
					int loginCount_ = Integer.parseInt(loginCount);
//			如果次数大于等于5次就禁止用户登录，否则就允许用户登录
					if(loginCount_>=5){
						map.put("status", false);
						map.put("getPasswordStatus", false);
						map.put("msg", "This user getPasswordStatus in frequently");
						return JsonUtils.objectToJson(map);
					}else{
//						如果次数大于等于5次,就将次数加一再保存到Redis中
						jedisClient.set(tokenKey, String.valueOf(loginCount_+1));
						//设置session的过期时间 时间单位是秒
						jedisClient.expire(tokenKey, 60*30);
					}
					
					
				}
			
			
			
			
			LOGGER.info("用户"+userName+"提交修改忘记密码请求");
			List<User> listUser = userService.getPassword(userQuery);
			if (null != listUser && listUser.size() > 0) {
				map.put("status", true);
				map.put("msg", "User exist");
				LOGGER.info("User exist");
				String userId = "";
//				因为查询是用的模糊匹配，所以要进一步匹配
				for(User user:listUser){
					if(user.getUserName()!=null&&user.getUserName().equals(userName)){
						userId = String.valueOf(user.getUserId());
						if(user.getUserEmail()!=null&&user.getUserEmail().equals(userEmail)){
							userEmail = user.getUserEmail();
							
							String uuid = UUID.randomUUID().toString();
							jedisClient.set(uuid, userId);
							jedisClient.expire(uuid, 600);
							// 发送邮件
							//插入邮件任务表
							String emailContent = jedisClient.get("validateEmailContent");
							String ProductIP = jedisClient.get("ProjectIP");
							emailContent = emailContent.replace("$ProjectIP$", ProductIP);
							String msg = String.format(emailContent, userName,uuid);
							EmailTask emailTask = CreateEmailUtils.createEmailTask(jedisClient.get("ddssSender"), msg, userName, userEmail, jedisClient.get("validateEmailSubject"));
							emailTaskService.saveEmail(emailTask);
							
						}else{
							map.put("status", false);
							map.put("msg", "User not exist");
							LOGGER.info("User not exist");
						}
					}
				}
//				CommonsEmail.sendHtmlMail(listUser.get(0).getUserName(), uuid, listUser.get(0).getUserEmail(),listUser.get(0).getUserId().toString());
			} else {
				map.put("status", false);
				map.put("msg", "User not exist");
				LOGGER.info("User not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Check user by email & name has exception");
			LOGGER.error("Check user by email & name has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * checkUserDate(校验用户修改密码是否超过十分钟)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月16日 下午3:17:02    
	 * 修改人：宫森      
	 * 修改时间：2018年3月16日 下午3:17:02    
	 * 修改备注： 
	 * &#64;param millisecond
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST}, value = "/user/checkUserDate")
	public String checkUserDate(String millisecond, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (!StringUtils.isBlank(millisecond)) {
				String string = jedisClient.get(millisecond);
				if (!StringUtils.isBlank(string)) {
					map.put("status", true);
					map.put("userId", string);
					map.put("msg", "Link can use");
				} else {
					map.put("status", false);
					map.put("msg", "Link already out time");
				}
			} else {
				map.put("status", false);
				map.put("msg", "Link already out time");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Check user link date has exception");
			LOGGER.error("Check user link date has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * getUserPwdById(校验旧密码是否正确)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月12日 下午3:48:21    
	 * 修改人：宫森      
	 * 修改时间：2018年3月12日 下午3:48:21    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/oauth/user/getUserPwdById")
	public String getUserPwdById(@RequestBody User user, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
			User u = decryptToken.token2User(is);
			String pwd = "";
			if (!StringUtils.isBlank(user.getUserPwd())) {
				pwd = ActivityStringUtils.createMd5Str(user.getUserPwd(), null);
			}
			User userById = userService.getUserById(u);
			if (!StringUtils.isBlank(userById.getUserPwd()) && pwd.equals(userById.getUserPwd())) {
				map.put("msg", "Old passwrod input right");
				map.put("status", true);
				LOGGER.info("Old passwrod input right");
			} else {
				map.put("msg", "Old passwrod input wrong");
				map.put("status", false);
				LOGGER.info("Old passwrod input wrong");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query password has exception");
			LOGGER.error("Query password has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * updateUser(修改用户、个人中心修改)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:39:25    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:39:25    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST}, value = "/oauth/user/updateUser")
	public String updateUser(@RequestBody User user) {
//		public String updateUser( User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean boo = false;
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
//			如果传入了用户启用状态就根据用户名修改ftp启用状态（启用帐户？，1 =是，0 =否）
			if(user.getEnableaccount()>-1){
//				获取用户名
				String userName = user.getUserName();
				if(StringUtils.isBlank(userName)){
					map.put("status", false);
					map.put("msg", "userName is null");
					return JsonUtils.objectToJson(map);
				}
//				修改用户ftp启用状态
				int i = userMapper.updateWftpEnableaccount(user);
				if(i>0){
					map.put("status", true);
					map.put("msg", "update Wftp Enableaccount Success");
					return JsonUtils.objectToJson(map);
				}
			}
			
			
			
//			User u = decryptToken.token2User(is);
			boo = userService.updateUser(user);
			if (boo) {
				map.put("status", boo);
				map.put("msg", "Success");
				LOGGER.info("Update user was successed");
			} else {
				map.put("status", boo);
				map.put("msg", "Failed");
				LOGGER.info("Update user was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			boo = false;
			map.put("msg", "Failed");
			map.put("status", false);
			LOGGER.error("Update user has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	@RequestMapping(method = { RequestMethod.POST}, value = "/user/updateUserPassword")
	public String updateUserPassword(@RequestBody User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean boo = false;
		try {
			BigDecimal userId = user.getUserId();
			if(null==userId){
				map.put("status", false);
				map.put("msg", "User id is null");
				return JsonUtils.objectToJson(map);
			}
			String userPwd = user.getUserPwd();
			if(StringUtils.isBlank(userPwd)){
				map.put("status", false);
				map.put("msg", "User password is null");
				return JsonUtils.objectToJson(map);
			}
			boo = userService.updateUserPassword(user);
			if (boo) {
				map.put("status", boo);
				map.put("msg", "Success");
				LOGGER.info("Update user was successed");
			} else {
				map.put("status", boo);
				map.put("msg", "Failed");
				LOGGER.info("Update user was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			boo = false;
			map.put("msg", "Failed");
			LOGGER.error("Update user has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}
	/**
	 * <pre>
	 * getCacheByKey(根据key值获取redis缓存里的value值)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月9日 下午2:31:42    
	 * 修改人：宫森      
	 * 修改时间：2018年3月9日 下午2:31:42    
	 * 修改备注： 
	 * &#64;param key
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/getCacheByKey")
	public String getCacheByKey(String key, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("status", false);
		String value = "";
		if (!StringUtils.isBlank(key)) {
			value = jedisClient.get(key);
			if(value.charAt(0) == '"'){
				value = value.replace("\\", "");
				value = value.substring(1, value.length()-1);
			}
//			给单位类别排序
			if(key.equals("workType")){
				JSONArray array = JSONArray.fromObject(value);
				List<Object> list = new ArrayList<Object>();
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				for(Object o:array){
					Map<String,Object> map2 = (Map<String,Object>)o;
					String workType = String.valueOf(map2.get("value"));
					
					switch (workType) {
					case "业务部门":
						map2.put("val", "BusinessDepartments");
						list.set(0, map2);
						break;
					case "科研院所":
						map2.put("val", "ScientificResearchInstitutes");
						list.set(1, map2);
						break;
					case "高等院校":
						map2.put("val", "CollegesAndUniversities");
						list.set(2, map2);
						break;
					case "其他":
						map2.put("val", "Other");
						list.set(3, map2);
						break;

					default:
						break;
					}
				}
				map.put("status", true);
				map.put("msg", true);
				map.put("data", JsonUtils.objectToJson(list));
//				给行业领域排序
			}else if(key.equals("apllicationArea")){
				JSONArray array = JSONArray.fromObject(value);
				List<Object> list = new ArrayList<Object>();
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				list.add(0);
				for(Object o:array){
					Map<String,Object> map2 = (Map<String,Object>)o;
					String workType = String.valueOf(map2.get("value"));
					
					switch (workType) {
					case "国土":
						map2.put("val", "land");
						list.set(0, map2);
						break;
					case "海洋":
						map2.put("val", "ocean");
						list.set(1, map2);
						break;
					case "林业":
						map2.put("val", "Forestry");
						list.set(2, map2);
						break;
					case "交通":
						map2.put("val", "traffic");
						list.set(3, map2);
						break;
					case "环保":
						map2.put("val", "environmentalProtection");
						list.set(4, map2);
						break;
					case "测绘":
						map2.put("val", "Mapping");
						list.set(5, map2);
						break;
					case "减灾":
						map2.put("val", "disasterReduction");
						list.set(6, map2);
						break;
					case "农业":
						map2.put("val", "Agriculture");
						list.set(7, map2);
						break;
					case "气象":
						map2.put("val", "Meteorological");
						list.set(8, map2);
						break;
					case "其他":
						map2.put("val", "Other");
						list.set(9, map2);
						break;
						
					default:
						break;
					}
				}
				map.put("status", true);
				map.put("msg", true);
				map.put("data", JsonUtils.objectToJson(list));
				
			}else{
				map.put("status", true);
				map.put("msg", true);
				map.put("data", value);
			}
			
			
		} else {
			map.put("msg", "key is null");
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * getCacheByKey(根据key值获取redis缓存里的value值)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月9日 下午2:31:42    
	 * 修改人：宫森      
	 * 修改时间：2018年3月9日 下午2:31:42    
	 * 修改备注： 
	 * &#64;param key
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/logout")
	public String logout(String key, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(key)){
			map.put("status", false);
			map.put("msg", Constant.TOKEN_NULL);
			return JsonUtils.objectToJson(map);
		}
		String string = jedisClient.get(key);
		if(StringUtils.isBlank(string)){
			map.put("status", false);
			map.put("msg", "Logout failed");
			return JsonUtils.objectToJson(map);
		} else {
			Long del = jedisClient.del(key);
			if (del.compareTo(new Long(1)) == 0) {
				map.put("status", true);
				map.put("msg", "Logout success");
			} else {
				map.put("status", false);
				map.put("msg", "Logout failed");
			}
		}
		return JsonUtils.objectToJson(map);
	}

	/*@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/listPlace")
	public String listPlace(Place place, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Place> listPlace = placeService.listPlace(place);
			if (null != listPlace && listPlace.size() > 0) {
				map.put("status", true);
				map.put("data", listPlace);
				map.put("msg", "查询省成功");
				LOGGER.info("查询省成功");
			} else {
				map.put("status", false);
				LOGGER.info("查询省失败");
				map.put("msg", "查询省失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "查询省异常");
			LOGGER.info("查询省异常");
		}
		return JsonUtils.objectToJson(map);
	}

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/user/listPlaceById")
	public String listPlaceById(Place place, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Place> listPlaceById = placeService.listPlaceById(place);
			if (null != listPlaceById && listPlaceById.size() > 0) {
				map.put("status", true);
				map.put("data", listPlaceById);
				map.put("msg", "查询市成功");
				LOGGER.info("查询市成功");
			} else {
				map.put("status", false);
				map.put("msg", "查询市失败");
				LOGGER.info("查询市失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "查询市异常");
			LOGGER.info("查询市异常");
		}
		return JsonUtils.objectToJson(map);
	}*/

	/**
	 * <pre>
	 * deleteUserIds(根据id或者ids删除用户)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月10日 下午5:29:49    
	 * 修改人：宫森      
	 * 修改时间：2018年3月10日 下午5:29:49    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = {RequestMethod.POST}, value = "/oauth/user/deleteUserIds")
	public String deleteUserIds(@RequestBody User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
//			User u = decryptToken.token2User(is);
			if(StringUtils.isBlank(user.getIds())){
				map.put("status", false);
				map.put("msg", "Ids is null");
				return JsonUtils.objectToJson(map);
			}
			boolean boo = userService.deleteUserByIds(user);
			if(boo){
				map.put("status", true);
				map.put("msg", "Delete successed");
				LOGGER.info("Delete successed");
			} else {
				map.put("status", false);
				map.put("msg", "Delete failed");
				LOGGER.info("Delete failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Delete has exception");
			LOGGER.error("Delete has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * updateUserPwd(重置用户密码)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月12日 下午2:16:09    
	 * 修改人：宫森      
	 * 修改时间：2018年3月12日 下午2:16:09    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST}, value = "/oauth/user/updateUserPwd")
	public String updateUserPwd(@RequestBody User user, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
//			User u = decryptToken.token2User(is);
			boolean boo = userService.updateUserPwd(user);
			map.put("status", boo);
			if (boo) {
				map.put("msg", "Reset password was successed");
			} else {
				map.put("msg", "Reset password was failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Reset password has exception");
			LOGGER.error("Reset password has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * getUserById(根据用户id查询用户信息)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月12日 下午2:27:38    
	 * 修改人：宫森      
	 * 修改时间：2018年3月12日 下午2:27:38    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST}, value = "/oauth/user/getUserById")
//	public String getUserById( User user) {
		public String getUserById(@RequestBody User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
//			User u = decryptToken.token2User(is);
			User userById = userService.getUserById(user);
			if (null != userById) {
				userById.setUserPwd(null);
				String s = "";
				s += "," + userById.getWorkArea() + "," + userById.getApplicationArea() + "," + userById.getWorkType();
				String ids = s.substring(1, s.length());
				// 设置配置表信息
				List<SysConfig> listSysConfig = sysConfigService.listSysConfigById(ids);
				for (SysConfig sysConfig : listSysConfig) {
					if (userById.getApplicationArea() != null && userById.getApplicationArea().toString().equals(sysConfig.getId().toString())) {// 应用领域
						userById.setApplicationAreaStr(sysConfig.getConfigValue());
					} else if (userById.getWorkType() != null && userById.getWorkType().toString().equals(sysConfig.getId().toString())) { // 单位类别
						userById.setWorkTypeStr(sysConfig.getConfigValue());
					}
				}
				
				List<Place> world = geoService.getPlaceByCode("world");
				for (Place place : world) {
					if(place.getGid().equals(userById.getCountry())){
						userById.setCountryStr(place.getNamechn());
						break;
					}
				}
				
//				如果国家不是中国，省市就取其他省市的字段值
				if(userById.getCountry()!=1){
					userById.setProvinceStr(userById.getOtherProvince());
					userById.setCityStr(userById.getOtherCity());
				}else{
					// setting place
					List<Place> province = geoService.getPlaceByCode("province");
					for (Place place : province) {
						if(place.getGid().equals(userById.getProvince())){
							userById.setProvinceStr(place.getNamechn());
							break;
						}
					}
					List<Place> city = geoService.getPlaceByCode("city");
					for (Place c : city) {
						if(c.getGid().equals(userById.getCity())){
							userById.setCityStr(c.getNamechn());
							break;
						}
					}
				}
				
				// setting roleName
				List<Role> listRole = roleService.listRole(new Role());
				for (Role role : listRole) {
					if (role.getRoleId().equals(userById.getRoleId())) {
						userById.setRoleName(role.getRoleName());
						break;
					}
				}
				map.put("msg", "Query user successed");
				map.put("status", true);
				map.put("data", userById);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Query user has exception");
			LOGGER.error("Query user has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	/**
	 * <pre>
	 * updateUserStatusById(用户审核、批量审核)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月12日 下午2:33:11    
	 * 修改人：宫森      
	 * 修改时间：2018年3月12日 下午2:33:11    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = {RequestMethod.POST}, value = "/oauth/user/updateUserStatusById")
	public String updateUserStatusById(@RequestBody User user) {
//		public String updateUserStatusById( User user) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
			User u = decryptToken.token2User(is);
			user.setFounder(u.getRealName());
			boolean boo = userService.updateUserStatusById(user);
			map.put("status", boo);
			if (boo) {
				map.put("msg", "Audit user successed");
			} else {
				map.put("msg", "Audit user failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", false);
			map.put("msg", "Audit user exception");
			LOGGER.error("Audit user exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return JsonUtils.objectToJson(map);
	}

	
		/** <pre>exportExl(导出用户列表)
		 * 创建人：宫森      
		 * 创建时间：2018年3月22日 下午3:29:24    
		 * 修改人：宫森      
		 * 修改时间：2018年3月22日 下午3:29:24    
		 * 修改备注： 
		 * @param user
		 * @param request
		 * @param response
		 * @return</pre>    
		 */
		 
	@RequestMapping(value = "/oauth/user/exportExl")
	public String exportExl(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		OutputStream out = null;
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_NULL);
				return JsonUtils.objectToJson(map);
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				map.put("status", false);
				map.put("msg", Constant.TOKEN_INVALID);
				return JsonUtils.objectToJson(map);
			}
//			User us = decryptToken.token2User(is);
			String ids = user.getIds();
			if(StringUtils.isBlank(ids)){
				map.put("status", false);
				map.put("msg", "Ids is null");
				return JsonUtils.objectToJson(map);
			}
			if(StringUtils.isBlank(user.getIds())){
				map.put("status", false);
				map.put("msg", "Ids is null");
				return JsonUtils.objectToJson(map);
			}
			List<User> listUser = userService.listUserByIds(user);
			if(listUser==null||listUser.size()<=0){
				map.put("status", true);
				map.put("msg", "No data");
				return JsonUtils.objectToJson(map);
			}
			List<Map<String,String>> userList = new ArrayList<Map<String,String>>();
			for (User u : listUser) {
				//setting field
				Map<String,String> userMap = new HashMap<String, String>();
				userMap.put("user_id", u.getUserId().toString());
				userMap.put("userName", u.getUserName());
				userMap.put("user_sex", u.getUserSex()==0?"男":"女");
				userMap.put("real_name", u.getRealName());
				userMap.put("user_email", u.getUserEmail());
				userMap.put("user_mobile", u.getUserMobile());
				userMap.put("work_unit", u.getWorkUnit());
				userMap.put("status", u.getStatus()==0?"锁定":"正常");
				userMap.put("create_time", u.getCreateTime());
				userList.add(userMap);
			}
			String[] head = {"用户ID","用户名","性别","姓名","邮箱","手机号","工作单位","状态","创建时间"};
			String[] body = {"user_id","userName","user_sex","real_name","user_email","user_mobile","work_unit","status","create_time"};
			String fileName = DateTimeUtils.getNowStrTimeStemp();
			
			//打开输出流
			out = response.getOutputStream();
			// 对浏览器进行设置========================================================
 			// 解决浏览器兼容问题
 			if (request.getHeader("User-Agent").toLowerCase()
 					.indexOf("firefox") > 0) {
 				fileName = new String(fileName.getBytes("GB2312"), "ISO-8859-1");
 			} else {
 				// 对文件名进行编码处理中文问题
 				fileName = java.net.URLEncoder.encode(fileName, "UTF-8");// 处理中文文件名的问题
 				fileName = new String(fileName.getBytes("UTF-8"), "GBK");// 处理中文文件名的问题
 			}
 			response.reset(); // 重点突出
 			response.setCharacterEncoding("UTF-8"); // 重点突出
 			response.setContentType("application/x-msdownload");// 不同类型的文件对应不同的MIME类型
 			//默认为inline方式
			//设置头部信息
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ fileName + ".xls");
 			
 			
			//获取当前国际化
			String language = request.getParameter("language");
			String local = PropertiesUtils.getLocale(language);
			//封装数据导出
			ExportExcelForShopcar.exporsOrderXls("用户信息", userList, out,
					DateTimeUtils.YMDHMS,head,body,local);
			out.flush();
			out.close();
			map.put("status", true);
			map.put("msg", "");
			return JsonUtils.objectToJson(map);
		} catch (Exception e) {
			map.put("status", false);
			map.put("msg", e.getMessage());
			LOGGER.error("Export user exception, caused by "+e.getMessage(),Constant.SYSTEM);
			return JsonUtils.objectToJson(map);
		}finally {
			try {
				if(null!=out){
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Export user exception, caused by "+e.getMessage(),Constant.SYSTEM);
			}
		}
	}
	
	/**
	 * 根据用户名获取用户信息
	 * @param userName
	 * @return
	 */
	@RequestMapping(value = "/user/getUserByUserName")
	public String getUserByUserName(String userName){
		if(StringUtils.isBlank(userName)){
			return null;
		}
		try {
			User user = new User();
			user.setUserName(userName);
			user = userService.getUserByName(user);
			return JsonUtils.objectToJson(user);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 邮件查询用户邮箱列表
	 * @param userQuery
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/userEmailList")
	@ResponseBody
	public String userEmailList(UserQuery userQuery, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<User> listUser = this.userService.userEmailList(userQuery);
			map.put("data", listUser);
			map.put("status", true);
			map.put("msg", "User list query successed");
			LOGGER.info("User list query successed");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("User list query exception");
			map.put("status", false);
			map.put("msg", "User list query exception");
		}
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 统计分析首先查询用户单位 去重
	 * @param userQuery
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/userWorkUnit")
	@ResponseBody
	public RestfulJSON userWorkUnit(UserQuery userQuery, HttpServletRequest request) {
		RestfulJSON json = new RestfulJSON();
		try {
			List<User> listUser = this.userService.userWorkUnit(userQuery);
			json.setData(listUser);
			json.setStatus(true);
			json.setMsg("User list query successed");
			LOGGER.info("User list query successed");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("User list query exception");
			json.setStatus(false);
			json.setMsg("User list query exception");
		}
		return json;
	}
	
	/**
	 * 统计分析根据查询用户单位查询
	 * @param userQuery
	 * @param request
	 * @return
	 */
	@RequestMapping("/user/userByWorkUnit")
	@ResponseBody
	public RestfulJSON userByWorkUnit(@RequestBody UserQuery userQuery, HttpServletRequest request) {
		RestfulJSON json = new RestfulJSON();
		try {
			String ids = userQuery.getIds();
			if(StringUtils.isBlank(ids)){
				json.setStatus(false);
				json.setMsg("User work unit is null");
				return json;
			}
			List<User> listUser = this.userService.userByWorkUnit(userQuery);
			json.setData(listUser);
			json.setStatus(true);
			json.setMsg("User list query successed");
			LOGGER.info("User list query successed");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info("User list query exception");
			json.setStatus(false);
			json.setMsg("User list query exception");
		}
		return json;
	}
	
	/**
	 * <pre>
	 * updateUser(修改用户、个人中心修改)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月8日 上午9:39:25    
	 * 修改人：宫森      
	 * 修改时间：2018年3月8日 上午9:39:25    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/oauth/user/updateUserById")
	public RestfulJSON updateUserById(@RequestBody User user) {
		RestfulJSON json = new RestfulJSON();
		boolean boo = false;
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				json.setStatus(false);
				json.setMsg(Constant.TOKEN_NULL);
				return json;
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				json.setStatus(false);
				json.setMsg(Constant.TOKEN_INVALID);
				return json;
			}
			User u = decryptToken.token2User(is);
			user.setUserId(u.getUserId());
			boo = userService.updateUser(user);
			if (boo) {
				LOGGER.info("Update user was successed");
				json.setStatus(boo);
				json.setMsg("Success");
			} else {
				LOGGER.info("Update user was failed");
				json.setStatus(boo);
				json.setMsg("Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setStatus(false);
			json.setMsg("Update user has exception");
			LOGGER.error("Update user has exception has exception, caused by "+e.getMessage(),Constant.SYSTEM);
		}
		return json;
	}
	/**
	 * <pre>
	 * getUserById(token 个人中心)   
	 * 创建人：宫森      
	 * 创建时间：2018年3月12日 下午2:27:38    
	 * 修改人：宫森      
	 * 修改时间：2018年3月12日 下午2:27:38    
	 * 修改备注： 
	 * &#64;param user
	 * &#64;return
	 * </pre>
	 */

	@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = "/oauth/user/getUserInfoById")
	public RestfulJSON getUserInfoById(@RequestBody User user, HttpServletRequest request) {
		RestfulJSON json = new RestfulJSON();
		try {
			String token = user.getToken();
			if(StringUtils.isBlank(token)){
				json.setStatus(false);
				json.setMsg(Constant.TOKEN_NULL);
				return json;
			}
			String is = jedisClient.get(token);
			if(StringUtils.isBlank(is)){
				json.setStatus(false);
				json.setMsg(Constant.TOKEN_INVALID);
				return json;
			}
			User u = decryptToken.token2User(is);
			User userById = userService.getUserById(u);
			if (null != userById) {
				userById.setUserPwd(null);
				String s = "";
				s += "," + userById.getApplicationArea() + "," + userById.getWorkType();
				String ids = s.substring(1, s.length());
				// 设置配置表信息
				List<SysConfig> listSysConfig = sysConfigService.listSysConfigById(ids);
				for (SysConfig sysConfig : listSysConfig) {
					/*if (userById.getWorkArea().toString().equals(sysConfig.getId().toString())) {// 用户行业
						userById.setWorkAreaStr(sysConfig.getConfigValue());
					} else */if (userById.getApplicationArea().toString().equals(sysConfig.getId().toString())) {// 应用领域
						userById.setApplicationAreaStr(sysConfig.getConfigValue());
					} else if (userById.getWorkType().toString().equals(sysConfig.getId().toString())) { // 单位类别
						userById.setWorkTypeStr(sysConfig.getConfigValue());
					}
				}
				// setting place
				List<Place> province = geoService.getPlaceByCode("province");
				for (Place place : province) {
					if(place.getGid().equals(userById.getProvince())){
						userById.setProvinceStr(place.getNamechn());
						break;
					}
				}
				List<Place> city = geoService.getPlaceByCode("city");
				for (Place c : city) {
					if(c.getGid().equals(userById.getCity())){
						userById.setCityStr(c.getNamechn());
						break;
					}
				}
				// setting roleName
				List<Role> listRole = roleService.listRole(new Role());
				for (Role role : listRole) {
					if (role.getRoleId().equals(userById.getRoleId())) {
						userById.setRoleName(role.getRoleName());
						break;
					}
				}
				json.setStatus(true);
				json.setMsg("Query user successed");
				json.setData(userById);
			}
		} catch (Exception e) {
			e.printStackTrace();
			json.setStatus(false);
			json.setMsg("Query user faile");
		}
		return json;
	}
	public static boolean isBlank(Map<String, Object> map,String name, String value){
		if(StringUtils.isBlank(value)){
			map.put("status", false);
			map.put("msg", name + " is null");
			return false;
		}
		return true;
	}
	public static boolean isNull(Map<String, Object> map,String name, Integer value){
		if(null == value){
			map.put("status", false);
			map.put("msg", name + " is null");
			return false;
		}
		return true;
	}
	public static boolean isNull(Map<String, Object> map,String name, String value){
		if(null == value){
			map.put("status", false);
			map.put("msg", name + " is null");
			return false;
		}
		return true;
	}
	
	@RequestMapping(method = { RequestMethod.POST,RequestMethod.GET }, value = "/oauth/user/getWorkTypeAndUserInfo")
	public RestfulJSON getWorkTypeAndUserInfo(){
		RestfulJSON json = new RestfulJSON();
		try {
			List<Map<Object, Object>> list_user = userService.getWorkTypeAndUserInfo();
			json.setData(list_user);
			json.setStatus(true);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			json.setStatus(false);
		}
		return json;
	}
	
	
	
//	public static void main(String[] args) {
//		
//		RestTemplate restTemplate1 = new RestTemplate();
//		
//		String result = restTemplate1.getForObject("http://172.16.25.27:8081/netGate/oauth/map_query_sensor?pageSize=1000&timeType=image_start_time&startTime=2018-12-05 15:59:38&endTime=2019-03-05 15:59:38&satellite=HY-1C&sensor=COCTS&outProductLevel=L1&ploygon=POLYGON ((59.4141 60.9304,71.7187 60.9304,71.7187 66.5133,59.4141 66.5133,59.4141 60.9304))&regionalType=2&token=token9ae0f3cf83cF4314dd9020a7a5b18a8f", String.class);
//		
//		System.out.println("结果是："+result);
//	}
	
}
