package nz.co.dav.imaging.resources;

import groovy.util.logging.Slf4j

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

import nz.co.dav.imaging.ds.ImagingProcessDS

import org.apache.commons.io.IOUtils
import org.jboss.resteasy.plugins.providers.multipart.InputPart
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput

import com.google.inject.Inject

@Path("/")
@Slf4j
public class ImagingResource {

	@Inject
	ImagingProcessDS imagingProcessDS

	@POST
	@Path("/process")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	Response processImage(final MultipartFormDataInput input) {
		log.info "processImage start..."
		def scalingConfig
		def tags
		Map<String,byte[]> imageMap = [:]
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap()
		uploadForm.each {k,v->
			InputPart inputPart = v.first()
			if(k == 'tags'){
				tags = inputPart.getBody(String.class, null)
			}else if(k == 'scalingConfig'){
				scalingConfig = inputPart.getBody(String.class, null)
			} else {
				byte [] bytes = this.getImageBytes(inputPart)
				imageMap.put(k, bytes)
			}
		}
		log.info "scalingConfigString:{} $scalingConfig"
		log.info "tags:{} $tags"
		uploadForm.keySet().each { log.info "field:{} $it" }
		String imgMetaJsonString = imagingProcessDS.process(scalingConfig, tags, imageMap)
		return Response.ok(imgMetaJsonString).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	Response doGet() {
		return Response.ok("Imaging API is available ...").type(MediaType.TEXT_PLAIN).build()
	}

	@GET
	@Path("/{tag}/{name}")
	@Produces("application/json")
	Response getImageMeta(
			@PathParam("tag") String tag,@PathParam("name") String name) {
			
	}

	byte[] getImageBytes(final InputPart inputPart){
		InputStream inputStream
		try {
			inputStream = inputPart.getBody(InputStream.class,null)
			return IOUtils.toByteArray(inputStream)
		} catch (final Exception e) {
			log.error "read image stream error. $e"
		} finally{
			if(inputStream){
				inputStream.close()
			}
		}
	}

	String getFileName(final MultivaluedMap<String, String> header) {
		String[] contentDisposition = header.getFirst("Content-Disposition").split(";")
		contentDisposition.each{
			if ((it.trim().startsWith("filename"))) {
				String[] name = it.split("=")
				String finalFileName = name[1].trim().replaceAll("\"", "")
				return finalFileName
			}
		}
		return "unknown"
	}
}
