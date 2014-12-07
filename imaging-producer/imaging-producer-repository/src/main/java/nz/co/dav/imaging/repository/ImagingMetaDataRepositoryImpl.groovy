package nz.co.dav.imaging.repository;

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.ImageMetaModel

@Slf4j
class ImagingMetaDataRepositoryImpl extends Neo4jRepositoryBase implements ImagingMetaDataRepository{

	@Override
	String createImageMetaData(final Map<String,String> metaMap) {
		String addMetaJson = cypherCreateStatmentReqConverter.apply(metaMap)
		return neo4jRestAPIAccessor.createNodeFromCypherAPI("ImageMetaData", addMetaJson)
	}

	@Override
	ImageMetaModel getImageMetaDataByTagAndName(String tag, String name) {
		return null
	}

	@Override
	List<ImageMetaModel> getAllImageMetaModel(String tag) {
		List<ImageMetaModel> resultList = []
		return resultList
	}
}
