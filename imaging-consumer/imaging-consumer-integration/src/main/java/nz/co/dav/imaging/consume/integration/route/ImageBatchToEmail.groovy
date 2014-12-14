package nz.co.dav.imaging.consume.integration.route;

import nz.co.dav.imaging.consume.config.ConfigurationService
import nz.co.dav.imaging.consume.config.EmailConfig

import org.apache.camel.builder.RouteBuilder

class ImageBatchToEmail extends RouteBuilder {

	EmailConfig emailConfig

	public ImageBatchToEmail(final ConfigurationService configurationService) {
		this.emailConfig = configurationService.getEmailConfig()
	}


	@Override
	public void configure() throws Exception {
		
	}
}
