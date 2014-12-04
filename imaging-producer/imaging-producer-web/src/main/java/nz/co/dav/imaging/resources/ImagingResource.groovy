package nz.co.dav.imaging.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class ImagingResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImagingResource.class);

	@POST
	@Path("/process")
	@Produces("application/json")
	@Consumes("multipart/form-data")
	public Response processImage(final @MultipartForm ImageProcessRequest imageProcessRequest) {
		LOGGER.info("processImage start...");
		LOGGER.info("imageProcessRequest:{} ", imageProcessRequest);

		return Response.ok("Image processed ...").type(MediaType.APPLICATION_JSON).build();
	}

	@GET
	public Response doGet() {
		return Response.ok("Imaging started ...").type(MediaType.TEXT_PLAIN).build();
	}
}
