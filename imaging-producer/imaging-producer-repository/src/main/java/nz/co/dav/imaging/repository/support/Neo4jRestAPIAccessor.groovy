package nz.co.dav.imaging.repository.support

import static com.google.common.base.Preconditions.checkNotNull
import static com.google.common.base.Preconditions.checkState
import static nz.co.dav.imaging.util.JerseyClientUtil.getResponsePayload
import groovy.util.logging.Slf4j

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status

import nz.co.dav.imaging.ConflictException
import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.repository.convert.Neo4jRestAPIRespJsonConverter

import org.apache.commons.lang3.ArrayUtils

import com.google.common.base.Joiner
import com.google.inject.Inject
import com.google.inject.name.Named
import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource

@Slf4j
class Neo4jRestAPIAccessor {

	@Inject
	Client jerseyClient

	@Inject
	@Named("NEO4J.HOST_URI")
	String neo4jHostUri

	@Inject
	Neo4jRestAPIRespJsonConverter neo4jRestAPIRespJsonConverter

	enum RelationshipDirection{
		NONE, IN, ALL,OUT
	}

	void deleteRelationship(final String relationUri){
		WebResource webResource = jerseyClient.resource(relationUri)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class)
		checkState(response.getStatus() == Status.NO_CONTENT.code,'delete relationship fail.')
	}

	Map<String,Object> getRelationByRelationNodeUri(final String relationshipNodeUri){
		WebResource webResource = jerseyClient.resource(relationshipNodeUri)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class)

		String respStr =getResponsePayload(response)
		checkState(response.getStatus() == Status.OK.code,'get relationships fail.')
		return neo4jRestAPIRespJsonConverter.getRelationFromRelationsAPI(respStr)
	}

	Map<String,Map<String,String>> getRelationsByNodeId(final String nodeUri, RelationshipDirection direction, final String... types){
		checkNotNull(direction,"direction can not be null")
		if(direction == RelationshipDirection.NONE && ArrayUtils.isEmpty(types)){
			throw new RuntimeException("types can not be empty when relationship direction is none")
		}
		StringBuilder builder
		if(direction != RelationshipDirection.NONE){
			builder = new StringBuilder("/").append(direction.name().toLowerCase())
		}
		if(types){
			if(!builder){
				builder = new StringBuilder("/")
			}else {
				builder.append("/")
			}
			builder = Joiner.on("&").appendTo(builder, types)
		}
		WebResource webResource = jerseyClient.resource(nodeUri).path("/relationships").path(builder.toString())
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class)

		String respStr =getResponsePayload(response)
		checkState(response.getStatus() == Status.OK.code,'get relationships fail.')
		return neo4jRestAPIRespJsonConverter.getRelationsFromRelationsAPI(respStr)
	}

	Map<String,Map<String,String>> buildRelationshipBetween2NodesWithProperties(final String fromNodeUri,final String toNodeUri, final String relationshipLabel,final Map<String,String> propertyMap){
		String propertyRequestJson = neo4jRestAPIRespJsonConverter.getCypherUpdateStatementRequest(propertyMap)
		String buildJsonBody ="{\"to\" : \"$toNodeUri\",\"type\" : \"$relationshipLabel\",\"data\" : {$propertyRequestJson}}"
		return this.dobuildRelationship(fromNodeUri, buildJsonBody)
	}

	Map<String,Map<String,String>> buildRelationshipBetween2Nodes(final String fromNodeUri,final String toNodeUri, final String relationshipLabel){
		String buildJsonBody ="{\"to\" : \"$toNodeUri\",\"type\" : \"$relationshipLabel\"}"
		return this.dobuildRelationship(fromNodeUri, buildJsonBody)
	}

	Map<String,Map<String,String>> dobuildRelationship(final String fromNodeUri,final String buildJsonBody){
		log.info "fromNodeUri:{} $fromNodeUri"
		log.info "buildJsonBody:{} $buildJsonBody"
		WebResource webResource = jerseyClient.resource(fromNodeUri).path('/relationships')
		ClientResponse response =  webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class,buildJsonBody)
		checkState(response.getStatus() == Status.CREATED.code,'Unknow error.')
		String respStr =getResponsePayload(response)
		return neo4jRestAPIRespJsonConverter.getRelationsFromRelationsAPI(respStr)
	}

	String createNodeFromCypherAPI(final String nodeLabel,final String createStatement){
		def self
		String jsonBody = "{\"statements\":[{\"statement\":\"CREATE (p:$nodeLabel{$createStatement}) RETURN p\",\"resultDataContents\":[\"REST\"]}]}"
		log.info "jsonBody: {} $jsonBody"
		ClientResponse response = doCallRestCypherTransactionalAPI(jsonBody)
		self = neo4jRestAPIRespJsonConverter.getNodesUriFromTransactionalWithRestFormat(getResponsePayload(response))
		return self
	}

	ClientResponse doCallRestCypherTransactionalAPI(final String jsonBody){
		WebResource webResource = jerseyClient.resource(neo4jHostUri)
				.path("transaction/commit")
		ClientResponse response =  webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, jsonBody)
		log.info "${response.getStatus()}"
		checkState(response.getStatus() == Status.OK.code,'Unknow error.')
		return response
	}

	void buildUnique(final String key,final String value,final String nodeUri){
		String uniqueNodeReqBody = "{\"value\" : \"$value\",\"uri\" : \"$nodeUri\",\"key\" : \"$key\"}"
		log.info "uniqueNodeReqBody:{} $uniqueNodeReqBody"
		WebResource webResource = jerseyClient.resource(neo4jHostUri)
				.path("index/node/favorites").queryParam("uniqueness", "create_or_fail")
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, uniqueNodeReqBody)

		if(response.getStatus() != Status.CREATED.code){
			checkState(response.getStatus() == Status.CONFLICT.code,'Unknow error.')
			String conflictIndex = neo4jRestAPIRespJsonConverter.getConflictNodeUriWhenCreateUniqueNode(getResponsePayload(response))
			throw new ConflictException(conflictIndex)
		}
	}

	AbstractCypherQueryResult doCypherQuery(final String queryJson,final int expectedStatusCode,final boolean returnRequired,final String distinctColumn){
		ClientResponse response  = doRestCypherQuery(queryJson)
		checkState(response.getStatus() == expectedStatusCode,'Unknown exception.')
		if(returnRequired){
			def responseStr = getResponsePayload(response)
			return neo4jRestAPIRespJsonConverter.getCypherQueryResult(responseStr,distinctColumn)
		}
		return null
	}

	Map getNodeFromNodeAPIByUri(final String uri){
		WebResource webResource = jerseyClient.resource(uri)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class)

		String respStr =getResponsePayload(response)
		if (response.getStatusInfo().statusCode != Status.OK.code){
			String errorMessage = neo4jRestAPIRespJsonConverter.getErrorMessageFromAPI(respStr)
			if(response.getStatus() == Status.NOT_FOUND.code){
				throw new NotFoundException(errorMessage)
			}
			throw new RuntimeException("Unknown error.")
		}
		return neo4jRestAPIRespJsonConverter.getNodeDataFromNodeAPI(respStr)
	}

	void deleteNodeFromNodeAPIByUri(final String uri){
		WebResource webResource = jerseyClient.resource(uri)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.delete(ClientResponse.class)

		if(response.getStatusInfo().statusCode != Status.NO_CONTENT.code){
			String errorMessage = neo4jRestAPIRespJsonConverter.getErrorMessageFromAPI(getResponsePayload(response))
			if (response.getStatus() == Status.CONFLICT.code) {
				throw new ConflictException(errorMessage)
			}  else if(response.getStatusInfo().statusCode == Status.NOT_FOUND.code){
				throw new NotFoundException(errorMessage)
			}
			throw new RuntimeException('Unknown error.')
		}
	}

	ClientResponse doRestCypherQuery(final String queryJson){
		WebResource webResource = jerseyClient.resource(neo4jHostUri)
				.path("cypher")
		return  webResource.accept(MediaType.APPLICATION_JSON)
		.type(MediaType.APPLICATION_JSON)
		.post(ClientResponse.class, queryJson)
	}
}
