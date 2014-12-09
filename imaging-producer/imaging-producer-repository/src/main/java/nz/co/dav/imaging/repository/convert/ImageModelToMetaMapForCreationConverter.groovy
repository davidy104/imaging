package nz.co.dav.imaging.repository.convert

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.ImageMetaModel

import com.google.common.base.Function

@Slf4j
class ImageModelToMetaMapForCreationConverter implements Function<ImageMetaModel, Function<Map<String,Object>, String>> {

	Function<Map<String,Object>, String> cypherCreateStatementReqConverter

	public ImageModelToMetaMapForCreationConverter(final Function<Map<String,Object>, String> cypherCreateStatementReqConverter) {
		this.cypherCreateStatementReqConverter = cypherCreateStatementReqConverter
	}

	@Override
	public Function<Map<String, Object>, String> apply(final ImageMetaModel imageMetaModel) {
		Map<String,Object> imageMetaMap = [:]
		imageMetaMap.put("tag", imageMetaModel.tag)
		imageMetaMap.put("name", imageMetaModel.name)
		imageMetaMap.put("meta", imageMetaModel.meta)
		imageMetaMap.put("createTime", imageMetaModel.createTime)
		log.info "imageMetaMap:{} $imageMetaMap"
		return cypherCreateStatementReqConverter.apply(imageMetaMap)
	}
}
