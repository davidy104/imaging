package nz.co.dav.imaging.integration.processor;


import groovy.util.logging.Slf4j

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import nz.co.dav.imaging.model.AbstractImageInfo

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.imgscalr.Scalr
import org.imgscalr.Scalr.Mode
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Slf4j
class ImageScalingProcessor implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		AbstractImageInfo imageInfo = exchange.getProperty("imageInfo", AbstractImageInfo.class)
		String s3Path = exchange.getProperty("s3Path")
		String tag = exchange.getProperty("tag", String.class)
		String processTime = exchange.getProperty("processTime", String.class)

		byte[] imageBytes = imageInfo.imageBytes
		String imageExtension = imageInfo.extension
		String imageName = imageInfo.imageName

		Map imageTransform = exchange.in.getBody()
		def transformName = imageTransform['name']
		String fileName = imageName + "."+imageExtension
		InputStream imageInputStream= new ByteArrayInputStream(imageBytes)
		if(transformName != 'original'){
			Integer width = Integer.valueOf(imageTransform['width'])
			Integer hight = Integer.valueOf(imageTransform['height'])
			BufferedImage img = ImageIO.read(imageInputStream)
			final BufferedImage bufferedImage = Scalr.resize(img,
					Mode.AUTOMATIC,width, hight)
			final ByteArrayOutputStream output = new ByteArrayOutputStream() {
						@Override
						public synchronized byte[] toByteArray() {
							return this.buf
						}
					}
			ImageIO.write(bufferedImage, imageExtension, output)
			imageInputStream = new ByteArrayInputStream(
					output.toByteArray(), 0, output.size())
			fileName = imageName +"-"+transformName+ "."+imageExtension
		}
		exchange.in.setBody(imageInputStream, InputStream.class)
		int imgStreamAvailable = imageInputStream.available()

		String outputPath
		if(tag){
			tag = !tag.endsWith("/")?tag +"/"+fileName:tag+fileName
			outputPath = !s3Path.endsWith("/")?s3Path +"/"+tag:s3Path+tag
		} else {
			outputPath = !s3Path.endsWith("/")?s3Path +"/"+fileName:s3Path+fileName
		}
		
		exchange.setProperty("imgStreamAvailable", imgStreamAvailable)
		exchange.setProperty("outputPath", outputPath)
	}
}
