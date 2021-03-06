package nz.co.dav.imaging.integration.route

import java.text.SimpleDateFormat

import nz.co.dav.imaging.config.ConfigurationService
import nz.co.dav.imaging.event.ImageMetaDataPersistEvent

import org.apache.camel.Exchange
import org.apache.camel.ExchangePattern
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.common.eventbus.EventBus
import com.google.inject.Inject
import com.google.inject.name.Named

/**
 * com.amazonaws.services.s3.model.AmazonS3Exception
 * @author Davidy
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
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsEventQueueName

	@Inject
	@Named("imageMetaDataPersistEventBus")
	EventBus imageMetaDataPersistEventBus

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

	@Override
	public void configure() throws Exception {
		String eventTime = DATE_FORMAT.format(new Date())

		onException(java.net.SocketTimeoutException.class,
				java.net.ConnectException.class,com.amazonaws.services.s3.model.AmazonS3Exception.class)
				.maximumRedeliveries(2)
				.redeliveryDelay(1000)
				.handled(true)
				.to("log:errors?level=ERROR&showAll=true&multiline=true")

		onException(Exception.class)
				.handled(true)
				.to("log:errors?level=ERROR&showAll=true&multiline=true")

		from("direct:ImageProcess")
				.routeId('ImageProcess')
				.setExchangePattern(ExchangePattern.InOut)
				.setProperty("scalingConfigs", simple('${body.scalingConfigs}'))
				.setProperty("s3Path", simple('${body.s3Path}'))
				.setProperty("tag", simple('${body.tag}'))
				.setProperty("processTime", simple('${body.processTime}'))
				.split(simple('${body.images}'), imageMetadataAggregationStrategy)
				.parallelProcessing().executorServiceRef("genericThreadPool")
				.to("direct:singleImageProcess")
				.end()
				.to("direct:sendImgEvent")
				.threads()
				.executorServiceRef("genericThreadPool")
				.setBody(simple('${property.metadataSet}'))
				.threads()
				.executorServiceRef("genericThreadPool")
				.process(new Processor(){
					@Override
					public void process(final Exchange exchange) throws Exception {
						def imageMetaSet = exchange.getIn().getBody(Set.class)
						imageMetaDataPersistEventBus.post(new ImageMetaDataPersistEvent(imageMetaDataSet:imageMetaSet))
					}
				})
				.marshal().json(JsonLibrary.Jackson)
				.end()

		from("direct:singleImageProcess")
				.process(imageMetadataRetrievingProcessor)
				.wireTap("direct:scalingImage")
				.executorServiceRef("genericThreadPool")

		from("direct:scalingImage")
				.setProperty("imageInfo", simple('${body}'))
				.split(simple('${property.scalingConfigs}'))
				.to("direct:singleScalingImage")
				.end()

		from("direct:singleScalingImage")
				.process(imageScalingProcessor)
				.setHeader("CamelAwsS3ContentType", constant("image/jpeg"))
				.setHeader("CamelAwsS3ContentLength", simple('${property.imgStreamAvailable}'))
				.setHeader("CamelAwsS3Key", simple('${property.outputPath}'))
				.to("aws-s3://$awsS3Bucket?amazonS3Client=#amazonS3")

		from("direct:sendImgEvent")
				.setBody(simple('${property.tag}:'+eventTime))
				.to("aws-sqs://$awsSqsEventQueueName?amazonSQSClient=#amazonSqs")
	}
}
