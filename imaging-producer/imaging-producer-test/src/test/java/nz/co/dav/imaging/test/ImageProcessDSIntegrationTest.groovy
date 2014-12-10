package nz.co.dav.imaging.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.SharedModule
import nz.co.dav.imaging.config.ConfigurationServiceModule
import nz.co.dav.imaging.ds.ImagingDSModule
import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.integration.ImageCamelContextModule
import nz.co.dav.imaging.repository.ImagingRepositoryModule
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules

import org.apache.camel.CamelContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import com.google.common.io.Resources
import com.google.inject.Inject
@RunWith(GuiceJUnitRunner.class)
@GuiceModules([ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class,ImagingDSModule.class,ImagingRepositoryModule.class ])
@Slf4j
class ImageProcessDSIntegrationTest {

	@Inject
	ImagingProcessDS imagingProcessDS

	@Inject
	CamelContext camelContext

	static final String[] IMAGS = ["test01.JPG", "test02.JPG"]

	Map<String,byte[]> imagesMap = [:]

	@Before
	void setUp(){
		IMAGS.eachWithIndex  {obj,i->
			def imageName = "test-$i"
			imagesMap.put(imageName, Resources.toByteArray(Resources.getResource(obj)))
		}
		camelContext.start()
	}

	@After
	void tearDown(){
		camelContext.stop()
	}

	@Test
	public void test() {
		String scalingConfig = "standard=1024*1024,thumbnail=1217*1217"
		String tags = "officeTags01"
		def response = imagingProcessDS.process(scalingConfig, tags, imagesMap)
		log.info "imgmeta--------------------------:{} $response"
	}
}
