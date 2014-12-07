package nz.co.dav.imaging.repository.support

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class AbstractCypherQueryResult {
	//key=column
	Map<String,Set<String>> dataColumnMap = [:]
	//key=column,key=uri,key=field
	Map<String,Map<String,Map<String,String>>> nodeColumnMap = [:]
	Set<AbstractCypherQueryNode> distinctNodes = []
}
