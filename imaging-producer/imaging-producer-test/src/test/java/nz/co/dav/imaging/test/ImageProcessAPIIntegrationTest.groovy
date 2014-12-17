package nz.co.dav.imaging.test;

import static nz.co.dav.imaging.util.JerseyClientUtil.getResponsePayload
import static org.junit.Assert.*
import groovy.util.logging.Slf4j

import javax.ws.rs.core.MediaType

import nz.co.dav.imaging.SharedModule
import nz.co.dav.imaging.config.ConfigurationServiceModule
import nz.co.dav.imaging.integration.ImageCamelContextModule
import nz.co.dav.imaging.repository.ImagingRepositoryModule
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.io.Resources
import com.google.inject.Inject
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataBodyPart
import com.sun.jersey.multipart.FormDataMultiPart

@RunWith(GuiceJUnitRunner.class)
@GuiceModules([ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class,ImagingRepositoryModule.class ])
@Slf4j
class ImageProcessAPIIntegrationTest {

	static final String imageServiceURI = 'http://localhost/image/'

	private Client jerseyClient

	static final String[] IMAGS = ["test01.JPG", "test02.JPG","test03.JPG","test04.JPG","test05.JPG","test06.JPG"]

	Map<String,byte[]> imagesMap = [:]

	static final String TEST_TAG="SANTA PARADE"
	
	static final String TEST_DELETE_NAME="santa-2"

	@Inject
	public void setJerseyClient(Client jerseyClient) {
		this.jerseyClient = jerseyClient;
	}

	@Before
	void setUp(){
		IMAGS.eachWithIndex  {obj,i->
			def imageName = "santa-$i"
			imagesMap.put(imageName, Resources.toByteArray(Resources.getResource(obj)))
		}
	}

	@Test
	public void testProcessImage() {
		FormDataMultiPart multiPart = new FormDataMultiPart()
		multiPart.field("scalingConfig", "normal=1024*1024,thumbnail=1217*1217")
		multiPart.field("tag", TEST_TAG)

		imagesMap.each{k,v->
			final FormDataContentDisposition dispo = FormDataContentDisposition
					.name(k)
					.size(v.length)
					.build()

			final FormDataBodyPart imageBodyPart = new FormDataBodyPart(dispo, v,
					MediaType.APPLICATION_OCTET_STREAM_TYPE)

			multiPart.bodyPart(imageBodyPart)
		}
		ClientResponse response = jerseyClient.resource(imageServiceURI).path("process").type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class,multiPart)

		int statusCode = response.getStatus()
		log.info "statusCode:{} $statusCode"

		def responseStr = getResponsePayload(response)
		log.info "responseStr:{} $responseStr"
	}

	@Test
	public void testDelete(){
		WebResource webResource = jerseyClient.resource(imageServiceURI).path(TEST_TAG)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class)

		int statusCode = response.getStatus()
		log.info "statusCode:{} $statusCode"

		def responseStr = getResponsePayload(response)
		log.info "responseStr:{} $responseStr"
	}
	
	@Test
	public void testDeleteByTagAndName(){
		WebResource webResource = jerseyClient.resource(imageServiceURI).path(TEST_TAG).path(TEST_DELETE_NAME)
				ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class)
				
				int statusCode = response.getStatus()
				log.info "statusCode:{} $statusCode"
				
				def responseStr = getResponsePayload(response)
				log.info "responseStr:{} $responseStr"
	}
}
