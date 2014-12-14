package nz.co.dav.imaging.consume.integration.processor

import groovy.util.logging.Slf4j

import org.apache.camel.ConsumerTemplate
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor
import org.apache.commons.io.IOUtils

@Slf4j
class ImageFetchFromS3Processor implements Processor {

	String awsS3Bucket

	public ImageFetchFromS3Processor(final String awsS3Bucket) {
		this.awsS3Bucket = awsS3Bucket;
	}

	@Override
	void process(Exchange exchange) {
		log.info "ImageFetchFromS3Processor start"
		InputStream fileStream
		final String s3key = exchange.in.getBody(String.class)
		String fileName = s3key.substring(s3key.lastIndexOf("/")+1, s3key.length())
		
		log.info "s3key:{} $s3key"
		log.info "fileName:{} $fileName"
		
		final ConsumerTemplate template =exchange.getContext().createConsumerTemplate()
		Exchange s3ImageExchange = template.receive("aws-s3://$awsS3Bucket?amazonS3Client=#amazonS3&maxMessagesPerPoll=1&prefix=${s3key}", 5000L)

		if (s3ImageExchange) {
			Message message = s3ImageExchange.getIn()
			Long awsS3ContentLength =(Long)message.headers['CamelAwsS3ContentLength']
			exchange.setProperty('awsS3ContentLength',awsS3ContentLength)
			exchange.setProperty('fileName',fileName)
			try {
				fileStream = message.getBody(InputStream.class)
				byte[] cotentBytes = IOUtils.toByteArray(fileStream)
				exchange.in.setBody(cotentBytes, byte[].class)
			} catch (e) {
				throw e
			} finally{
				if(fileStream){
					fileStream.close()
				}
			}
		}
	}
}
