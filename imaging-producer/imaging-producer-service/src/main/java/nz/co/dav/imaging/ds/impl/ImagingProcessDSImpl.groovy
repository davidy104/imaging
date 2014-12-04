package nz.co.dav.imaging.ds.impl;

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.model.AbstractImageInfo
import nz.co.dav.imaging.model.ImageProcessRequest

import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate

@Slf4j
class ImagingProcessDSImpl implements ImagingProcessDS {

	@Produce(uri="direct:ImageProcess")
	private ProducerTemplate producerTemplate

	@Override
	String process(final String scalingConfig,final String tags, final Map<String, byte[]> imagesMap) {
		ImageProcessRequest imageProcessRequest
		return producerTemplate.requestBody(imageProcessRequest, String.class)
	}
	
	
	AbstractImageInfo buildAbstractImage(final String imageName,final byte[] imageBytes){
		return new AbstractImageInfo(imageBytes:imageBytes)
	}
}
