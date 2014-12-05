package nz.co.dav.imaging.consume.integration.route;

import nz.co.dav.imaging.consume.integration.processor.ImageEventMessageReceivingProcessor;

import org.apache.camel.builder.RouteBuilder;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ImageReceivingRoute extends RouteBuilder {

	@Inject
	@Named("imageEventMessageReceivingProcessor")
	private ImageEventMessageReceivingProcessor imageEventMessageReceivingProcessor;

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	private String awsSqsEventQueueName;

	@Override
	public void configure() throws Exception {
		from("aws-sqs://" + awsSqsEventQueueName + "?amazonSQSClient=#amazonSqs&delay=5000&maxMessagesPerPoll=1&deleteAfterRead=true")
				.autoStartup(true)
				.process(imageEventMessageReceivingProcessor)
				.end();
	}
}
