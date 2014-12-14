package nz.co.dav.imaging.consume.config;

import com.amazonaws.auth.AWSCredentials

interface ConfigurationService {
	AWSCredentials getAWSCredentials()
	EmailConfig getEmailConfig()
}
