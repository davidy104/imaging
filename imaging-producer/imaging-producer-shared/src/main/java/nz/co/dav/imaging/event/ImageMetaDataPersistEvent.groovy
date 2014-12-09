package nz.co.dav.imaging.event

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class ImageMetaDataPersistEvent {
	String imageMataDataJson
}
