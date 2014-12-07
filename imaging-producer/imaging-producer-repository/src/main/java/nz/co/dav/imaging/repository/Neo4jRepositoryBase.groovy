package nz.co.dav.imaging.repository

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull
import static nz.co.dav.imaging.util.JerseyClientUtil.getResponsePayload
import groovy.util.logging.Slf4j

import javax.ws.rs.core.Response.Status

import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.repository.convert.CypherCreateStatementReqConverter
import nz.co.dav.imaging.repository.convert.CypherInPredicateConverter
import nz.co.dav.imaging.repository.convert.CypherUpdateStatementReqConverter
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult
import nz.co.dav.imaging.repository.support.Neo4jRestAPIAccessor
import nz.co.dav.imaging.repository.support.Neo4jRestAPIAccessor.RelationshipDirection

import org.apache.commons.lang3.StringUtils

import com.google.common.base.Function
import com.google.inject.Inject
import com.google.inject.name.Named
import com.sun.jersey.api.client.ClientResponse

@Slf4j
class Neo4jRepositoryBase implements GeneralNeo4jRepository {

	@Inject
	Neo4jRestAPIAccessor neo4jRestAPIAccessor

	@Inject
	@Named("cypherCreateStatmentReqConverter")
	CypherCreateStatementReqConverter cypherCreateStatmentReqConverter

	@Inject
	@Named("cypherUpdateStatementReqConverter")
	CypherUpdateStatementReqConverter cypherUpdateStatementReqConverter

	@Inject
	@Named("cypherInPredicateConverter")
	CypherInPredicateConverter cypherInPredicateConverter

	@Override
	Object customCypherQuery(final String queryJson,final int expectedStatusCode, final Function<String,?> converter) {
		ClientResponse clientResponse = neo4jRestAPIAccessor.doRestCypherQuery(queryJson)
		String responseStr = getResponsePayload(clientResponse)
		if(converter == null){
			return responseStr
		}
		return converter.apply(responseStr)
	}

	@Override
	void deleteNodeByNodeUri(final String nodeUri) {
		checkArgument(!StringUtils.isEmpty(nodeUri),"nodeUri can not be null")
		neo4jRestAPIAccessor.deleteNodeFromNodeAPIByUri(nodeUri)
	}

	@Override
	Map<String, String> getNodeByNodeUri(final String nodeUri) {
		checkArgument(!StringUtils.isEmpty(nodeUri),"nodeUri can not be null")
		return neo4jRestAPIAccessor.getNodeFromNodeAPIByUri(nodeUri)
	}

	protected String createUniqueNode(final String nodeLabel,final String createStatement,final String key,final String value){
		def self
		try {
			self = neo4jRestAPIAccessor.createNodeFromCypherAPI(nodeLabel, createStatement)
			checkNotNull(self,"createdNodeUri can not be null.")
			neo4jRestAPIAccessor.buildUnique(key, value, self)
		} catch (e) {
			if(self){
				neo4jRestAPIAccessor.deleteNodeFromNodeAPIByUri(self)
			}
			throw e
		}
		return self
	}
	protected AbstractCypherQueryResult cypherQuery(final String queryJson){
		this.cypherQuery(queryJson, null)
	}

	protected AbstractCypherQueryResult cypherQuery(final String queryJson,final String distinctColumn){
		return neo4jRestAPIAccessor.doCypherQuery(queryJson, Status.OK.code, true,distinctColumn)
	}

	protected void cypherUpdateOrDelete(final String queryJson){
		neo4jRestAPIAccessor.doCypherQuery(queryJson, Status.OK.code, false,null)
	}

	void deleteRelationship(final String nodeUri,final String relationshipNodeUri, final String type, RelationshipDirection relationshipDirection){
		def relationshipUri
		if(!relationshipDirection){
			relationshipDirection =RelationshipDirection.ALL
		}

		Map<String,Map<String,String>> resultMap = neo4jRestAPIAccessor.getRelationsByNodeId(nodeUri,relationshipDirection,type)
		resultMap.any {
			Map<String,String> dataMap = it.value
			if(dataMap.get('start')==nodeUri && dataMap.get('end') == relationshipNodeUri && (dataMap.get('type')).toLowerCase() == type.toLowerCase()){
				relationshipUri = it.key
				return true
			}
		}
		if(!relationshipUri){
			throw new NotFoundException("Relationship not found.")
		}
		neo4jRestAPIAccessor.deleteRelationship(relationshipUri)
	}

}
