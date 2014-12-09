package nz.co.dav.imaging.repository.convert

import groovy.util.logging.Slf4j

import java.util.regex.Matcher
import java.util.regex.Pattern

import com.google.common.base.Function
import com.google.common.base.Joiner

@Slf4j
class CypherCreateStatementReqConverter implements Function<Map<String,Object>, String> {
	String regEx = '[`~!@#$%^&*()+=|{}:;,//[//].<>/?~！@#￥%……&*（）——+|{}‘；：”“’。，、？]'
	@Override
	String apply(final Map<String, Object> inputMap) {
		List list = []
		inputMap.each { k,v->
			def vStr = (String)v
			Pattern p = Pattern.compile(regEx)
			Matcher m = p.matcher(vStr)
			vStr = m.replaceAll("").trim()
			vStr = vStr.replaceAll("'","")

			m = p.matcher(k)
			k = m.replaceAll("").trim()
			list << "$k:'$vStr'"
		}
		return Joiner.on(",").join(list)
	}
}
