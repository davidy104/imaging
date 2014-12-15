package nz.co.dav.imaging.consume.integration.route

import nz.co.dav.imaging.consume.ImageMetaAPIOperationException
import nz.co.dav.imaging.consume.integration.processor.GetImageMetaResponseTransformer

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder

import com.google.inject.Inject
import com.google.inject.name.Named

class ImageMetaAPIOperationRoute extends RouteBuilder {

	@Inject
	@Named("IMAGING_PRODUCER_HTTP_URI")
	String imagingProducerHttpUri

	@Inject
	@Named("getImageMetaResponseTransformer")
	GetImageMetaResponseTransformer getImageMetaResponseTransformer

	//HttpOperationFailedException
	@Override
	public void configure() throws Exception {

		from("direct:getImageByTag")
				.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
				.setHeader(Exchange.HTTP_PATH, simple('${body}'))
				.to(imagingProducerHttpUri)
				.convertBodyTo(String.class)
				.choice()
				.when(header(Exchange.HTTP_RESPONSE_CODE).isEqualTo(200))
				.transform(getImageMetaResponseTransformer)
				.endChoice()
				.otherwise()
				.to("log:getImageByTag failed?level=ERROR")
				.throwException(new ImageMetaAPIOperationException())


		from("direct:deleteImagesByTag")
				.setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.DELETE))
				.setHeader(Exchange.HTTP_PATH, simple('${body}'))
				.to(imagingProducerHttpUri)
				.choice()
				.when(header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo(204))
				.to("log:deleteImagesByTag failed?level=ERROR")
				.throwException(new ImageMetaAPIOperationException())
				.endChoice()
	}
}
