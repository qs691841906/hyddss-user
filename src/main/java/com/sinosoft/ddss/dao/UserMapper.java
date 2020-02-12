package com.sinosoft.ddss.dao;

import com.sinosoft.ddss.common.entity.User;
import com.sinosoft.ddss.common.entity.query.UserQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(BigDecimal userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    Integer insertSelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    User selectByPrimaryKey(BigDecimal userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddss_sys_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(User record);
    /**
     * <pre>listUser(查询用户列表)   
    	 * 创建人：宫森      
    	 * 创建时间：2018年3月14日 上午10:17:21    
    	 * 修改人：宫森      
    	 * 修改时间：2018年3月14日 上午10:17:21    
    	 * 修改备注： 
    	 * @param user
    	 * @return</pre>
     */
    List<User> listUser(UserQuery userQuery);
    
    
    /**
     * 根据用户名和邮箱查询用户
     * @param userQuery
     * @return
     */
    List<User> selectUserByUserNameEmail(UserQuery userQuery);
	
		/** <pre>checkUserName(根据用户名查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:25    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:25    
		 * 修改备注： 
		 * @param name
		 * @return</pre>    
		 */
		 
	User checkUserName(String name);

	
		/** <pre>getUserByName(根据用户名查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:28    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:28    
		 * 修改备注： 
		 * @param user
		 * @return</pre>    
		 */
		 
	User getUserByName(User user);

	
		/** <pre>countUser(查询用户数量)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:30    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:30    
		 * 修改备注： 
		 * @param user
		 * @return</pre>    
		 */
		 
	Integer countUser(UserQuery userQuery);

	
		/** <pre>deleteUserByIds(删除用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:32    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:32    
		 * 修改备注： 
		 * @param list
		 * @return</pre>    
		 */
		 
	int deleteUserByIds(List<BigDecimal> list);

	
		/** <pre>updateUserPwd(重置用户密码)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:34    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:34    
		 * 修改备注： 
		 * @param user
		 * @return</pre>    
		 */
		 
	int updateUserPwd(User user);

	
		/** <pre>getUserById(根据id查询用户)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:36    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:36    
		 * 修改备注： 
		 * @param user
		 * @return</pre>    
		 */
		 
	User getUserById(User user);

	
		/** <pre>updateUserStatusById(更新用户审核状态)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月14日 上午10:17:40    
		 * 修改人：宫森      
		 * 修改时间：2018年3月14日 上午10:17:40    
		 * 修改备注： 
		 * @param list
		 * @return</pre>    
		 */
		 
	int updateUserStatusById(User user);

	
		/** <pre>listlistUserByIds(根据用户ids查询用户列表)   
		 * 创建人：宫森      
		 * 创建时间：2018年3月22日 上午10:00:42    
		 * 修改人：宫森      
		 * 修改时间：2018年3月22日 上午10:00:42    
		 * 修改备注： 
		 * @param user
		 * @return</pre>    
		 */
		 
	List<User> listlistUserByIds(User user);
	
    /**
     * <pre>listUser(查询用户列表)   
    	 * 创建人：杨金地      
    	 * 创建时间：2018年4月28日 上午10:17:21    
    	 * 修改人：杨金地   
    	 * 修改时间：2018年4月28日 上午10:17:21    
    	 * 修改备注： 
    	 * @param user
    	 * @return</pre>
     */
    List<User> QueryCallPolice(Integer delay);

	/**
	 * 查询所有的work unit
	 * @param userQuery
	 * @return
	 */
	List<User> unitListUser(UserQuery userQuery);

	List<User> userByWorkUnit(UserQuery userQuery);

	BigDecimal getUsetSeq();
//	查询用户的用户类别和用户信息
	List<Map<Object, Object>> getWorkTypeAndUserInfo();
	
	
	/**
	 * 根据用户名查询wftp用户
	 * @param name
	 * @return
	 */
	List<Map<String,String>> selectWftpByUserName(String name);
	
	/**
	 * 修改wftp用户启用状态
	 * @param map
	 * @return
	 */
	int updateWftpEnableaccount(User user);
	
	/**
	 * 修改wftp用户目录
	 * @param map
	 * @return
	 */
	int updateWftpDirpath(Map<String,String> map);
	
	
}