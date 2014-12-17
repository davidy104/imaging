package nz.co.dav.imaging.model

import groovy.transform.ToString

import com.fasterxml.jackson.annotation.JsonIgnore

@ToString(includeNames = true, includeFields=true)
class Page {
	long totalCount
	int totalPages
	List content =[]
	@JsonIgnore
	def metaContent
}
