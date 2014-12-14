package nz.co.dav.imaging.consume.config;

import nz.co.dav.imaging.consume.model.SendEmailReq

import com.amazonaws.auth.AWSCredentials

interface ConfigurationService {
	AWSCredentials getAWSCredentials()
	EmailConfig getEmailConfig()
	SendEmailReq getBatchImageEmailRequest()
	String getImageEmailTemplateVm()
}
