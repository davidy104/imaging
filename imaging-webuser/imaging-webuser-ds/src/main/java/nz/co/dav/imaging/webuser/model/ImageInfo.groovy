package nz.co.dav.imaging.webuser.model;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["tag","name","subjectLocation","createTime"])
class ImageInfo {
	String tag
	String name
	String makeAndModel
	String createTime
	String subjectLocation
	String imageUri
}
