package nz.co.dav.imaging.integration.ds

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import nz.co.dav.imaging.model.AbstractImageInfo

@ToString(includeNames = true, excludes=['imageBytes'])
@EqualsAndHashCode(includes=["imageInfo"])
class ImageProcessRequest {
	List<Map<String, String>> scalingConfigs = []
	Set<AbstractImageInfo> images = []
	String tags
	String s3Path
	String processTime
}
