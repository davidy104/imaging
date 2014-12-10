package nz.co.dav.imaging.integration.processor

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.AbstractImageInfo

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.sanselan.Sanselan
import org.apache.sanselan.common.IImageMetadata
import org.apache.sanselan.common.ImageMetadata.Item

@Slf4j
class ImageMetadataRetrievingProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		AbstractImageInfo imageInfo = exchange.getIn().getBody(AbstractImageInfo.class)
		String tag = exchange.getProperty("tag", String.class)
		String processTime = exchange.getProperty("processTime", String.class)
		String name = imageInfo.imageName

		byte[] imageBytes = imageInfo.imageBytes
		def metadataMap = [:]
		final IImageMetadata metadata = Sanselan.getMetadata(imageBytes);
		if(metadata) {
			metadata.getItems().each{
				Item item = (Item)it
				def originalKeyword = item.keyword
				if(!originalKeyword.startsWith("Unknown")){
					originalKeyword = originalKeyword.replaceAll("\\s", "")
					def text = item.text
					text = text.replaceAll("/", "\\/")
					text =  text.replaceAll("'", "")
					metadataMap.put(originalKeyword, text)
				}
			}
		}
		metadataMap.put("tag", tag)
		metadataMap.put("name", name)
		metadataMap.put("processTime", processTime)
		exchange.setProperty("metadataMap", metadataMap)
	}
}
