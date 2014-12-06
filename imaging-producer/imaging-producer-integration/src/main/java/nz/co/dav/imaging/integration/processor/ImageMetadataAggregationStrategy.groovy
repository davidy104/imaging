package nz.co.dav.imaging.integration.processor;

import groovy.util.logging.Slf4j

import org.apache.camel.Exchange
import org.apache.camel.processor.aggregate.AggregationStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.google.common.collect.Sets

@Slf4j
class ImageMetadataAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange,
			Exchange newExchange) {
		Set<Map<String,String>> metaSet
		def totalImageCount
		Map<String,String> imageMetaMap

		if (!oldExchange) {
			newExchange.setProperty("totalImageCount", 1)
			return newExchange
		}
		totalImageCount = oldExchange.getProperty("totalImageCount")
		metaSet = oldExchange.getProperty("metadataSet", Set.class)
		if(!metaSet){
			metaSet = Sets.<Map<String,String>>newHashSet()
			imageMetaMap = oldExchange.getProperty("metadataMap", Map.class)
			metaSet << imageMetaMap
		}
		imageMetaMap = newExchange.getProperty("metadataMap", Map.class)
		metaSet << imageMetaMap

		log.info "metaSet:{} $metaSet"

		oldExchange.setProperty("totalImageCount", totalImageCount++)
		oldExchange.setProperty("metadataSet", metaSet)
		return oldExchange
	}
}
