package nz.co.dav.imaging.integration.ds;

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.ImageProcessRequest;

import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
@Slf4j
class ImageProcess {

	@Produce(uri="direct:ImageProcess")
	private ProducerTemplate producerTemplate

	String process(final ImageProcessRequest imageProcessRequest) {
		return this.producerTemplate.requestBody(
		imageProcessRequest, String.class)
	}
}
