package nz.co.dav.imaging.repository.convert

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.repository.support.AbstractCypherQueryNode
import nz.co.dav.imaging.repository.support.AbstractCypherQueryResult

import com.google.common.base.Function
import com.google.inject.Inject

@Slf4j
class CypherAPIQuerySuccessRespConverter implements Function<String, AbstractCypherQueryResult> {
	@Inject
	JsonSlurper jsonSlurper

	String distinctColumn

	AbstractCypherQueryResult apply(final String jsonInput,final String distinctColumn){
		this.distinctColumn = distinctColumn
		this.apply(jsonInput)
	}

	@Override
	AbstractCypherQueryResult apply(final String jsonInput) {
		AbstractCypherQueryResult result = new AbstractCypherQueryResult()
		Map metaMap = (Map)jsonSlurper.parseText(jsonInput)
		List columnList = metaMap.get('columns')
		List metaDataList = metaMap.get('data')
		if(!metaDataList){
			throw new NotFoundException("Data not found from Neo4j response.")
		}
		boolean distinctNode = false

		if(distinctColumn){
			metaDataList.each {meta->
				boolean found = false
				meta.eachWithIndex {obj,i->
					String column = columnList.get(i)
					if(!found){
						if(column == distinctColumn && obj instanceof Map){
							distinctNode = true
							Map<String,Object> tdistinctMap = (Map)obj
							Map<String,String> distinctDataMap = (Map)tdistinctMap.get("data")
							String nodeUri = tdistinctMap.get("self")
							result.distinctNodes << new AbstractCypherQueryNode(uri:nodeUri,column:column,dataMap:distinctDataMap)
						}
					}
				}
			}
		}
		if(distinctNode){
			metaDataList.each {meta->
				boolean found = false
				AbstractCypherQueryNode disctNode
				Map<String,AbstractCypherQueryNode> relatedNodeMap = [:]
				Map<String,String> dataValuesMap = [:]

				meta.eachWithIndex {obj,i->
					String column = columnList.get(i)
					if(!found && distinctColumn == column){
						String curNodeUri = ((Map)obj).get("self")
						disctNode = result.distinctNodes.find{
							it.uri == curNodeUri && it.column == column
						}
					}
					if(distinctColumn != column){
						if(obj instanceof Map){
							Map<String,Object> nodeMap = (Map)obj
							String nodeUri = (String)(nodeMap.get("self"))
							Map<String,String> nodeDataMap = nodeMap.get("data")
							def queryNode = new AbstractCypherQueryNode(uri:nodeUri,column:column,dataMap:nodeDataMap)
							relatedNodeMap.put(column, queryNode)
						}else{
							dataValuesMap.put(column, (String)obj)
						}
					}
				}
				relatedNodeMap.each {k,v->
					if(disctNode.relationAbstractCypherQueryNodes.containsKey(k)){
						disctNode.relationAbstractCypherQueryNodes.get(k) << v
					}else{
						disctNode.relationAbstractCypherQueryNodes.put(k, [v] as Set)
					}
				}

				dataValuesMap.each{k,v->
					if(disctNode.relationDataMap.containsKey(k)){
						disctNode.relationDataMap.get(k) << v
					}else{
						disctNode.relationDataMap.put(k, [v] as Set)
					}
				}
			}
		} else {
			metaDataList.each {meta->
				meta.eachWithIndex {obj,i->
					String column = columnList.get(i)
					if(obj instanceof Map){
						Map<String,Object> nodeMap = (Map)obj
						String nodeUri = (String)(nodeMap.get("self"))
						Map<String,String> nodeDataMap = nodeMap.get("data")

						Map<String,Map<String,String>> internalMap = [:]
						if(result.nodeColumnMap.containsKey(column)){
							internalMap = result.nodeColumnMap.get(column)
						}
						if(!internalMap.containsKey(nodeUri)){
							internalMap.put(nodeUri, nodeDataMap)
						}
						result.nodeColumnMap.put(column, internalMap)
					} else {
						Set<String> dataSet = []
						if(result.dataColumnMap.containsKey(column)){
							dataSet = result.dataColumnMap.get(column)
						}
						dataSet << (String)obj
						result.dataColumnMap.put(column, dataSet)
					}
				}
			}
		}
		return result
	}
}
