package nz.co.dav.imaging.consume.integration.route;

import groovy.json.JsonSlurper
import nz.co.dav.imaging.consume.integration.processor.ImageEventMessageReceivingProcessor
import nz.co.dav.imaging.consume.integration.processor.ImageFetchFromS3Processor

import org.apache.camel.Exchange
import org.apache.camel.Expression
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.common.base.Splitter
import com.google.inject.Inject
import com.google.inject.name.Named

//org.apache.camel.component.http.HttpOperationFailedException
class ImageReceivingRoute extends RouteBuilder {

	@Inject
	@Named("imageEventMessageReceivingProcessor")
	ImageEventMessageReceivingProcessor imageEventMessageReceivingProcessor

	@Inject
	@Named("IMAGING_CONSUME_TYPE")
	String imagingConsumeType

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsEventQueueName

	@Inject
	@Named("IMAGING_PRODUCER_HTTP_URI")
	String imagingProducerHttpUri

	@Inject
	@Named("jsonSlurper")
	JsonSlurper jsonSlurper

	final static String S3PATH_KEY = "s3Path"
	final static String IMAGE_SCALINGS_DELIMITER =":"
	final static String PROCESS_SCALING_TYPE="thumbnail"

	@Inject
	@Named("imageFetchFromS3Processor")
	ImageFetchFromS3Processor imageFetchFromS3Processor

	@Inject
	@Named("imageBytesAggregationStrategy")
	AggregationStrategy imageBytesAggregationStrategy

	@Override
	public void configure() throws Exception {

		from("aws-sqs://$awsSqsEventQueueName?amazonSQSClient=#amazonSqs&delay=5000&maxMessagesPerPoll=1&deleteAfterRead=false")
				.autoStartup(true)
				.routeId("fetchImages")
				.transform(new Expression() {
					@Override
					public <T> T evaluate(Exchange exchange, Class<T> type) {
						String imageEventMessage = exchange.getIn().getBody(String.class)
						exchange.setProperty("imageEventMessage", imageEventMessage)
						String tag = imageEventMessage.substring(0, imageEventMessage.indexOf(":"))
						String path = "meta/" + tag
						return (T) path
					}
				})
				.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
				.setHeader(Exchange.HTTP_PATH, simple('${body}'))
				.to(imagingProducerHttpUri)
				.transform(new Expression() {
					@Override
					public <T> T evaluate(Exchange exchange, Class<T> type) {
						String jsonResponse = exchange.getIn().getBody(String.class)
						Set<String> needFetchImagePathSet = []
						List<Object> resultList = (List)jsonSlurper.parseText(jsonResponse)
						resultList.each {
							Map<String,Object> resultMap = (Map)it
							def s3keies = resultMap[S3PATH_KEY]
							String name = resultMap['name']
							println "s3keies:{} $s3keies"
							println "name:{} $name"
							Iterable<String> values = Splitter.on(IMAGE_SCALINGS_DELIMITER).split(s3keies)
							values.iterator().any {scalingImage->
								String selectScalingImageName = scalingImage.substring(scalingImage.lastIndexOf("/")+1, scalingImage.lastIndexOf("."))
								String matchImageName = name+"-"+PROCESS_SCALING_TYPE
								if(matchImageName.equals(selectScalingImageName)){
									needFetchImagePathSet << scalingImage
									true
								}
							}
						}
						return (T)needFetchImagePathSet
					}
				})
				.split(simple('${body}'),imageBytesAggregationStrategy)
				.process(imageFetchFromS3Processor)
				.end()
				.setBody(simple('${property.imagesBytesList}'))
				
				.choice()
				.when(imagingConsumeType.equalsIgnoreCase("email"))
				.to("velocity:dummy?loaderCache=false&contentCache=false")
				.to("direct:sendEmail")
				.endChoice()
				.when(imagingConsumeType.equalsIgnoreCase("file"))
				.bean(sendEmailUtils, "toTextPlainContent")
				.to("direct:doSendEmail")
				.endChoice()
				.otherwise()
				.to("log:unknown email format?level=ERROR")
				.throwException(new Exception("unknow email format"))
				
				
				
				
				
				//				.process(imageEventMessageReceivingProcessor)
				.to("direct:imageBatchToLocalFile")
				.end()

	}
}
