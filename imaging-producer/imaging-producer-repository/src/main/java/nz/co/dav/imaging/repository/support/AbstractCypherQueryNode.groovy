package nz.co.dav.imaging.repository.support

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
@EqualsAndHashCode(includes=["uri","column"])
@ToString(includeNames = true, includeFields=true)
class AbstractCypherQueryNode {
	String uri
	String column
	Map<String,String> dataMap =[:]
	//key=column
	Map<String,Set<String>> relationDataMap = [:]
	Map<String,Set<AbstractCypherQueryNode>> relationAbstractCypherQueryNodes = [:]
}
