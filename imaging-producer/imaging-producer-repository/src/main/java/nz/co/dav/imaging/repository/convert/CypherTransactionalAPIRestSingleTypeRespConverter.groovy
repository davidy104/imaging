package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import com.google.common.base.Function
import com.google.inject.Inject
/**
 * 
 * @author Davidy
 * resultDataContents:['REST'] should be into request content
 */
@Slf4j
class CypherTransactionalAPIRestSingleTypeRespConverter implements Function<String,Map<String,Map<String,String>>> {

	@Inject
	JsonSlurper jsonSlurper

	@Override
	Map<String,Map<String,String>> apply(final String jsonInput) {
		Map<String,Map<String,String>> resultMap = [:]
		Map respMap = (Map)jsonSlurper.parseText(jsonInput)
		List errorList = (ArrayList)respMap.get('errors')
		if(errorList){
			Map errorMap = (Map)(errorList.get(0))
			String errorMessage = "${errorMap.get('code')}|${errorMap.get('message')}"
			throw new RuntimeException(errorMessage)
		}

		List results = (ArrayList)respMap.get('results')

		results.each{
			Map resultsMap = (Map)it
			Map singleResultMap = (Map)((ArrayList)((Map)((ArrayList)resultsMap.get('data')).get(0)).get('rest')).get(0)
			Map dataMap = (Map)singleResultMap.get('data')
			resultMap.put(singleResultMap.get('self'), dataMap)
		}
		return resultMap
	}
}
