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
		String tags = exchange.getProperty("tags", String.class)
		String processTime = exchange.getProperty("processTime", String.class)
		String name = imageInfo.imageName

		byte[] imageBytes = imageInfo.imageBytes
		def metadataMap = [:]
		final IImageMetadata metadata = Sanselan.getMetadata(imageBytes);
		if(metadata) {
			metadata.getItems().each{
				Item item = (Item)it
				def originalKeyword = item.keyword
				originalKeyword = originalKeyword.replaceAll("\\s", "")
				metadataMap.put(originalKeyword, item.text)
			}
		}
		metadataMap.put("tags", tags)
		metadataMap.put("name", name)
		metadataMap.put("processTime", processTime)
		log.info "metadataMap:{} $metadataMap"
		exchange.setProperty("metadataMap", metadataMap)
	}
}
