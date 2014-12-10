package nz.co.dav.imaging.model;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["tag","name","createTime"])
class ImageMetaModel implements Serializable{
	String tag
	String name
	String createTime
	String s3Prefix
	String s3Path
	Map<String,String> metaMap = [:]
}
