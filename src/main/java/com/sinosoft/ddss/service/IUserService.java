package com.sinosoft.ddss.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.UserQuery;

/**
 *
 * 用户业务逻辑接口类
 */
public interface IUserService {

    
    	/** <pre>listUser(查询用户列表)   
    	 * 创建人：宫森      
    	 * 创建时间：2018年3月13日 下午7:44:36    
    	 * 修改人：宫森      
    	 * 修改时间：2018年3月13日 下午7:44:36    
    	 * 修改备注： 
    	 * @param user
    	 * @return
    	 * @throws Exception</pre>    
    	 */
    	 
    List<User> listUser(UserQuery userQuery) throws Exception;
    
    	/** <pre>countUser(查询用户数量)   
    	 * 创建人：宫森      
    	 * 创建时间：2018年3月13日 下午7:44:57    
    	 * 修改人：宫森      
    	 * 修改时间：2018年3月13日 下午7:44:57    
    	 * 修改备注： 
    	 * @param user
    	 * @return
    	 * @throws Exception</pre>    
    	 */
    	 
    Integer countUser(UserQuery userQuery) throws Exception;
	
		/** <pre>insertUser(增加用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:45:13    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:45:13    
		 * 修改备注： 
		 * @param user
		 * @param request
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean insertUser(User user, HttpServletRequest request) throws Exception;

	
		/** <pre>checkUserName(根据用户名查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:45:27    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:45:27    
		 * 修改备注： 
		 * @param name
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean checkUserName(String name) throws Exception;

	
		/** <pre>getUserByName(根据用户名查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:45:44    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:45:44    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	User getUserByName(User user) throws Exception;
	
		/** <pre>updateUser(修改用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:46:08    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:46:08    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean updateUser(User user) throws Exception;
	
		/** <pre>deleteUserByIds(根据id删除用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:46:16    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:46:16    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean deleteUserByIds(User user) throws Exception;
	
		/** <pre>updateUserPwd(重置用户密码)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:46:28    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:46:28    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean updateUserPwd(User user) throws Exception;
	
		/** <pre>getUserById(根据用户id查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:46:40    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:46:40    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	User getUserById(User user) throws Exception;
	
		/** <pre>updateUserStatusById(更新用户审核状态)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月13日 下午7:46:57    
		 * 修改人：宫森      
		 * 修改时间：2018年3月13日 下午7:46:57    
		 * 修改备注： 
		 * @param user
		 * @return
		 * @throws Exception</pre>    
		 */
		 
	boolean updateUserStatusById(User user) throws Exception;

	List<User> getPassword(UserQuery userQuery)throws Exception;

	List<User> listUserByIds(User user) throws Exception;

	List<User> userEmailList(UserQuery userQuery) throws Exception;
	/** <pre>listUser(查询所有报警信息)   
	 * 创建人：宫森      
	 * 创建时间：2018年4月28日 下午7:44:36    
	 * 修改人：宫森      
	 * 修改时间：2018年4月28日 下午7:44:36    
	 * 修改备注： 
	 * @param user
	 * @return
	 * @throws Exception</pre>    
	 */
	 
	List<User> QueryCallPolice(Integer delay) throws Exception;

	List<User> userWorkUnit(UserQuery userQuery) throws Exception;

	List<User> userByWorkUnit(UserQuery userQuery) throws Exception;

	
		/** <pre>updateUserPassword(修改用户密码)   
		 * Author：sen_kung     
		 * Create date：2018年6月25日 下午2:39:35    
		 * Author：sen_kung      
		 * Update date：2018年6月25日 下午2:39:35    
		 * Description： 
		 * @param user
		 * @return</pre>    
		 */
		 
	boolean updateUserPassword(User user);
	
	/**
	 * 获取单位类别和对应的用户信息
	 * @return
	 */
	List<Map<Object,Object>> getWorkTypeAndUserInfo();

}
