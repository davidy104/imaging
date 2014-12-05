package nz.co.dav.imaging.model

import groovy.transform.ToString

@ToString(includeNames = true, excludes=['imageBytes'])
class ImageProcessRequest {
	List<Map<String, String>> scalingConfigs = []
	Set<AbstractImageInfo> images = []
	String tags
	String s3Path
	String processTime
}
