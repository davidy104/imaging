package nz.co.dav.imaging.consume.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.google.inject.Inject
import com.google.inject.name.Named

class ConfigurationServiceImpl implements ConfigurationService{

	@Inject
	@Named("AWS.ACCESS_KEY_ID")
	String awsAccessKey

	@Inject
	@Named("AWS.SECRET_KEY")
	String awsSecretKey

	@Inject
	@Named("MAIL.HOST")
	String mailHost

	@Inject
	@Named("MAIL.PASSWORD")
	String mailPassword

	@Inject
	@Named("MAIL.DEBUG")
	String mailDebug

	@Inject
	@Named("MAIL.SMTP_AUTH")
	String mailSmtpAuth

	@Inject
	@Named("MAIL.CONNECTION_TIMEOUT")
	String mailConnectionTimeout

	@Inject
	@Named("MAIL.SMTP_PORT")
	String mailSmtpPort

	@Inject
	@Named("MAIL.SMTPS_PORT")
	String mailSmtpsPort

	@Inject
	@Named("MAIL.REDELIVERY_DELAY")
	String mailRedeliveryDelay

	@Inject
	@Named("MAIL.MAXIMUM_REDELIVERIES")
	String mailMaxiMumRedeliveries

	@Inject
	@Named("MAIL.ATTACHED_SIZE")
	String mailAttachedSize

	@Override
	public AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(awsAccessKey, awsSecretKey)
	}
	
	@Override
	public EmailConfig getEmailConfig() {
		EmailConfig emailConfig = new EmailConfig(host:mailHost,password:mailPassword)
		if(mailDebug){
			emailConfig.debug = Boolean.valueOf(mailDebug)
		}
		if(mailSmtpAuth){
			emailConfig.smtpAuth = Boolean.valueOf(mailSmtpAuth)
		}
		if(mailConnectionTimeout){
			emailConfig.connectionTimeOut = Integer.valueOf(mailConnectionTimeout)
		}
		if(mailSmtpPort){
			emailConfig.smtpPort = Integer.valueOf(mailSmtpPort)
		}
		if(mailSmtpsPort){
			emailConfig.smtpsPort = Integer.valueOf(mailSmtpsPort)
		}
		if(mailRedeliveryDelay){
			emailConfig.redeliveryDelay = Integer.valueOf(mailRedeliveryDelay)
		}
		if(mailMaxiMumRedeliveries){
			emailConfig.maximumRedeliveries = Integer.valueOf(mailMaxiMumRedeliveries)
		}
		if(mailAttachedSize){
			emailConfig.attachedSize = Integer.valueOf(mailAttachedSize)
		}
		return emailConfig
	}
}
