package nz.co.dav.imaging.consume.integration.processor;

import groovy.util.logging.Slf4j

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.common.collect.Lists
import com.google.common.collect.Maps

@Slf4j
class ImageBytesAggregationStrategy implements AggregationStrategy {

	String allowedTotalImageGroupSizeStr

	public ImageBytesAggregationStrategy(final String allowedTotalImageGroupSizeStr) {
		this.allowedTotalImageGroupSizeStr = allowedTotalImageGroupSizeStr;
	}

	@Override
	public Exchange aggregate(Exchange oldExchange,
			Exchange newExchange) {
		log.info "ImageBytesAggregationStrategy start"
		byte[] imageBytes
		Object body
		List<Map<String,byte[]>> imagesBytesList
		Map<String,byte[]> imageBytesMap
		Integer allowedTotalImageGroupSize = Integer.valueOf(allowedTotalImageGroupSizeStr)
		log.info "allowedTotalImageGroupSize:{} $allowedTotalImageGroupSize"

		String fileName
		body = newExchange.in.getBody()
		boolean ifFirstTime = (oldExchange == null)?true:false

		if(body instanceof byte[]){
			log.info "body is byte"
			imageBytes = (byte[])body
			if(checkImageSize(imageBytes,allowedTotalImageGroupSize)){
				log.info "ImageBytesAggregationStrategy do start"
				imageBytes = (byte[])body
				log.info "current imageBytes.length:{} $imageBytes.length"
				fileName = newExchange.properties['fileName']
				if (ifFirstTime) {
					imageBytesMap = Maps.<String,byte[]>newHashMap()
					imagesBytesList = Lists.<Map<String,byte[]>>newArrayList()
					imageBytesMap.put(fileName, imageBytes)
					imagesBytesList << imageBytesMap
					newExchange.setProperty("imagesBytesList", imagesBytesList)
				} else {
					imagesBytesList = oldExchange.properties['imagesBytesList']
					imageBytesMap = imagesBytesList.last()

					int previousImageTotalBytes = 0
					imageBytesMap.values().each {
						byte[] previousBytes = (byte[])it
						previousImageTotalBytes += previousBytes.length
					}

					double megabytes = (previousImageTotalBytes+imageBytes.length) / 1024 / 1024
					log.info "megabytes:{} $megabytes"
					if(megabytes > allowedTotalImageGroupSize){
						imageBytesMap = Maps.<String,byte[]>newHashMap()
						imageBytesMap.put(fileName, imageBytes)
						imagesBytesList << imageBytesMap
					} else {
						imageBytesMap.put(fileName, imageBytes)
					}
					oldExchange.setProperty("imagesBytesList", imagesBytesList)
				}
			}
		}
		if(ifFirstTime){
			if(!imagesBytesList){
				imageBytesMap = Maps.<String,byte[]>newHashMap()
				imagesBytesList = Lists.<Map<String,byte[]>>newArrayList()
				imagesBytesList << imageBytesMap
			}
			newExchange.setProperty("imagesBytesList", imagesBytesList)
			return newExchange
		} else {
			return oldExchange
		}
	}

	boolean checkImageSize(byte[] data, int maximumSize){
		double megabytes = data.length / 1024 / 1024
		if (megabytes <= maximumSize) {
			return true
		}
		return false
	}
}
