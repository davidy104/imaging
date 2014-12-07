package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class Neo4jRestAPIRespJsonConverter {

	@Inject
	JsonSlurper jsonSlurper

	@Inject
	@Named("nodeAPIRespConverter")
	NodeAPIRespConverter nodeAPIRespConverter

	@Inject
	@Named("relationshipsQueryRespConverter")
	RelationshipsQueryRespConverter relationshipsQueryRespConverter

	@Inject
	@Named("cypherTransactionalAPIRestSingleTypeRespConverter")
	CypherTransactionalAPIRestSingleTypeRespConverter cypherTransactionalAPIRestSingleTypeRespConverter

	@Inject
	@Named("relationshipQueryRespConverter")
	RelationshipQueryRespConverter relationshipQueryRespConverter

	@Inject
	@Named("cypherAPIQuerySuccessRespConverter")
	CypherAPIQuerySuccessRespConverter cypherAPIQuerySuccessRespConverter

	@Inject
	@Named("cypherUpdateStatementReqConverter")
	CypherUpdateStatementReqConverter cypherUpdateStatementReqConverter

	@Inject
	@Named("neo4jAPIErrorRespConverter")
	Neo4jAPIErrorRespConverter neo4jAPIErrorRespConverter

	String getCypherUpdateStatementRequest(final Map<String,String> inputMap){
		return cypherUpdateStatementReqConverter.apply(inputMap)
	}

	Map<String,Map<String,String>> getNodesWithSingleTypeFromTransactionalAPIRestFormat(final String jsonResponse){
		return cypherTransactionalAPIRestSingleTypeRespConverter.apply(jsonResponse)
	}

	String getNodesUriFromTransactionalWithRestFormat(final String jsonResponse){
		Map<String,Map<String,String>> resultMap = getNodesWithSingleTypeFromTransactionalAPIRestFormat(jsonResponse)
		return resultMap.keySet().first()
	}

	AbstractCypherQueryResult getCypherQueryResult(final String jsonResponse, final String distinctNodeColumn){
		return cypherAPIQuerySuccessRespConverter.apply(jsonResponse,distinctNodeColumn)
	}

	Map<String,Map<String,String>> getRelationsFromRelationsAPI(final String jsonResponse){
		return relationshipsQueryRespConverter.apply(jsonResponse)
	}

	Map<String,Object> getRelationFromRelationsAPI(final String jsonResponse){
		return relationshipQueryRespConverter.apply(jsonResponse)
	}

	Map<String, String> getNodeDataFromNodeAPI(final String jsonResponse){
		return nodeAPIRespConverter.apply(jsonResponse)
	}

	String getConflictNodeUriWhenCreateUniqueNode(final String jsonResponse){
		Map jsonResult = (Map)jsonSlurper.parseText(jsonResponse)
		return jsonResult.get('indexed')
	}

	String getErrorMessageFromAPI(final String jsonResponse){
		return this.neo4jAPIErrorRespConverter.apply(jsonResponse)
	}
}
