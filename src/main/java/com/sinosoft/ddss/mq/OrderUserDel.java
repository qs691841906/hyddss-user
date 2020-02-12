package com.sinosoft.ddss.mq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinosoft.ddss.common.constant.Constant;
import com.sinosoft.ddss.common.entity.UserTask;
import com.sinosoft.ddss.service.IUserWebService;
import com.sinosoft.ddss.utils.Constants;
import com.sinosoft.ddss.utils.FastJsonUtil;
import com.sinosoft.ddss.utils.WebClientUtils;

@Component
@RabbitListener(queues = Constant.QUE_ORDERUSR_DEL)
public class OrderUserDel {

	private CountDownLatch latch = new CountDownLatch(1);

	@Autowired
	private IUserWebService userWebService;
	private static final  Logger log = LoggerFactory.getLogger(OrderUserDel.class);
	@RabbitHandler
	public void process(String message) {
		UserTask userTask = FastJsonUtil.toBean(message, UserTask.class);
		if (null != userTask) {
			List<UserTask> deleteList = new ArrayList<>();
			deleteList.add(userTask);
			String xmlInfo = userWebService.DDSS_ORDERUSR_DEL(deleteList);
			String result = WebClientUtils.webClient(
					"http://10.11.108.69:8880/DBMS/webservices/OrderCustomersDelService?wsdl", xmlInfo,
					"orderCustomersDel");
			if (result.toUpperCase().equals(Constant.REPLYINFO_SUCCESS)) {
				log.info("调用10.11.108.69:8880/DBMS/webservices/OrderCustomersDelService成功****************"+result);
				latch.countDown();
			}else{
				log.error("调用10.11.108.69:8880/DBMS/webservices/OrderCustomersDelService失败****************"+result);
			}
		}
	}
}