package nz.co.dav.imaging.integration.route

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

	@Override
	public void configure() throws Exception {
		from("direct:ImageProcess")
				.routeId('ImageProcess')
				.setExchangePattern(ExchangePattern.InOut)
				.setProperty("scalingConfigs", simple('${body.scalingConfigs}'))
				.setProperty("s3Path", simple('${body.s3Path}'))
				.setProperty("tags", simple('${body.tags}'))
				.setProperty("processTime", simple('${body.processTime}'))
				.split(simple('${body.images}'), imageMetadataAggregationStrategy).parallelProcessing().executorServiceRef("genericThreadPool")
				.to("direct:singleImageProcess")
				.end()
				.setBody(simple('${property.metadataSet}'))
				.marshal().json().end()

		from("direct:singleImageProcess")
				.process(imageMetadataRetrievingProcessor)
				.wireTap("direct:scalingImage")
				.executorServiceRef("genericThreadPool")

		from("direct:scalingImage")
				.setProperty("imageInfo", simple('${body}'))
				.split(simple('${property.scalingConfigs}'))
				.process(imageScalingProcessor)
				.to('aws-s3://' + configurationService.getAwsS3BucketName()+ '?amazonS3Client=#amazonS3')
				.end()
	}
}
