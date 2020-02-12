
package com.sinosoft.ddss.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.sinosoft.ddss.common.entity.SysConfig;
import com.sinosoft.ddss.common.util.JsonUtils;
import com.sinosoft.ddss.jedis.JedisClient;
import com.sinosoft.ddss.service.ISysConfigService;

@Component
@Order(value=1)
public class ConfigInitializer implements CommandLineRunner {
	@Autowired
	private ISysConfigService sysConfigService;
	@Autowired
	private JedisClient jedisClient;
	@Override
	public void run(String... args) throws Exception {
		System.err.println("*****************配置初始化开始******************");
		List<SysConfig> listSysConfig = sysConfigService.listSysConfig();
		String[] keyArr = new String[listSysConfig.size()];
		for(int i=0;i<keyArr.length;i++){
			keyArr[i] = listSysConfig.get(i).getConfigKey();
		}
		TreeMap<String, Integer> map = new TreeMap<String,Integer>();
		if(null!=listSysConfig&&listSysConfig.size()>0){
			for (int i = 0; i < keyArr.length; i++) {
				if(!map.isEmpty() && map.containsKey(keyArr[i])){
					map.put(keyArr[i], map.get(keyArr[i])+1);
				} else {
					map.put(keyArr[i], 1);
				}
			}
		}
		Set<String> list = new TreeSet<String>();
		for (String key : map.keySet()) {
			if(map.get(key)==1){
				list.add(key);
			}
		}
		Map<Object, Object> idMap = new HashMap<>();
		for (int i = 0; i < listSysConfig.size(); i++) {
			SysConfig sysConfig = listSysConfig.get(i);
			String configKey = sysConfig.getConfigKey();
			if (i == 0) {
				idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
			} else {
				if (configKey.equals(listSysConfig.get(i - 1).getConfigKey())) {
					idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
				}else{
					idMap.clear();
					idMap.put(sysConfig.getId(), sysConfig.getConfigValue());
				}
			}
			jedisClient.set(configKey, JsonUtils.mapToJson(idMap));
		}
		if(list.size()>0){
			for (int i = 0; i < listSysConfig.size(); i++) {
				for (String s : list) {
					if(s.equals(listSysConfig.get(i).getConfigKey())){
						jedisClient.set(listSysConfig.get(i).getConfigKey(), listSysConfig.get(i).getConfigValue());
					} 
				}
			}
		}
		System.err.println("*****************配置初始化完成******************");
	}
}
