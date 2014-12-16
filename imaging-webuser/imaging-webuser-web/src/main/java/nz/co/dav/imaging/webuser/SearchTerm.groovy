package nz.co.dav.imaging.webuser

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["tag","name","scalingType"])
class SearchTerm {
	String tag
	String name
	String scalingType
}
