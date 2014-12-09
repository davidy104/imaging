package nz.co.dav.imaging.repository.convert

import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.common.base.Joiner

@Slf4j
class CypherCreateStatementReqConverter implements Function<Map<String,Object>, String> {
	@Override
	String apply(final Map<String, Object> inputMap) {
		List list = []
		inputMap.each { k,v->
			def vStr = (String)v
			vStr = vStr.replaceAll("'", "")
			list << "$k:'$vStr'"
		}
		return Joiner.on(",").join(list)
	}
}
