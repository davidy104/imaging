package nz.co.dav.imaging.ds.impl;

import static com.google.common.base.Preconditions.checkArgument
import groovy.util.logging.Slf4j

import java.text.SimpleDateFormat

import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.model.AbstractImageInfo
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.model.ImageProcessRequest
import nz.co.dav.imaging.model.Page
import nz.co.dav.imaging.repository.ImagingMetaDataRepository
import nz.co.dav.imaging.repository.support.AbstractCypherQueryNode
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.commons.lang3.StringUtils

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.google.common.base.Function
import com.google.common.base.Splitter
import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class ImagingProcessDSImpl implements ImagingProcessDS {

	@Produce(uri="direct:ImageProcess")
	private ProducerTemplate producerTemplate

	static final String S3_IMG_PATH = 'image'

	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

	@Inject
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket

	@Inject
	AmazonS3 amazonS3

	@Inject
	ImagingMetaDataRepository imagingMetaDataRepository

	@Inject
	@Named("imageMetaMapToModelConverter")
	Function<Map<String,String>, ImageMetaModel> imageMetaMapToModelConverter

	static final String IMAGE_SCALINGS_DELIMITER =":"

	@Override
	String process(final String scalingConfig,final String tag, final Map<String, byte[]> imagesMap) {
		String processTime = DATE_FORMAT.format(new Date())
		ImageProcessRequest imageProcessRequest = new ImageProcessRequest(s3Path:S3_IMG_PATH,tag:tag,processTime:processTime)
		imagesMap.each {k,v->
			imageProcessRequest.images << new AbstractImageInfo(imageBytes:v,imageName:k,extension:'jpg')
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

	@Override
	ImageMetaModel getImageMetaDataByTagAndName(final String tag,final String name) {
		Map<String,String> resultMap = imagingMetaDataRepository.getImageMetaDataByTagAndName(tag, name)
		return imageMetaMapToModelConverter.apply(resultMap)
	}

	@Override
	List<ImageMetaModel> getImageMetaData(final String tag)  {
		List<ImageMetaModel> resultList = []
		Map<String,Map<String,String>> imageMetaDataMap = imagingMetaDataRepository.getImageMetaData(tag)
		imageMetaDataMap.values().each {
			resultList << imageMetaMapToModelConverter.apply(it)
		}
		return resultList
	}

	@Override
	void deleteImage(final String tag) {
		checkArgument(!StringUtils.isEmpty(tag),"tag can not be null.")
		def key = S3_IMG_PATH +"/"+ tag
		amazonS3.deleteObject(awsS3Bucket, key)

		this.getImageMetaData(tag).each {
			def s3keies = it.s3Path
			if(s3keies){
				Iterable<String> values = Splitter.on(IMAGE_SCALINGS_DELIMITER).split(s3keies)
				values.each {s3key->
					amazonS3.deleteObject(awsS3Bucket, s3key)
				}
			}
		}
		imagingMetaDataRepository.deleteImage(tag)
	}

	@Override
	void deleteImage(final String tag,final String name)  {
		checkArgument(!StringUtils.isEmpty(tag),"tag can not be null.")
		checkArgument(!StringUtils.isEmpty(name),"name can not be null.")

		Map<String,String> resultMap = imagingMetaDataRepository.getImageMetaDataByTagAndName(tag, name)
		String imagesS3keies = resultMap['imagesS3Keis']
		String nodeUri = resultMap['nodeUri']
		String[] keies = imagesS3keies.split(":")

		keies.each {
			amazonS3.deleteObject(awsS3Bucket, it)
		}
		imagingMetaDataRepository.deleteImageByNodeUri(nodeUri)
	}

	@Override
	public S3Object getImage(final String tag,final String name, final String scalingType) {
		ImageMetaModel found = this.getImageMetaDataByTagAndName(tag, name)
		if(found){
			def s3keies = found.s3Path
			log.info "s3keies:{} $s3keies"
			def s3Path
			if(s3keies){
				Iterable<String> values = Splitter.on(IMAGE_SCALINGS_DELIMITER).split(s3keies)
				if(scalingType){
					values.any {
						if(it.contains(scalingType)){
							s3Path = it
							return true
						}
					}
				} else {
					s3Path = values.first()
				}
			}
			log.info "s3Path:{} $s3Path"
			if(s3Path){
				return amazonS3.getObject(new GetObjectRequest(awsS3Bucket, s3Path))
			}
		}
		return null
	}


	@Override
	Page paginate(final Integer pageOffset,final Integer pageSize,final String tag) {
		Page page = imagingMetaDataRepository.paginateImage(pageOffset, pageSize, tag)
		log.info "page:{} $page"
		AbstractCypherQueryResult abstractCypherQueryResult = page.metaContent
		Set<AbstractCypherQueryNode> nodes = abstractCypherQueryResult.distinctNodes
		nodes.each {
			page.content << imageMetaMapToModelConverter.apply(it.dataMap)
		}
		return page
	}

}
