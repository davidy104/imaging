package nz.co.dav.imaging.resources;

import groovy.util.logging.Slf4j

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response

import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.model.ImageMetaModel

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
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
		try {
			if(!StringUtils.isEmpty(tag) && !StringUtils.isEmpty(name)){
				ImageMetaModel foundImageMetaModel = imagingProcessDS.getImageMetaDataByTagAndName(tag,name)
				return Response.ok(foundImageMetaModel).type(MediaType.APPLICATION_JSON).build()
			} else if(!StringUtils.isEmpty(tag)&& StringUtils.isEmpty(name)){
				List<ImageMetaModel> imageMetaModelList = imagingProcessDS.getAllImageMetaModel(tag)
				return Response.ok(imageMetaModelList).type(MediaType.APPLICATION_JSON).build()
			} else {
				return Response.status(Response.Status.BAD_REQUEST).entity("either tag or name must be provided.").build();
			}
		} catch (final Exception e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build()
		}
		return null
	}

	@DELETE @Path("{tag}")
	@Produces("application/json")
	Response deletImageByTag(@PathParam("tag") String tag){
		try {
			imagingProcessDS.deleteAllImageMetaByTag(tag)
		} catch (final Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()
		}
		return Response.status(Response.Status.NO_CONTENT).entity("images deleted with tag [$tag]").build()
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
