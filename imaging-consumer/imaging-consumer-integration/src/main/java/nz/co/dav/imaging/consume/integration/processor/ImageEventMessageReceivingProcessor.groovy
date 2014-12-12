package nz.co.dav.imaging.consume.integration.processor;

import groovy.util.logging.Slf4j

import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Processor

@Slf4j
class ImageEventMessageReceivingProcessor implements Processor {

	@Override
	void process(Exchange exchange) throws Exception {
		Message message = exchange.in
		log.info "exchange:{} $exchange"
		log.info "message:{} $message"

//		def body = message.getBody()
//		log.info "body:{} $body"

		List<Map<String,byte[]>> imagesBytesList = message.getBody(List.class)
		imagesBytesList.each {
			it.each {k,v->
				log.info "$k --- $v.length"
			}
		}
	}
}
