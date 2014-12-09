package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function

@Slf4j
class Neo4jAPIErrorRespConverter implements Function<String, String> {
	JsonSlurper jsonSlurper


	public Neo4jAPIErrorRespConverter(JsonSlurper jsonSlurper) {
		this.jsonSlurper = jsonSlurper;
	}


	@Override
	String apply(final String jsonInput) {
		return ((Map)jsonSlurper.parseText(jsonInput)).get('message')
	}
}
