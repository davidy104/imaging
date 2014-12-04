package nz.co.dav.imaging.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.SharedModule
import nz.co.dav.imaging.config.ConfigurationServiceModule
import nz.co.dav.imaging.integration.ImageCamelContextModule
import nz.co.dav.imaging.model.AbstractImageInfo
import nz.co.dav.imaging.model.ImageProcessRequest
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules

import org.apache.camel.CamelContext
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.io.Resources
import com.google.inject.Inject
@RunWith(GuiceJUnitRunner.class)
@GuiceModules([ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class ])
@Slf4j
class ImageProcessRouteIntegrationTest {

	static final String[] IMAGS = [
		"test01.JPG",
		"test02.JPG",
		"test03.JPG",
		"test04.JPG"
	]

	@Inject
	CamelContext camelContext

	ImageProcessRequest request
	
	@Produce(uri="direct:ImageProcess")
	private ProducerTemplate producerTemplate

	@Before
	void setUp(){
		request = new ImageProcessRequest(tags:'firstTest',s3Path:'image',processTime:'2014-12-04')
		IMAGS.eachWithIndex{obj,i->
			def name = "test"+i
			byte[] ibytes = Resources.toByteArray(Resources.getResource(obj))
			AbstractImageInfo imgInfo = new AbstractImageInfo(imageName:name,extension:'jpg',imageBytes:ibytes)
			request.images << imgInfo
		}
		request.scalingConfigs = [
			[name:'original'],
			[name:'standard',width:'1024',height:'1024'],
			[name:'thumbnail',width:'1217',height:'1217']
		]
		camelContext.start()
	}

	@After
	void tearDown(){
		camelContext.stop()
	}

	@Test
	public void testRoute() {
		String response = producerTemplate.requestBody(request, String.class)
		log.info "response:{} $response"
		Thread.sleep(20000)
	}
}
