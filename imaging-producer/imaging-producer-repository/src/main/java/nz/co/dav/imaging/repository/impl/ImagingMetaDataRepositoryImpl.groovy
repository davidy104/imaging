package nz.co.dav.imaging.repository.impl;

import static com.google.common.base.Preconditions.checkArgument
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.DuplicatedException
import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.model.Page
import nz.co.dav.imaging.repository.ImagingMetaDataRepository
import nz.co.dav.imaging.repository.Neo4jRepositoryBase
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import org.apache.commons.lang3.StringUtils

import com.google.common.base.Function

@Slf4j
class ImagingMetaDataRepositoryImpl extends Neo4jRepositoryBase implements ImagingMetaDataRepository{

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
		if(!unique){
			throw new DuplicatedException('image with the same tag and name already existed.')
		}
		String addMetaJson = this.cypherCreateStatmentReqConverter.apply(imageMetaModel.metaMap)
		log.debug "addMetaJson:{} $addMetaJson"
		return neo4jRestAPIAccessor.createNodeFromCypherAPI("ImageMetaData", addMetaJson)
	}

	@Override
	Map<String,String> getImageMetaDataByTagAndName(final String tag,final String name) {
		AbstractCypherQueryResult result = this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.name = '$name' And i.tag = '$tag' RETURN i \"}")
		Map<String,Map<String,String>> metaResultMap = result.nodeColumnMap.get("i")
		String nodeUri = metaResultMap.keySet().first()
		Map<String,String> resultMap = metaResultMap.values().first()
		resultMap.put('nodeUri', nodeUri)
		return resultMap
	}

	@Override
	void deleteImageByNodeUri(final String nodeUri)  {
		this.deleteNodeByNodeUri(nodeUri)
	}

	@Override
	Map<String,Map<String,String>> getImageMetaData(final String tag) {
		return this.cypherQuery("{\"query\":\"MATCH (i:ImageMetaData) WHERE i.tag = '$tag' RETURN i \"}").nodeColumnMap.get("i")
	}

	@Override
	void deleteImage(final String tag) {
		cypherUpdateOrDelete("{\"query\":\"MATCH (i:ImageMetaData{tag:{tag}}) DELETE i\",\"params\":{\"tag\":\"$tag\"}}")
	}

	@Override
	void deleteImage(final String tag,final String name) {
		cypherUpdateOrDelete("{\"query\":\"MATCH (i:ImageMetaData{tag:{tag},name:{name}}) DELETE i\",\"params\":{\"tag\":\"$tag\",\"name\":\"$name\"}}")
	}

	//distinctNodes
	@Override
	Page paginateImage(Integer pageOffset, Integer pageSize, String tag)  {
		String queryJson = "MATCH (i:ImageMetaData)"
		if(tag){
			queryJson = "${queryJson} WHERE i.tag = '$tag'"
		}
		queryJson = "${queryJson} RETURN i"
		log.info "queryJson:{} $queryJson"
		return this.paginate(queryJson, pageOffset, pageSize, "i")
	}
}
