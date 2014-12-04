package nz.co.dav.imaging.integration;

import groovy.util.logging.Slf4j

import org.apache.camel.Consume
import org.apache.camel.Exchange
import org.apache.camel.Message

@Slf4j
class ImageEventConsumer {

	private String consumeEndpoint

	public ImageEventConsumer(final String consumeEndpoint) {
		this.consumeEndpoint = consumeEndpoint;
	}

	@Consume(property = "consumeEndpoint")
	public void onImgEventMessage(final Exchange exchange) {
		Message message = exchange.getIn()
		String body = message.getBody(String.class)
		log.info "received image event message--------------------:{} $body"

		Map<String, Object> headers = message.getHeaders()

		headers.each {k,v->
			log.info "$k ---- $v"
		}
	}
}