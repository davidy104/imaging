package nz.co.dav.imaging.test;

import static nz.co.dav.imaging.util.JerseyClientUtil.getResponsePayload
import static org.junit.Assert.*
import groovy.util.logging.Slf4j

import javax.ws.rs.core.MediaType

import nz.co.dav.imaging.SharedModule
import nz.co.dav.imaging.config.ConfigurationServiceModule
import nz.co.dav.imaging.integration.ImageCamelContextModule
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.io.Resources
import com.google.inject.Inject
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataBodyPart
import com.sun.jersey.multipart.FormDataMultiPart

@RunWith(GuiceJUnitRunner.class)
@GuiceModules([ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class ])
@Slf4j
class ImageProcessAPIIntegrationTest {

	static final String imageServiceURI = 'http://localhost:8222/image/'

	private Client jerseyClient

	static final String IMAG = "test01.JPG";

	byte[] imageBytes

	@Inject
	public void setJerseyClient(Client jerseyClient) {
		this.jerseyClient = jerseyClient;
	}

	@Before
	void setUp(){
		imageBytes = Resources.toByteArray(Resources.getResource(IMAG))
	}

	@Test
	public void testProcessImage() {
		//		Set<ImageScalingConfig> imageScalingConfigs = []
		//		imageScalingConfigs << new ImageScalingConfig(scalingMode:'standard',width:1024,height:1024)


		final FormDataContentDisposition dispo = FormDataContentDisposition
				.name("uploadedImage")
				.fileName("uploadedImage.jpg")
				.size(imageBytes.length)
				.build()

		final FormDataBodyPart imageBodyPart = new FormDataBodyPart(dispo, imageBytes,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);


		FormDataMultiPart multiPart = new FormDataMultiPart()
		//				multiPart.field("imageProcessRequest", imageProcessRequest, MediaType.APPLICATION_JSON_TYPE)
		multiPart.field("scalingConfig", "hello world")
		multiPart.bodyPart(imageBodyPart)

		ClientResponse response = jerseyClient.resource(imageServiceURI).path("process").type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class,multiPart)

		int statusCode = response.getStatus()
		log.info "statusCode:{} $statusCode"

		def responseStr = getResponsePayload(response)
		log.info "responseStr:{} $responseStr"
	}
}
