package nz.co.dav.imaging.integration.processor;

import groovy.util.logging.Slf4j;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

@Slf4j
class ImageScalingAggregationStrategy implements AggregationStrategy {

	@Override
	Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		String scalingFilesMessage
		if (!oldExchange) {
			scalingFilesMessage = newExchange.getProperty("outputPath", String.class)
			newExchange.setProperty("scalingFilesMessage", scalingFilesMessage)
			return newExchange
		}
		scalingFilesMessage = oldExchange.getProperty("scalingFilesMessage", String.class)
		def newOutpath = newExchange.getProperty("outputPath", String.class)
		oldExchange.setProperty("scalingFilesMessage", "$scalingFilesMessage:$newOutpath")
		return oldExchange
	}
}
