package nz.co.dav.imaging.repository.impl;

import static com.google.common.base.Preconditions.checkArgument
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.DuplicatedException
import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.repository.ImagingMetaDataRepository
import nz.co.dav.imaging.repository.Neo4jRepositoryBase
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import org.apache.commons.lang3.StringUtils

import com.google.common.base.Function
import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class ImagingMetaDataRepositoryImpl extends Neo4jRepositoryBase implements ImagingMetaDataRepository{

	@Inject
	@Named("imageMetaMapToModelConverter")
	Function<Map<String,String>, ImageMetaModel> imageMetaMapToModelConverter

	@Inject
	@Named("imageModelToMetaMapForCreationConverter")
	Function<ImageMetaModel, Function<Map<String, Object>, String>> imageModelToMetaMapForCreationConverter

	@Override
	String createImageMetaData(final ImageMetaModel imageMetaModel) {
		checkArgument(imageMetaModel!=null,"imageMetaModel can not be null.")
		def tag = imageMetaModel.tag
		def name = imageMetaModel.name
		checkArgument(!StringUtils.isEmpty(tag),"tag can not be null.")
		checkArgument(!StringUtils.isEmpty(name),"name can not be null.")

		boolean unique = false
		try {
			this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.name = '$name' And i.tag = '$tag' RETURN i \"}")
		} catch (final Exception e) {
			if(e instanceof NotFoundException){
				unique = true
			} else {
				throw e
			}
		}
		log.info "unique:{} $unique"
		if(!unique){
			throw new DuplicatedException('image with the same tag and name already existed.')
		}
		String addMetaJson = imageModelToMetaMapForCreationConverter.apply(imageMetaModel)
		log.info "addMetaJson:{} $addMetaJson"
		return neo4jRestAPIAccessor.createNodeFromCypherAPI("ImageMetaData", addMetaJson)
	}

	@Override
	ImageMetaModel getImageMetaDataByTagAndName(final String tag,final String name) {
		AbstractCypherQueryResult result = this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.name = '$name' And i.tag = '$tag' RETURN i \"}")
		Map<String,Map<String,String>> metaResultMap = result.nodeColumnMap.get("i")
		Map<String,String> resultMap = metaResultMap.values().first()
		return imageMetaMapToModelConverter.apply(resultMap)
	}

	@Override
	List<ImageMetaModel> getAllImageMetaModel(final String tag) {
		List<ImageMetaModel> resultList = []
		Map<String,Map<String,String>> metaResultMap = this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.tag = '$tag' RETURN i \"}").nodeColumnMap.get("i")
		metaResultMap.values().each {
			resultList << imageMetaMapToModelConverter.apply(it)
		}
		return resultList
	}

	@Override
	void deleteAllImageMetaByTag(final String tag) {
		checkArgument(!StringUtils.isEmpty(tag),"tag can not be null.")
		cypherUpdateOrDelete("{\"query\":\"MATCH (i:ImageMetaData{tag:{tag}}) DELETE i\",\"params\":{\"tag\":\"$tag\"}}")
	}
}
