package nz.co.dav.imaging.integration.event;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["s3Key"])
class ImageSentToS3Event {
	String s3Key
}
