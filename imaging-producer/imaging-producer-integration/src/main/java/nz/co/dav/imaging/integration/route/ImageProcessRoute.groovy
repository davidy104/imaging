package nz.co.dav.imaging.integration.route

import java.text.SimpleDateFormat

import nz.co.dav.imaging.config.ConfigurationService

import org.apache.camel.ExchangePattern
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.aggregate.AggregationStrategy

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

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

	@Override
	public void configure() throws Exception {
		String eventTime = DATE_FORMAT.format(new Date())

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
				.to("direct:sendImgEvent")
				.threads()
				.executorServiceRef("genericThreadPool")
				.setBody(simple('${property.metadataSet}'))
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
				.setBody(simple('${property.tags}:'+eventTime))
				.to("aws-sqs://$awsSqsEventQueueName?amazonSQSClient=#amazonSqs")
	}
}
