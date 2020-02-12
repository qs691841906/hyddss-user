package com.sinosoft.ddss.config;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sinosoft.ddss.service.IUserWebService;


@Configuration
public class CxfConfig {
	@Autowired
	private Bus bus;
	
	@Autowired
	IUserWebService userWebService;

	/** JAX-WS **/
	@Bean
	public Endpoint endpoint() {
		EndpointImpl endpoint = new EndpointImpl(bus, userWebService);
		endpoint.publish("/UserWebService");
		return endpoint;
	}
	
}
