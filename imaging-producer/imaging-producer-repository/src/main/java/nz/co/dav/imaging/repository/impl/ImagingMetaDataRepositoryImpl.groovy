package nz.co.dav.imaging.repository.impl;

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.repository.ImagingMetaDataRepository
import nz.co.dav.imaging.repository.Neo4jRepositoryBase
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import com.google.common.base.Function
import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class ImagingMetaDataRepositoryImpl extends Neo4jRepositoryBase implements ImagingMetaDataRepository{

	@Inject
	@Named("imageMetaMapToModelConverter")
	Function<Map<String,String>, ImageMetaModel> imageMetaMapToModelConverter

	@Override
	String createImageMetaData(final Map<String,String> metaMap) {
		String addMetaJson = cypherCreateStatmentReqConverter.apply(metaMap)
		log.info "addMetaJson:{} $addMetaJson"
		def nodeUri = neo4jRestAPIAccessor.createNodeFromCypherAPI("ImageMetaData", addMetaJson)
		log.info "nodeUri:{} $nodeUri"
		return nodeUri
	}

	@Override
	ImageMetaModel getImageMetaDataByTagAndName(String tag, String name) {
		AbstractCypherQueryResult result = this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.name = '$name' And i.tag = '$tag' RETURN i \"}")
		Map<String,Map<String,String>> metaResultMap = result.nodeColumnMap.get("i")
		Map<String,String> resultMap = metaResultMap.values().first()
		return imageMetaMapToModelConverter.apply(resultMap)
	}

	@Override
	List<ImageMetaModel> getAllImageMetaModel(String tag) {
		List<ImageMetaModel> resultList = []
		Map<String,Map<String,String>> metaResultMap = this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.tag = '$tag' RETURN i \"}").nodeColumnMap.get("i")
		metaResultMap.values().each {
			resultList << imageMetaMapToModelConverter.apply(it)
		}
		return resultList
	}

	@Override
	void deleteAllImageMetaByTag(final String tag) {
		cypherUpdateOrDelete("{\"query\":\"MATCH (i:ImageMetaData{tag:{tag}}) DELETE i\",\"params\":{\"tag\":\"$tag\"}}")
	}
}
