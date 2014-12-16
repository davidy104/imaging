package nz.co.dav.imaging.webuser.ds.impl
import static com.google.common.base.Preconditions.checkState
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import javax.annotation.Resource
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response.Status

import nz.co.dav.imaging.webuser.ds.ImagingDS
import nz.co.dav.imaging.webuser.model.ImageInfo

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.WebResource
@Service
@Slf4j
class ImagingDSImpl implements ImagingDS{

	@Resource
	Client jerseyClient

	@Resource
	JsonSlurper jsonSlurper

	@Value('${image.producer_uri}')
	String imageProducerHostUri

	//	static final String IMAGE_SERVICE_URI="http://localhost/image"

	@Override
	Set<ImageInfo> getImagingUrisByTag(final String tagName,String scalingType) {
		if(StringUtils.isEmpty(scalingType)){
			scalingType = "normal"
		}
		Set<ImageInfo> resultSet = []
		WebResource webResource = jerseyClient.resource(imageProducerHostUri).path("meta").path(tagName)
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.get(ClientResponse.class)

		String responseStr= getResponsePayload(response)
		checkState(response.getStatus() == Status.OK.code,responseStr)

		List resultList = (List)jsonSlurper.parseText(responseStr)
		resultList.each {
			Map resultMap = (Map)it
			def name = resultMap['name']
			def tag = resultMap['tag']
			def imageUri = imageProducerHostUri+"/stream/"+tag+"/"+name+"/"+scalingType
			log.info "imageUri:{} $imageUri"
			resultSet << new ImageInfo(tag:tag,name:name,imageUri:imageUri)
		}
		log.info "resultSet size:{} ${resultSet.size()}"
		return resultSet
	}


	static String getResponsePayload(final ClientResponse response) throws IOException {
		String result = null
		InputStream inputStream
		try{
			inputStream = response.getEntityInputStream()
			result = IOUtils.toString(inputStream, "UTF-8")
		}catch(e){
			log.error "read rest response error."
		}finally{
			if(inputStream){
				inputStream.close()
			}
		}
		return result
	}
}
