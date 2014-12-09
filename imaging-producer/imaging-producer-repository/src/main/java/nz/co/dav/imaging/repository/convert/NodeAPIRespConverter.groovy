package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function

@Slf4j
class NodeAPIRespConverter implements Function<String, Map<String,String>> {

	JsonSlurper jsonSlurper

	public NodeAPIRespConverter(final JsonSlurper jsonSlurper) {
		this.jsonSlurper = jsonSlurper;
	}

	@Override
	Map<String, String> apply(String jsonInput) {
		Map<String,Object> metaMap = (Map)jsonSlurper.parseText(jsonInput)
		def resultMap = [:]
		resultMap.put("nodeUri", metaMap.get('self'))
		resultMap.putAll((Map)metaMap.get('data'))
		return resultMap
	}
}
