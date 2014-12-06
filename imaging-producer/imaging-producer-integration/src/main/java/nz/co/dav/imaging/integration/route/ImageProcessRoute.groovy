package nz.co.dav.imaging.integration.route

import nz.co.dav.imaging.config.ConfigurationService
import nz.co.dav.imaging.integration.event.ImageSentToS3Event
import nz.co.dav.imaging.integration.event.ImagesSentCompletedEvent

import org.apache.camel.Exchange
import org.apache.camel.ExchangePattern
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.google.inject.name.Named

/**
 * com.amazonaws.services.s3.model.AmazonS3Exception
 * @author Davidy
 *
 */
class ImageProcessRoute extends RouteBuilder {

	@Inject
	ConfigurationService configurationService

	@Inject
	@Named("imageMetadataRetrievingProcessor")
	Processor imageMetadataRetrievingProcessor

	@Inject
	@Named("imageScalingProcessor")
	Processor imageScalingProcessor

	@Inject
	@Named("imageMetadataAggregationStrategy")
	AggregationStrategy imageMetadataAggregationStrategy

	@Inject
	@Named("imageScalingAggregationStrategy")
	AggregationStrategy imageScalingAggregationStrategy

	@Inject
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsEventQueueName

	@Inject
	@Named("imageSendEventBus")
	final EventBus imageSendEventBus


	@Override
	public void configure() throws Exception {

		from("direct:ImageProcess")
				.routeId('ImageProcess')
				.setExchangePattern(ExchangePattern.InOut)
				.setProperty("scalingConfigs", simple('${body.scalingConfigs}'))
				.setProperty("s3Path", simple('${body.s3Path}'))
				.setProperty("tags", simple('${body.tags}'))
				.setProperty("processTime", simple('${body.processTime}'))
				.split(simple('${body.images}'), imageMetadataAggregationStrategy)
				.parallelProcessing().executorServiceRef("genericThreadPool")
				.to("direct:singleImageProcess")
				.end()
				//				.threads()
				//				.executorServiceRef("genericThreadPool")
				.to("direct:generateImgMetaJson")
				.end()

		from("direct:singleImageProcess")
				.onCompletion().onCompleteOnly()
				.process(new Processor(){
					@Override
					public void process(Exchange exchange) throws Exception {
						imageSendEventBus.post(new ImagesSentCompletedEvent(sqsEventQueueName:awsSqsEventQueueName))
					}
				})
				.end()
				.process(imageMetadataRetrievingProcessor)
				.wireTap("direct:scalingImage")
				.executorServiceRef("genericThreadPool")

		from("direct:scalingImage")
				.setProperty("imageInfo", simple('${body}'))
				.split(simple('${property.scalingConfigs}'),imageScalingAggregationStrategy)
				.to("direct:singleScalingImage")
				.end()
				.process(new Processor(){
					@Override
					public void process(Exchange exchange) throws Exception {
						def s3Key = exchange.getProperty("scalingFilesMessage")
						imageSendEventBus.post(new ImageSentToS3Event(s3Key:s3Key))
					}
				})

		from("direct:singleScalingImage")
				.process(imageScalingProcessor)
				.setHeader("CamelAwsS3ContentType", constant("image/jpeg"))
				.setHeader("CamelAwsS3ContentLength", simple('${property.imgStreamAvailable}'))
				.setHeader("CamelAwsS3Key", simple('${property.outputPath}'))
				.to("aws-s3://$awsS3Bucket?amazonS3Client=#amazonS3")


		from("direct:generateImgMetaJson")
				.setBody(simple('${property.metadataSet}'))
				.marshal().json()

		//		from("direct:sendImgEvent")
		//				.setBody(simple('${property.scalingFilesMessage}'))
		//				.to("aws-sqs://$awsSqsEventQueueName?amazonSQSClient=#amazonSqs")
	}
}
