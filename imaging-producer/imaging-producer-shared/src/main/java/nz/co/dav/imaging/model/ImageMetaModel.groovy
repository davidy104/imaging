package nz.co.dav.imaging.model;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["tag","name"])
class ImageMetaModel {
	String tag
	String name
	Date createTime
	Map<String,String> meta = [:]
}
