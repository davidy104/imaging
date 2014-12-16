package nz.co.dav.imaging.webuser.config

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.filter.LoggingFilter
import com.sun.jersey.api.json.JSONConfiguration

@Configuration
class DSApplicationContextConfiguration {
	@Bean(destroyMethod = "destroy")
	Client jerseyClient() {
		com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig()
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
				Boolean.TRUE)
		Client client = Client.create(config)
		client.setConnectTimeout(10000)
		client.setReadTimeout(10000)
		client.addFilter(new LoggingFilter(System.out))
		return client
	}

	@Bean
	JsonSlurper jsonSlurper(){
		return new JsonSlurper()
	}

	@Bean
	JsonBuilder jsonBuilder(){
		return new JsonBuilder()
	}
}
