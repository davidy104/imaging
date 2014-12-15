package nz.co.dav.imaging.consume.integration.route;

import nz.co.dav.imaging.consume.integration.processor.ImageFetchFromS3Processor

import org.apache.camel.Exchange
import org.apache.camel.Expression
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.inject.Inject
import com.google.inject.name.Named

//org.apache.camel.component.http.HttpOperationFailedException
class ImageReceivingRoute extends RouteBuilder {

	@Inject
	@Named("IMAGING_CONSUME_TYPE")
	String imagingConsumeType

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsEventQueueName

	@Inject
	@Named("imageFetchFromS3Processor")
	ImageFetchFromS3Processor imageFetchFromS3Processor

	@Inject
	@Named("imageBytesAggregationStrategy")
	AggregationStrategy imageBytesAggregationStrategy

	@Override
	public void configure() throws Exception {

		from("aws-sqs://$awsSqsEventQueueName?amazonSQSClient=#amazonSqs&delay=5000&maxMessagesPerPoll=1&deleteAfterRead=true")
				.autoStartup(true)
				.routeId("fetchImages")
				.transform(new Expression() {
					@Override
					public <T> T evaluate(Exchange exchange, Class<T> type) {
						String imageEventMessage = exchange.getIn().getBody(String.class)
						exchange.setProperty("imageEventMessage", imageEventMessage)
						String tag = imageEventMessage.substring(0, imageEventMessage.indexOf(":"))
						exchange.setProperty("tag", tag)
						String path = "meta/" + tag
						return (T) path
					}
				})
				.to("direct:getImageByTag")
				.split(simple('${body}'),imageBytesAggregationStrategy)
				.process(imageFetchFromS3Processor)
				.end()
				.setBody(simple('${property.imagesBytesList}'))
				.to("direct:imagesOutput")
				.setBody(simple('${property.tag}'))
				.to("direct:deleteImagesByTag")
				.end()

		from("direct:imagesOutput")
				.choice()
				.when(constant(imagingConsumeType).isEqualTo("email"))
				.to("direct:imageBatchToEmail")
				.endChoice()
				.when(constant(imagingConsumeType).isEqualTo("file"))
				.to("direct:imageBatchToLocalFile")
				.endChoice()
				.when(constant(imagingConsumeType).isEqualTo("both"))
				.multicast().parallelProcessing().executorServiceRef("genericThreadPool")
				.to("direct:imageBatchToEmail").to("direct:imageBatchToLocalFile").end()
				.endChoice()
				.otherwise()
				.to("log:unknown email format?level=ERROR")
				.throwException(new RuntimeException("unknow imagingConsumeType."))

	}
}
