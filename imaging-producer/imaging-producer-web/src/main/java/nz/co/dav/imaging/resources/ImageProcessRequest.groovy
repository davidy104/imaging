package nz.co.dav.imaging.resources

import groovy.transform.ToString

import javax.ws.rs.FormParam

import org.jboss.resteasy.annotations.providers.multipart.PartType

@ToString(includeNames = true, excludes = ['imageBytes'])
class ImageProcessRequest {
	
	@FormParam("scalingConfig")
	String scalingConfig
	
	@FormParam("uploadedImage")
	@PartType("application/octet-stream")
	byte[] imageBytes
}
