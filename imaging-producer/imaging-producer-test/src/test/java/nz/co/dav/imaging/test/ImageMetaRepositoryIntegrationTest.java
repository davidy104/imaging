package nz.co.dav.imaging.test;

import nz.co.dav.imaging.SharedModule;
import nz.co.dav.imaging.config.ConfigurationServiceModule;
import nz.co.dav.imaging.ds.ImagingDSModule;
import nz.co.dav.imaging.integration.ImageCamelContextModule;
import nz.co.dav.imaging.repository.ImagingMetaDataRepository;
import nz.co.dav.imaging.repository.ImagingRepositoryModule;
import nz.co.dav.imaging.test.GuiceJUnitRunner.GuiceModules;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ ConfigurationServiceModule.class, SharedModule.class, ImageCamelContextModule.class, ImagingDSModule.class, ImagingRepositoryModule.class })
// @Slf4j
public class ImageMetaRepositoryIntegrationTest {

	@Inject
	private ImagingMetaDataRepository imagingMetaDataRepository;

	private static final String TEST_TAG = "officeTags01";
	private static final String TEST_NAME = "test01";

	@Test
	public void testCRD() {
		// def metaMap = [Make:'Apple', Model:'iPhone 5s', Orientation:1,
		// XResolution:72, YResolution:72, InteropIndex:'S', tag:TEST_TAG,
		// name:TEST_NAME,createTime:'2014:03:14 12:23:22']
		// String nodeUri =
		// imagingMetaDataRepository.createImageMetaData(metaMap)
		//
		// ImageMetaModel foundModel =
		// imagingMetaDataRepository.getImageMetaDataByTagAndName(TEST_TAG,
		// TEST_NAME)
		// log.info "foundModel:{} $foundModel"
		//
		// assertNotNull(foundModel)
		// imagingMetaDataRepository.deleteAllImageMetaByTag(TEST_TAG)
	}

	@Test
	public void testStatement() throws Exception {

		String statement = "Make:'Apple',Model:'iPhone 5s',Orientation:'1',XResolution:'72',YResolution:'72',ResolutionUnit:'2'"
				+ ",Software:'7.0.6'"
				+ ",ModifyDate:'2014:11:30 13:07:19',YCbCrPositioning:'1',ExifOffset:'204', GPSInfo:'1518'"
				+ ",ExposureTime:'1\\/1866 (0.001)'"
				+ ",FNumber:'11\\/5 (2.2)'"
				+ ",ExposureProgram:'2',ISO:'32'"
				+ ",ExifVersion:'48, 50, 50, 49'"
				+ ",DateTimeOriginal:'2014:11:30 13:07:19'"
				+ ",CreateDate:'2014:11:30 13:07:19'"
				+ ",ComponentsConfiguration:'1, 2, 3, 0'"
				+ ",ShutterSpeedValue:'10985\\/1011 (10.865)'"
				+ ",ApertureValue:'7983\\/3509 (2.275)'"
				+ ",BrightnessValue:'43675\\/4416 (9.89)'"
				+ ",MeteringMode:'5',Flash:'16',FocalLength:'103\\/25 (4.12)'"
				+ ",SubjectLocation:'1630, 1220, 832, 499'"
				+ ",MakerNote:'65, 112, 112, 108, 101, 32, 105, 79, 83, 0, 0, 1, 77, 77, 0, 7, 0, 1, 0, 9, 0, 0, 0, 1, 0, 0, 0, 0, 0, 2, 0, 7, 0, 0, 2, 46, 0, 0, 0, 104, 0, 3, 0, 7, 0, 0, 0, 104, 0, 0, 2... (766)'"
				+ ",SubSecTimeOriginal:'775',SubSecTimeDigitized:'775'"
				+ ",FlashpixVersion:'48, 49, 48, 48',ColorSpace:'1'"
				+ ",ExifImageWidth:'3264',ExifImageLength:'2448'"
				+ ",SensingMethod:'2',SceneType:'1',ExposureMode:'0'"
				+ ",WhiteBalance:'0',FocalLengthIn35mmFormat:'30',SceneCaptureType:'0'"
				+ ",InteropIndex:'S',InteropVersion:'36, 51, 532\\/100 (5.32)'"
				+ ",Compression:'6',JpgFromRawStart:'1814'"
				+ ",JpgFromRawLength:'11984',tag:'officeTags01',name:'test-0'"
				+ ",processTime:'2014-12-10 11:28:55'";

		// String nodeUri =
		// imagingMetaDataRepository.createImageMetaData(statement);
		// System.out.println("nodeUri:{} " + nodeUri);
	}

}
