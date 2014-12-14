package nz.co.dav.imaging.consume.integration.route;

import nz.co.dav.imaging.consume.config.ConfigurationService
import nz.co.dav.imaging.consume.config.EmailConfig
import nz.co.dav.imaging.consume.integration.processor.SendEmailTransformer
import nz.co.dav.imaging.consume.model.SendEmailReq

import org.apache.camel.builder.RouteBuilder


class ImageBatchToEmail extends RouteBuilder {

	EmailConfig emailConfig

	String emailImageTemplateVM

	SendEmailReq imageEmailTemplateReq

	SendEmailTransformer sendEmailTransformer

	//	String smtpsUri

	public ImageBatchToEmail(final ConfigurationService configurationService,final SendEmailTransformer sendEmailTransformer) {
		this.emailConfig = configurationService.getEmailConfig()
		this.emailImageTemplateVM = configurationService.getImageEmailTemplateVm()
		this.imageEmailTemplateReq = configurationService.getBatchImageEmailRequest()
		this.sendEmailTransformer = sendEmailTransformer
		//		smtpsUri = emailConfig.host + ":"+ emailConfig.smtpsPort + "?connectionTimeout="
		//		+ emailConfig.connectionTimeOut + "&username="+emailConfig.user+"&password="+emailConfig.password+"&contentType=text/html&debugMode="+emailConfig.debug
	}

	@Override
	public void configure() throws Exception {
		from("direct:imageBatchToEmail").routeId("imageBatchToEmail")
				.split(body())
				.to("direct:doImageBatchToEmail")
				.end()

		from("direct:doImageBatchToEmail")
				.setProperty("attachmentMap", simple('${body}'))
				.setBody(constant(imageEmailTemplateReq))
				.to("direct:doSendEmail")

		from("direct:doSendEmail")
				.transform(sendEmailTransformer)
				.to("velocity:emailTemplate.vm")
				.to("log:mail")
				.to("smtps://" + emailConfig.host + ":"+ emailConfig.smtpsPort + "?connectionTimeout="
				+ emailConfig.connectionTimeOut + "&username="+emailConfig.user+"&password="+emailConfig.password+"&contentType=text/html&debugMode="+emailConfig.debug)
	}
}
