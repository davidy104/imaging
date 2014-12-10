package nz.co.dav.imaging.ds.impl;

import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.model.AbstractImageInfo
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.model.ImageProcessRequest
import nz.co.dav.imaging.repository.ImagingMetaDataRepository

import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate

import com.google.common.base.Splitter
import com.google.inject.Inject

@Slf4j
class ImagingProcessDSImpl implements ImagingProcessDS {

	@Produce(uri="direct:ImageProcess")
	private ProducerTemplate producerTemplate

	static final String S3_IMG_PATH = 'image'

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

	@Inject
	ImagingMetaDataRepository imagingMetaDataRepository

	@Override
	String process(final String scalingConfig,final String tags, final Map<String, byte[]> imagesMap) {
		String processTime = DATE_FORMAT.format(new Date())
		log.info "processTime:{} $processTime"
		ImageProcessRequest imageProcessRequest = new ImageProcessRequest(s3Path:S3_IMG_PATH,tags:tags,processTime:processTime)
		imagesMap.each {k,v->
			imageProcessRequest.images << this.buildAbstractImage(k, v)
		}
		imageProcessRequest.scalingConfigs = this.buildScalingConfigMap(scalingConfig)
		return  producerTemplate.requestBody(imageProcessRequest, String.class)
	}

	//normal=1024*1024,stardand=1217*1217
	List<Map<String,String>> buildScalingConfigMap(final String scalingConfig){
		List<Map<String,String>> resultList = []
		Splitter.MapSplitter mapSplitter = Splitter.on(",").withKeyValueSeparator("=")
		mapSplitter.split(scalingConfig).each {k,v->
			def scalingConfigMap = [:]
			scalingConfigMap.put("name", k)
			Iterable<String> values = Splitter.on("*").split(v)
			scalingConfigMap.put("width", values[0])
			scalingConfigMap.put("height", values[1])
			resultList << scalingConfigMap
		}
//		resultList << [name:'original']
		return resultList
	}

	AbstractImageInfo buildAbstractImage(final String imageName,final byte[] imageBytes){
		return new AbstractImageInfo(imageBytes:imageBytes,imageName:imageName,extension:'jpg')
	}

	@Override
	ImageMetaModel getImageMetaDataByTagAndName(final String tag,final String name) {
		return imagingMetaDataRepository.getImageMetaDataByTagAndName(tag, name)
	}

	@Override
	List<ImageMetaModel> getAllImageMetaModel(final String tag)  {
		return imagingMetaDataRepository.getAllImageMetaModel(tag)
	}

	@Override
	void deleteAllImageMetaByTag(final String tag) {
		imagingMetaDataRepository.deleteAllImageMetaByTag(tag)
	}

}
