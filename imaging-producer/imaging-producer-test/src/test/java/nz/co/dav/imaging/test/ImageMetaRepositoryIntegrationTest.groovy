package nz.co.dav.imaging.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.SharedModule
import nz.co.dav.imaging.config.ConfigurationServiceModule
import nz.co.dav.imaging.ds.ImagingDSModule
import nz.co.dav.imaging.integration.ImageCamelContextModule
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.repository.ImagingMetaDataRepository
import nz.co.dav.imaging.repository.ImagingRepositoryModule
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules

import org.junit.Test
import org.junit.runner.RunWith

import com.google.inject.Inject

@RunWith(GuiceJUnitRunner.class)
@GuiceModules([ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class,ImagingDSModule.class,ImagingRepositoryModule.class ])
@Slf4j
class ImageMetaRepositoryIntegrationTest {

	@Inject
	ImagingMetaDataRepository imagingMetaDataRepository
	
	static final String TEST_TAG='officeTags01'
	static final String TEST_NAME='test01'
	
	@Test
	public void testCRD() {
		def metaMap = [Make:'Apple', Model:'iPhone 5s', Orientation:1, XResolution:72, YResolution:72, InteropIndex:'S', tag:TEST_TAG, name:TEST_NAME,createTime:'2014:03:14 12:23:22']
		String nodeUri = imagingMetaDataRepository.createImageMetaData(metaMap)
		
		ImageMetaModel foundModel = imagingMetaDataRepository.getImageMetaDataByTagAndName(TEST_TAG, TEST_NAME)
		log.info "foundModel:{} $foundModel"
		
		assertNotNull(foundModel)
		imagingMetaDataRepository.deleteAllImageMetaByTag(TEST_TAG)
	}

}
