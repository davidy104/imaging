package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.inject.Inject

@Slf4j
class Neo4jAPIErrorRespConverter implements Function<String, String> {
	@Inject
	JsonSlurper jsonSlurper
	@Override
	String apply(final String jsonInput) {
		return ((Map)jsonSlurper.parseText(jsonInput)).get('message')
	}
}
