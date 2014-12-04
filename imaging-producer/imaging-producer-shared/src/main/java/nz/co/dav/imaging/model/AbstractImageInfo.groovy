package nz.co.dav.imaging.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["imageName"])
class AbstractImageInfo {
	byte[] imageBytes
	String imageName
	String extension
}
