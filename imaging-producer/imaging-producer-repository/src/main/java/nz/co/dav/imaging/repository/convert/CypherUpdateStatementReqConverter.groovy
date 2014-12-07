package nz.co.dav.imaging.repository.convert

import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.common.base.Joiner
@Slf4j
class CypherUpdateStatementReqConverter implements Function<Map<String,String>, String> {

	@Override
	String apply(Map<String, String> inputMap) {
		def list = []
		inputMap.each {k,v->
			list <<  "\"$k\":\"$v\""
		}
		return Joiner.on(",").join(list)
	}
}
