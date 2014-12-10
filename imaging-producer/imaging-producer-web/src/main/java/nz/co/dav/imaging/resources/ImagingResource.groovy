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
import javax.ws.rs.core.Response.Status

import nz.co.dav.imaging.NotFoundException
import nz.co.dav.imaging.ds.ImagingProcessDS
import nz.co.dav.imaging.model.ImageMetaModel

import org.apache.commons.io.IOUtils
import org.apache.commons.io.input.ProxyInputStream
import org.jboss.resteasy.plugins.providers.multipart.InputPart
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput

import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.S3Object
import com.google.inject.Inject


@Path("/")
@Slf4j
public class ImagingResource {

	@Inject
	ImagingProcessDS imagingProcessDS

	@POST
	@Path("process")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	Response processImage(final MultipartFormDataInput input) {
		log.info "processImage start..."
		def scalingConfig
		def tag
		Map<String,byte[]> imageMap = [:]
		Map<String, List<InputPart>> uploadForm = input.getFormDataMap()
		uploadForm.each {k,v->
			InputPart inputPart = v.first()
			if(k == 'tag'){
				tag = inputPart.getBody(String.class, null)
			}else if(k == 'scalingConfig'){
				scalingConfig = inputPart.getBody(String.class, null)
			} else {
				byte [] bytes = this.getImageBytes(inputPart)
				imageMap.put(k, bytes)
			}
		}
		log.info "scalingConfigString:{} $scalingConfig"
		log.info "tag:{} $tag"
		uploadForm.keySet().each { log.info "field:{} $it" }
		String imgMetaJsonString = imagingProcessDS.process(scalingConfig, tag, imageMap)
		return Response.ok(imgMetaJsonString).type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	Response doGet() {
		return Response.ok("Imaging API is available ...").type(MediaType.TEXT_PLAIN).build()
	}

	@GET
	@Path("meta/{tag}/{name}")
	@Produces("application/json")
	Response getImageMeta(
			@PathParam("tag") String tag,@PathParam("name") String name) {
		ImageMetaModel foundImageMetaModel
		try {
			foundImageMetaModel = imagingProcessDS.getImageMetaDataByTagAndName(tag,name)
		} catch (final Exception e) {
			if(e instanceof NotFoundException){
				return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build()
			}
			throw e
		}
		return Response.ok(foundImageMetaModel).type(MediaType.APPLICATION_JSON).build()
	}

	@GET
	@Path("meta/{tag}")
	@Produces("application/json")
	Response getImageMetaByTag(@PathParam("tag") String tag){
		List<ImageMetaModel> imageMetaModelList = []
		try {
			imageMetaModelList = imagingProcessDS.getAllImageMetaModel(tag)
		} catch (e) {
			if(e instanceof NotFoundException){
				return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build()
			}
			throw e
		}
		return Response.ok(imageMetaModelList).type(MediaType.APPLICATION_JSON).build()
	}

	@DELETE @Path("{tag}")
	@Produces("application/json")
	Response deletImage(@PathParam("tag") String tag){
		try {
			imagingProcessDS.deleteAllImageMeta(tag)
		} catch (e) {
			if(e instanceof NotFoundException ){
				return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build()
			} else if(e instanceof AmazonS3Exception){
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()
			}
			throw e
		}
		return Response.status(Response.Status.NO_CONTENT).entity("images deleted with tag [$tag]").build()
	}

	@GET
	@Path("stream/{tag}/{name}/{scalingType}")
	Response showImage(@PathParam("tag") final String tag,@PathParam("name") String name,@PathParam("scalingType") String scalingType){
		try {
			final S3Object s3Object = imagingProcessDS.getImage(tag, name, scalingType)
			return Response.ok(new ProxyInputStream(s3Object.getObjectContent()) {
				@Override
				public void close() throws IOException {
					super.close();
					s3Object.close();
				}
			}, s3Object.getObjectMetadata().getContentType()).build()
		} catch (e) {
			if(e instanceof NotFoundException || e instanceof AmazonS3Exception){
				return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build()
			}
			throw e
		}
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
				String[] tname = it.split("=")
				String finalFileName = tname[1].trim().replaceAll("\"", "")
				return finalFileName
			}
		}
		return "unknown"
	}
}
