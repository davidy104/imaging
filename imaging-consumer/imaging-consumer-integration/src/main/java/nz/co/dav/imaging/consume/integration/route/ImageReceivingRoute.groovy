package nz.co.dav.imaging.consume.integration.route

import nz.co.dav.imaging.consume.config.ConfigurationService

import org.apache.camel.builder.RouteBuilder

import com.google.inject.Inject
import com.google.inject.name.Named

class ImageReceivingRoute extends RouteBuilder {

	@Inject
	ConfigurationService configurationService
	
	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsEventQueueName
	
	@Override
	void configure() throws Exception {
	}
}
