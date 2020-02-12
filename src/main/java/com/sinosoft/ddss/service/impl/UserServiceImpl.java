package com.sinosoft.ddss.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.EmailTask;
import com.sinosoft.ddss.common.entity.Role;
import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.UserRole;
import com.sinosoft.ddss.common.entity.UserTask;
import com.sinosoft.ddss.common.entity.query.UserQuery;
import com.sinosoft.ddss.common.util.ActivityStringUtils;
import com.sinosoft.ddss.common.util.DateTimeUtils;
import com.sinosoft.ddss.dao.EmailTaskMapper;
import com.sinosoft.ddss.dao.RoleMapper;
import com.sinosoft.ddss.dao.SysConfigMapper;
import com.sinosoft.ddss.dao.UserMapper;
import com.sinosoft.ddss.dao.UserRoleMapper;
import com.sinosoft.ddss.dao.UserTaskMapper;
import com.sinosoft.ddss.dataDao.PlaceMapper;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.IUserService;
import com.sinosoft.ddss.utils.Constants;
import com.sinosoft.ddss.utils.CreateEmailUtils;
/**
 *
 * 用户业务逻辑实现类
 */
@Service
public class UserServiceImpl implements IUserService {
	private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
	private JedisClient jedisClient;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private PlaceMapper placeMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserTaskMapper userTaskMapper;
    @Autowired
    private EmailTaskMapper emailTaskMapper;
	
		 
	@Override
	public List<User> listUser(UserQuery userQuery) throws Exception {
		if(null!=userQuery.getUserName()&&userQuery.getUserName().equals("null")){
			userQuery.setUserName(null);
		} 
		if(null!=userQuery.getUserEmail()&&userQuery.getUserEmail().equals("null")){
			userQuery.setUserEmail(null);
		} 
		if(null!=userQuery.getWorkUnit()&&userQuery.getWorkUnit().equals("null")){
			userQuery.setWorkUnit(null);
		} 
		if(null!=userQuery.getRoleId()&&userQuery.getRoleId().equals("null")){
			userQuery.setRoleId(null);
		}
		List<User> listUser = userMapper.listUser(userQuery);
		//先查出每个用户所对应的角色 再把角色名给 用户
		Role role = new Role();
		List<Role> listRole = roleMapper.listRole(role);
		// 预警的把预警字段设置为true
		boolean callPolice = userQuery.isCallPolice();
		if(callPolice){
			for(User user : listUser){
				for(Role ro:listRole){
					if(user.getRoleId().equals(ro.getRoleId())){
						user.setRoleName(ro.getRoleName());
						user.setCallPolice(true);
					}
				}
			}
		}else{
			for(User user : listUser){
				for(Role ro:listRole){
					if(user.getRoleId().equals(ro.getRoleId())){
						user.setRoleName(ro.getRoleName());
					}
				}
			}
		}
		return listUser;
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#countUser(com.sinosoft.ddss.common.entity.User)    
		 * 查询用户数量
		 */
		 
	@Override
	public Integer countUser(UserQuery userQuery) throws Exception {
		
		return userMapper.countUser(userQuery);
	}
	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#insertUser(com.sinosoft.ddss.common.entity.User, javax.servlet.http.HttpServletRequest)  
		 * 增加用户  
		 */
		 
	@Override
	public boolean insertUser(User user, HttpServletRequest request) {
		user.setStatus(0); //状态(0:锁定 1:正常 2:删除)
		// 查看是否需要自动审核
		String userAutomaticReview = jedisClient.get("userAutomaticReview");
		user.setAuditStatus(userAutomaticReview.equals("1") ? 1 : 0);//审核状态(0：待审核，1：成功，2：失败)
		boolean result = false;
		user.setUserPwd(ActivityStringUtils.createMd5Str(user.getUserPwd(), null));
		//增加用户
		BigDecimal userId = userMapper.getUsetSeq();
		user.setUserId(userId);
		Integer i1 = userMapper.insertSelective(user);
//		BigDecimal userId = user.getUserId();
		if(i1.compareTo(new Integer(1)) == 0){
			//保存用户角色
			UserRole userrole= new UserRole();
			userrole.setRoleId(BigDecimal.valueOf(1));//设置默认角色id
			userrole.setUserId(userId);//设置新增用户返回id
			Integer i2 = userRoleMapper.insertSelective(userrole);
			if(i2.compareTo(new Integer(1)) == 0){
				result = true;
				//增加到用户任务
				UserTask record = new UserTask();
				record.setUserid(userId);
				record.setUserName(user.getUserName());
				record.setRealName(user.getRealName());
				record.setWorkArea(user.getWorkArea());
				record.setWorkType(user.getWorkType());
				record.setWorkUnit(user.getWorkUnit());
				record.setApplicationArea(user.getApplicationArea());
//				record.setIsReport(user.getIsReport());
				record.setSend(1);//重新发送
				
//				创建用户
				String userName = user.getUserName();
				String url = "/DDS_CACHE/"+userName;
				File file = new File(url);
				if(!file.exists()){
					file.mkdirs();
				}
				
				userTaskMapper.insertSelective(record);
				//增加到邮件任务表
				String msg = String.format(jedisClient.get("registerEmailContent"), user.getRealName());
				EmailTask emailTask = CreateEmailUtils.createEmailTask(jedisClient.get("EmailSendAddress"), msg, user.getUserName(), user.getUserEmail(), jedisClient.get("registerEmailSubject"));
				emailTaskMapper.insertSelective(emailTask);
				//发送待审核邮件给管理员
				String msg_ = String.format(jedisClient.get("auditUserEmailContent"), user.getWorkUnit(),user.getRealName(),DateTimeUtils.DateToString(new Date(), "yyyy/MM/dd HH:mm:ss"),user.getUserName());
				EmailTask emailTask_ = CreateEmailUtils.createEmailTask(jedisClient.get("EmailSendAddress"), msg_, user.getUserName(), jedisClient.get("AdministratorMail"), "用户注册等待审核");
				emailTaskMapper.insertSelective(emailTask_);
			}
			return result;
		}
		return result;
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#checkUserName(java.lang.String)    
		 * 根据用户名查询用户
		 */
		 
	@Override
	public boolean checkUserName(String name) throws Exception {
		boolean boo = false;
		User user = userMapper.checkUserName(name);
		List<Map<String,String>> list_map = userMapper.selectWftpByUserName(name);
		if(null!=user&&null!=list_map){
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#getUserByName(com.sinosoft.ddss.common.entity.User)    
		 * 根据用户名查询用户
		 */
		 
	@Override
	public User getUserByName(User user) {
		user.setUserPwd(ActivityStringUtils.createMd5Str(user.getUserPwd(), null));
		return userMapper.getUserByName(user);
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#updateUser(com.sinosoft.ddss.common.entity.User)    
		 * 修改用户
		 */
		 
	@Override
	public boolean updateUser(User user) throws Exception {
		boolean boo = false;
		if(!StringUtils.isBlank(user.getUserPwd())){
			user.setUserPwd(ActivityStringUtils.createMd5Str(user.getUserPwd(), null));
		}
		if(null==user.getRoleId()){
			Integer i = userMapper.updateByPrimaryKeySelective(user);
			
//			if(i>0){
//				
//				try {
//	//				修改用户的wftp启用状态
//					Map<String,String> map = new HashMap<String,String>();
//					map.put("enableaccount", String.valueOf(user.getWftpStatus()));
//					map.put("userName", user.getUserName());
//					int j = userMapper.updateWftpEnableaccount(map);
//	//				如果用户启用，修改用户的wftp目录
//					if(j>0&&user.getWftpStatus()==1){
//						String wftp_path = user.getWftpPath();
//						Map<String,String> map_wftp = new HashMap<String,String>();
//						map_wftp.put("dirpath", wftp_path);
//						map_wftp.put("userName", user.getUserName());
//						int k = userMapper.updateWftpDirpath(map_wftp);
//					}
//				} catch (Exception e) {
//					LOGGER.error(e.getMessage());
//				}
//				
//			}
			//修改用户任务
			UserTask record = new UserTask();
			record.setUserid(user.getUserId());
			record.setRealName(user.getRealName());
			record.setWorkArea(user.getWorkArea());
			record.setWorkType(user.getWorkType());
			record.setWorkUnit(user.getWorkUnit());
			record.setApplicationArea(user.getApplicationArea());
			record.setType(2);//2=修改
			record.setSend(1);
//			record.setIsReport(user.getIsReport());
			userTaskMapper.updateByPrimaryKeySelective(record);
			if(i.compareTo(new Integer(1)) == 0){
				boo = true;
			} else {
				boo = false;
			}
		} else {
			Integer i = userMapper.updateByPrimaryKeySelective(user);
			//修改用户任务
			UserTask ut = new UserTask();
			ut.setUserid(user.getUserId());
			ut.setRealName(user.getRealName());
			ut.setWorkArea(user.getWorkArea());
			ut.setWorkType(user.getWorkType());
			ut.setWorkUnit(user.getWorkUnit());
			ut.setApplicationArea(user.getApplicationArea());
			ut.setType(2);//2=修改
			ut.setIsReport(user.getIsReport());
			ut.setSend(1);//重新发送
			userTaskMapper.updateByPrimaryKeySelective(ut);	
			if(i.compareTo(new Integer(1)) == 0){
				Integer deleteByUserId = userRoleMapper.deleteByUserId(user.getUserId());
					UserRole record = new UserRole();
					record.setUserId(user.getUserId());
					record.setRoleId(user.getRoleId());
					Integer insert = userRoleMapper.insert(record);
					if(insert > 0){
						boo = true;
					}
			} else {
				boo = false;
			}
		}
		return boo;
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#deleteUserByIds(com.sinosoft.ddss.common.entity.User)    
		 * 根据id删除用户
		 */
		 
	@Override
	public boolean deleteUserByIds(User user) throws Exception {
		boolean boo = false;
		String ids = user.getIds();
		if(!StringUtils.isBlank(ids)){
			String[] split = ids.split(",");
			List<BigDecimal> lists = new ArrayList<BigDecimal>();
			for (String id : split) {
				if(! StringUtils.isBlank(id)){
					Long valueOf = Long.valueOf(id);
					lists.add(BigDecimal.valueOf(valueOf));
				}
			}
			Integer i = userMapper.deleteUserByIds(lists);
			if(i > 0){
				boo = true;
				userTaskMapper.deleteByIds(ids);
			} else {
				boo = false;
			}
		}
		return boo;
	}

	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#updateUserPwd(com.sinosoft.ddss.common.entity.User)    
		 * 重置用户密码
		 */
		 
	@Override
	public boolean updateUserPwd(User user) throws Exception {
		boolean boo = false;
		//String value = jedisClient.get("resetPwd");
		String value = getnewPsw();
		user.setUserPwd(ActivityStringUtils.createMd5Str(value, null));
		Integer i = userMapper.updateUserPwd(user);
		if(i > 0){
			//插入邮件任务表
			String msg = String.format(jedisClient.get("resetPasswordContent"), user.getUserName(),value);
			EmailTask emailTask = CreateEmailUtils.createEmailTask(jedisClient.get("ddssSender"), msg, user.getUserName(), user.getUserEmail(), jedisClient.get("resetPasswordSubject"));
			emailTaskMapper.insertSelective(emailTask);
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}
	private String getnewPsw(){
		String radom = new Random().nextInt(999)+"";//新的六位数密码
		int radom1 = new Random().nextInt(26)+97;//新的六位数密码
		char  a1 = (char)radom1;
		String as1 = a1+"";
		int radom2 = new Random().nextInt(26)+97;//新的六位数密码
		char  a2 = (char)radom2;
		String as2 = a2+"";
		int radom3 = new Random().nextInt(26)+97;//新的六位数密码
		char  a3 = (char)radom3;
		String as3 = a3+"";
		String newpsw = as1+as2+as3+radom+"_";
		return newpsw;
	}
	
		 /* (non-Javadoc)    
		 * @see com.sinosoft.ddss.service.IUserService#getUserById(com.sinosoft.ddss.common.entity.User)    
		 * 根据用户id查询用户
		 */
		 
	@Override
	public User getUserById(User user) throws Exception {
		User userById = userMapper.getUserById(user);
		if(userById==null){
			user.setAuditStatus(2);
			userById = userMapper.getUserById(user);
		}
		return userById;
	}

	
		 /* (non-Javadoc)    
		  *
		 * @see com.sinosoft.ddss.service.IUserService#updateUserStatusById(com.sinosoft.ddss.common.entity.User)    
		 *  更新用户审核状态
		 */
		 
	@Override
	public boolean updateUserStatusById(User user) throws Exception {
		boolean boo = false;
		String ids = user.getIds();
		if(!StringUtils.isBlank(ids)){
			/*String[] split = ids.split(",");
			List<BigDecimal> lists = new ArrayList<BigDecimal>();
			for (String id : split) {
				if(! StringUtils.isBlank(id)){
					Long valueOf = Long.valueOf(id);
					lists.add(BigDecimal.valueOf(valueOf));
				}
			}*/
//			如果审核通过就将用户的锁定状态解除
			if(user.getAuditStatus()==1){
				user.setStatus(1);
			}
			Integer i = userMapper.updateUserStatusById(user);
			if(i>0){
				boo = true;
			} else {
				boo = false;
			}
			List<User> listlistUserByIds = userMapper.listlistUserByIds(user);
			if(null!=listlistUserByIds&&listlistUserByIds.size()>0){
				//增加到邮件任务表
				for (User user2 : listlistUserByIds) {
					EmailTask emailTask = new EmailTask();
					if(user.getAuditStatus()==1){
						String msg = String.format(jedisClient.get("auditAccountContentPass"), user2.getRealName(),user2.getUserName());
						emailTask = CreateEmailUtils.createEmailTask(jedisClient.get("ddssSender"), msg, user2.getUserName(), user2.getUserEmail(), jedisClient.get("auditAccountSubject"));
					} else if(user.getAuditStatus()==2){
						String msg = String.format(jedisClient.get("auditAccountContentNoPass"), user2.getRealName(), user2.getUserName(),user2.getAuditFailReason());
						emailTask = CreateEmailUtils.createEmailTask(jedisClient.get("ddssSender"), msg, user2.getUserName(), user2.getUserEmail(), jedisClient.get("auditAccountSubject"));
//						将审核不通过的用户删除掉
						userMapper.deleteByPrimaryKey(user2.getUserId());
					
					}
					emailTaskMapper.insertSelective(emailTask);
				}
			}
		}
		return boo;
			
	}


	@Override
	public List<User> getPassword(UserQuery userQuery) throws Exception {
			return userMapper.selectUserByUserNameEmail(userQuery);
			
	}


	@Override
	public List<User> listUserByIds(User user) throws Exception {
		List<User> listUser = userMapper.listlistUserByIds(user);
		return listUser;
	}


	@Override
	public List<User> userEmailList(UserQuery userQuery) throws Exception {
		userQuery.setStartNum(-1);
		userQuery.setPageSize(-1);
		List<User> listUser = userMapper.listUser(userQuery);
		return listUser;
	}
	
	
	 /* (non-Javadoc)    
	 * @see com.sinosoft.ddss.service.IUserService#listUser(com.sinosoft.ddss.common.entity.User)    
	 * 查询用户列表
	 */
	 
@Override
public List<User> QueryCallPolice(Integer delay) throws Exception {

	List<User> listUser = userMapper.QueryCallPolice(delay);;
	return listUser;
}
	@Override
	public List<User> userWorkUnit(UserQuery userQuery) throws Exception {
		userQuery.setStartNum(-1);
		userQuery.setPageSize(-1);
		List<User> listUser = userMapper.unitListUser(userQuery);
		return listUser;
	}


	@Override
	public List<User> userByWorkUnit(UserQuery userQuery) throws Exception {
		List<User> listUser = userMapper.userByWorkUnit(userQuery);
		return listUser;
	}
	
	@Override
	public boolean updateUserPassword(User user) {
		boolean boo = false;
//		当修改密码时同时将明文密码修改
		if(null!=user.getUserPwd()){
			user.setUserPwd(ActivityStringUtils.createMd5Str(user.getUserPwd(), null));
			user.setPassword(user.getUserPwd());
		}else{
//			登录时只记录明文密码
			user.setPassword(user.getPassword());
		}
		Integer i = userMapper.updateByPrimaryKeySelective(user);
		if(i.compareTo(new Integer(1)) == 0){
			boo = true;
		} else {
			boo = false;
		}
		return boo;
	}


	@Override
	public List<Map<Object, Object>> getWorkTypeAndUserInfo() {
		List<Map<Object, Object>> list_user = userMapper.getWorkTypeAndUserInfo();
		
		List<String> userType_list = new ArrayList<String>();
		for(Map<Object, Object> map_user:list_user){
			String work_type = String.valueOf(map_user.get("work_type"));
			userType_list.add(work_type);
		}
		Stream<String> userType_stream = userType_list.stream().distinct();
		List<String> userType = userType_stream.collect(Collectors.toList()); 
		List<Map<Object, Object>> list = new ArrayList<>();
		for(String user_type:userType){
			Map<Object, Object> map_user = new HashMap<Object, Object>();
			map_user.put("work_type", user_type);
			List<Object> list_userInfo = new ArrayList<Object>();
			for(Map<Object, Object> map_userinfo:list_user){
				if(String.valueOf(map_userinfo.get("work_type")).equals(user_type)){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("user_name", map_userinfo.get("user_name"));
					map.put("work_type", map_userinfo.get("real_name"));
					map.put("user_email", map_userinfo.get("user_email"));
					map.put("work_id", map_userinfo.get("user_id"));
					list_userInfo.add(map);
					map_user.put("work_id", map_userinfo.get("work_id")+"w");
				}
			}
			map_user.put("list_userInfo", list_userInfo);
			list.add(map_user);
		}
		return list;
	}

}
