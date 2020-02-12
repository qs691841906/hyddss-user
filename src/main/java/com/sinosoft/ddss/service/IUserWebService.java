package com.sinosoft.ddss.service;

import java.util.List;

import javax.jws.WebService;

import com.sinosoft.ddss.common.entity.UserTask;

@WebService(name = "userWebService", // 暴露服务名称
		targetNamespace = "http://service.ddss.sinosoft.com/"// 命名空间,一般是接口的包名倒序
)
public interface IUserWebService {
	/**
	 * 与共享 用户注册
	 * 
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_USERREGIST(String xmlInfo);

	/**
	 * 与共享 配置信息获取
	 * 
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_CONFIGINFO(String xmlInfo);
	
	/**
	 * 与共享 用户登录验证
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_USERLOGIN(String xmlInfo);
	
	/**
	 * 与共享 用户信息查询
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_USERINFO(String xmlInfo);
	
	/**
	 * 与共享 数据集信息检索与反馈
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_DATASETRETRIEVAL(String xmlInfo);
	
	/**
	 * 与21世纪 订单用户增加
	 * @param userTask
	 * @return
	 */
	String DDSS_ORDERUSR_ADD(List<UserTask> userTask);
	/**
	 * 与21世纪 订单用户修改
	 * @param xmlInfo
	 * @return
	 */
	String DDSS_ORDERUSR_MOD(List<UserTask> userTask);
	/**
	 * 与21世纪 订单用户删除
	 * @param xmlInfo
	 * @return
	 */
	String DDSS_ORDERUSR_DEL(List<UserTask> userTask);
	/**
	 * 与共享 忘记密码
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_FORGOTPSW(String xmlInfo);
	
	/**
	 * 与共享 设置新密码
	 * @param xmlInfo
	 * @return
	 */
	String SSP_DDSS_SETNEWPSW(String xmlInfo);
}
