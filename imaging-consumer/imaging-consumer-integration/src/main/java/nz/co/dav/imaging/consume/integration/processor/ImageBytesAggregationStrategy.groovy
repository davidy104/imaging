package nz.co.dav.imaging.consume.integration.processor;

import groovy.util.logging.Slf4j

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class ImageBytesAggregationStrategy implements AggregationStrategy {

	@Inject
	@Named("IMAGE_SIZE_PER_GROUP")
	String allowedTotalImageGroupSizeStr

	@Override
	public Exchange aggregate(Exchange oldExchange,
			Exchange newExchange) {
		byte[] imageBytes
		Object body
		List<Map<String,byte[]>> imagesBytesList
		Map<String,byte[]> imageBytesMap
		Integer allowedTotalImageGroupSize = Integer.valueOf(allowedTotalImageGroupSizeStr)
		String fileName
		body = newExchange.in.getBody()
		boolean ifFirstTime = (oldExchange == null)?true:false
		if(body instanceof byte[] && checkImageSize((byte[])body,allowedTotalImageGroupSize)){
			imageBytes = (byte[])body
			if (ifFirstTime) {
				fileName = newExchange.properties['fileName']
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
		if(ifFirstTime){
			if(!imagesBytesList){
				imageBytesMap = Maps.<String,byte[]>newHashMap()
				imagesBytesList = Lists.<Map<String,byte[]>>newArrayList()
				imagesBytesList << imageBytesMap
			}
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
