package com.sinosoft.ddss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

//Spring Boot 应用的标识
@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
//mapper 接口类扫描包配置
//@MapperScan("com.sinosoft.ddss.dao")
//mapper 接口类扫描包配置
@EnableEurekaClient//表示可以作为服务向注册中心注册
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}
}
