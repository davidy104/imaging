package nz.co.dav.imaging.integration.processor

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.AbstractImageInfo

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.sanselan.Sanselan
import org.apache.sanselan.common.IImageMetadata
import org.apache.sanselan.common.ImageMetadata.Item

import com.google.common.base.Joiner

@Slf4j
class ImageMetadataRetrievingProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		AbstractImageInfo imageInfo = exchange.getIn().getBody(AbstractImageInfo.class)
		String tag = exchange.getProperty("tag", String.class)
		String s3Path = exchange.getProperty("s3Path")
		String processTime = exchange.getProperty("processTime", String.class)
		String name = imageInfo.imageName
		def extension = imageInfo.extension

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

		List<Map<String, String>> scalingConfigs = exchange.properties['scalingConfigs']
		Set<String> s3KeySet=[]
		def s3Prefix

		if(tag){
			tag = !tag.endsWith("/")?tag+"/":tag
			s3Prefix = !s3Path.endsWith("/")?s3Path+"/"+tag:s3Path+tag
		} else {
			s3Prefix = !s3Path.endsWith("/")?s3Path+"/":s3Path
		}
		metadataMap.put("s3Prefix", s3Prefix)

		scalingConfigs.each {
			def scalingName = it['name']
			def imageFullPath = s3Prefix + name +"-"+scalingName+ "."+extension
			s3KeySet << imageFullPath
		}
		String imageScalingS3Keis = Joiner.on(":").join(s3KeySet)
		metadataMap.put("imagesS3Keis", imageScalingS3Keis)

		exchange.setProperty("metadataMap", metadataMap)
	}
}
