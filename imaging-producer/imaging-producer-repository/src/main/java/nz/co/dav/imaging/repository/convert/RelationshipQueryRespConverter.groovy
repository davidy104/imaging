package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.inject.Inject

@Slf4j
class RelationshipQueryRespConverter implements Function<String, Map<String,Object>> {

	@Inject
	JsonSlurper jsonSlurper

	@Override
	Map<String, Object> apply(final String jsonResp) {
		return (Map<String, Object>)jsonSlurper.parseText(jsonResp)
	}
}
