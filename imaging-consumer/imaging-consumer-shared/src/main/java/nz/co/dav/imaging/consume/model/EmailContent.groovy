package nz.co.dav.imaging.consume.model

import groovy.transform.ToString
@ToString(includeNames = true, includeFields=true)
class EmailContent {
	String toWho = ""
	String[] contentDetails=['']
	String signature = ""
}
