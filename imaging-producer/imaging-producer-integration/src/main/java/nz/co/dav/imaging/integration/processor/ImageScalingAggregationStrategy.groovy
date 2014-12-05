package nz.co.dav.imaging.integration.processor;

import groovy.util.logging.Slf4j;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

@Slf4j
class ImageScalingAggregationStrategy implements AggregationStrategy {

	@Override
	Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		return null
	}

}
