package nz.co.dav.imaging.repository;

import com.google.common.base.Function

interface GeneralNeo4jRepository {
	def customCypherQuery(String queryJson,int expectedStatusCode, Function<String,?> converter)
	Map<String,String> getNodeByNodeUri(String nodeUri)
	void deleteNodeByNodeUri(String nodeUri)
}
