package nz.co.dav.imaging.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, excludes=['imageBytes'])
@EqualsAndHashCode(includes=["imageInfo"])
class ImageProcessRequest {
	List<Map<String, String>> scalingConfigs = []
	Set<AbstractImageInfo> images = []
	String tags
	String s3Path
	String sqsName
	String processTime
}
