package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.inject.Inject
@Slf4j
class RelationshipsQueryRespConverter implements Function<String, Map<String,Map<String,String>>> {

	@Inject
	JsonSlurper jsonSlurper

	@Override
	Map<String, Map<String, String>> apply(final String jsonResp) {
		Map resultMap = [:]
		Object metaResult = jsonSlurper.parseText(jsonResp)
		if(metaResult instanceof Map){
			this.doConvert((Map)metaResult, resultMap)
		} else if(metaResult instanceof List){
			((List)metaResult).each{
				this.doConvert(it, resultMap)
			}
		}
		return resultMap
	}

	void doConvert(Map<String,Object> singleRelationshipMetaMap,Map resultMap){
		Map<String,String> dataMap = [:]
		String relationshipId = singleRelationshipMetaMap.get('self')
		dataMap.put("start", singleRelationshipMetaMap.get('start'))
		dataMap.put("end", singleRelationshipMetaMap.get('end'))
		dataMap.put("type", singleRelationshipMetaMap.get('type'))
		resultMap.put(relationshipId, dataMap)
	}
}
