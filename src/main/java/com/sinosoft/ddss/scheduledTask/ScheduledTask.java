package com.sinosoft.ddss.scheduledTask;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sinosoft.ddss.common.entity.UserTask;
import com.sinosoft.ddss.dao.UserTaskMapper;
import com.sinosoft.ddss.service.IUserWebService;
import com.sinosoft.ddss.utils.Constants;
import com.sinosoft.ddss.utils.WebClientUtils;

/**
 * @author li_jiazhi
 * @create 2018年3月22日下午6:07:54
 * 定时任务类
 */
@Component
public class ScheduledTask {
	
	private static final  Logger log = Logger.getLogger(ScheduledTask.class);
	@Autowired
	private UserTaskMapper userTaskMapper;
	@Autowired
	private IUserWebService userWebService;
	/**
	 * 
	 * @author senKung
	 * @create 2018年3月22日下午6:35:35
	 *  定时扫描任务表,每半个小时执行一次
	 */
//	@Scheduled(fixedRate=30*60*1000)
	public void listTask(){
		//查出来 ddss_user_task 表里面的数据 按需进行同步
		List<UserTask> listTask = userTaskMapper.listUserTask();
		List<UserTask> addList = new ArrayList<>();
		List<UserTask> modifyList = new ArrayList<>();
		List<UserTask> deleteList = new ArrayList<>();
		if(null != listTask || listTask.size()>0){
			for (UserTask userTask : listTask) {
				if(userTask.getType()==1){
					addList.add(userTask);
				} else if (userTask.getType()==2){
					modifyList.add(userTask);
				} else {
					deleteList.add(userTask);
				}
			}
		}
		String result = "";
		if(null != addList || addList.size()>0){
			String xmlInfo = userWebService.DDSS_ORDERUSR_ADD(addList);
			String resultXml = WebClientUtils.webClient("http://localhost:8996/services/CommonService?wsdl", xmlInfo, "sayHello");
			result = WebClientUtils.getResult(resultXml);
		}
		if(result.equals(Constants.REPLYINFO_SUCCESS)){
			//删除数据
			for (UserTask userTask : addList) {
				userTaskMapper.deleteByPrimaryKey(userTask.getId());
			}
		}
		
		if(null != modifyList || modifyList.size()>0){
			String xmlInfo = userWebService.DDSS_ORDERUSR_MOD(modifyList);
			result = WebClientUtils.webClient("http://localhost:8996/services/CommonService?wsdl", xmlInfo, "sayHello");
		}
		if(result.equals(Constants.REPLYINFO_SUCCESS)){
			//删除数据
			for (UserTask userTask : modifyList) {
				userTaskMapper.deleteByPrimaryKey(userTask.getId());
			}
		}
		if(null != deleteList || deleteList.size()>0){
			String xmlInfo = userWebService.DDSS_ORDERUSR_DEL(deleteList);
			result = WebClientUtils.webClient("http://localhost:8996/services/CommonService?wsdl", xmlInfo, "sayHello");
		}
		if(result.equals(Constants.REPLYINFO_SUCCESS)){
			//删除数据
			for (UserTask userTask : deleteList) {
				userTaskMapper.deleteByPrimaryKey(userTask.getId());
			}
		}
	}
}


/*秒：可出现", - * /"四个字符，有效范围为0-59的整数  

分：可出现", - * /"四个字符，有效范围为0-59的整数  

时：可出现", - * /"四个字符，有效范围为0-23的整数  

每月第几天：可出现", - * / ? L W C"八个字符，有效范围为0-31的整数  

月：可出现", - * /"四个字符，有效范围为1-12的整数或JAN-DEc  

星期：可出现", - * / ? L C #"四个字符，有效范围为1-7的整数或SUN-SAT两个范围。1表示星期天，2表示星期一， 依次类推

 * : 表示匹配该域的任意值，比如在秒*, 就表示每秒都会触发事件。；

? : 只能用在每月第几天和星期两个域。表示不指定值，当2个子表达式其中之一被指定了值以后，为了避免冲突，需要将另一个子表达式的值设为“?”；

- : 表示范围，例如在分域使用5-20，表示从5分到20分钟每分钟触发一次  

/ : 表示起始时间开始触发，然后每隔固定时间触发一次，例如在分域使用5/20,则意味着5分，25分，45分，分别触发一次.  

, : 表示列出枚举值。例如：在分域使用5,20，则意味着在5和20分时触发一次。  

L : 表示最后，只能出现在星期和每月第几天域，如果在星期域使用1L,意味着在最后的一个星期日触发。  

W : 表示有效工作日(周一到周五),只能出现在每月第几日域，系统将在离指定日期的最近的有效工作日触发事件。注意一点，W的最近寻找不会跨过月份  

LW : 这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。  

# : 用于确定每个月第几个星期几，只能出现在每月第几天域。例如在1#3，表示某月的第三个星期日。*/

//"0 0 * * * *"                      表示每小时0分0秒执行一次

//" */10 * * * * *"                 表示每10秒执行一次

//"0 0 8-10 * * *"                 表示每天8，9，10点执行

//"0 0/30 8-10 * * *"            表示每天8点到10点，每半小时执行

//"0 0 9-17 * * MON-FRI"     表示每周一至周五，9点到17点的0分0秒执行

//"0 0 0 25 12 ?"                  表示每年圣诞节（12月25日）0时0分0秒执行
