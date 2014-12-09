package nz.co.dav.imaging.repository.convert

import groovy.util.logging.Slf4j

import org.apache.commons.lang3.StringEscapeUtils

import com.google.common.base.Function
import com.google.common.base.Joiner

@Slf4j
class CypherCreateStatementReqConverter implements Function<Map<String,Object>, String> {

	@Override
	String apply(final Map<String, Object> inputMap) {
		List list = []
//		def metaObj = inputMap['meta']
//		metaObj = org.codehaus.jettison.json.JSONObject.quote(metaObj)
//		log.info "metaObj:{} $metaObj"
		inputMap.each { k,v->
			String value = (String)v
			if(k == 'meta'){
				value = StringEscapeUtils.escapeJson(v).toString()
				log.info "metaObj:{} $v"
			}
			list << "$k:'$value'"
		}
		return Joiner.on(",").join(list)
	}
}
