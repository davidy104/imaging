package nz.co.dav.imaging.consume.integration.processor

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import org.apache.camel.Exchange
import org.apache.camel.Expression

import com.google.common.base.Splitter

@Slf4j
class GetImageMetaResponseTransformer implements Expression{

	JsonSlurper jsonSlurper

	final static String S3PATH_KEY = "s3Path"
	final static String TAG_KEY="tag"
	final static String IMAGE_SCALINGS_DELIMITER =":"
	final static String PROCESS_SCALING_TYPE="thumbnail"

	public GetImageMetaResponseTransformer(final JsonSlurper jsonSlurper) {
		this.jsonSlurper = jsonSlurper;
	}

	@Override
	public <T> T evaluate(Exchange exchange, Class<T> type) {
		String jsonResponse = exchange.getIn().getBody(String.class)
		Set<String> needFetchImagePathSet = []
		List<Object> resultList = (List)jsonSlurper.parseText(jsonResponse)
		resultList.each {
			Map<String,Object> resultMap = (Map)it
			def s3keies = resultMap[S3PATH_KEY]
			String name = resultMap['name']
			println "s3keies:{} $s3keies"
			println "name:{} $name"
			Iterable<String> values = Splitter.on(IMAGE_SCALINGS_DELIMITER).split(s3keies)
			values.iterator().any {scalingImage->
				String selectScalingImageName = scalingImage.substring(scalingImage.lastIndexOf("/")+1, scalingImage.lastIndexOf("."))
				String matchImageName = name+"-"+PROCESS_SCALING_TYPE
				if(matchImageName.equals(selectScalingImageName)){
					needFetchImagePathSet << scalingImage
					true
				}
			}
		}
		return (T)needFetchImagePathSet
	}

}
