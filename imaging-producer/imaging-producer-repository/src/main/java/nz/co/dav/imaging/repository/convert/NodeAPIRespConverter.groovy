package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.inject.Inject

@Slf4j
class NodeAPIRespConverter implements Function<String, Map<String,String>> {

	@Inject
	JsonSlurper jsonSlurper

	@Override
	Map<String, String> apply(String jsonInput) {
		Map<String,Object> metaMap = (Map)jsonSlurper.parseText(jsonInput)
		def resultMap = [:]
		resultMap.put("nodeUri", metaMap.get('self'))
		resultMap.putAll((Map)metaMap.get('data'))
		return resultMap
	}
}
