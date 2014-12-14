package nz.co.dav.imaging.consume.config

import nz.co.dav.imaging.consume.model.EmailContent
import nz.co.dav.imaging.consume.model.SendEmailReq

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
	@Named("MAIL.USERNAME")
	String mailUser
	
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

	@Inject
	@Named("EMAIL.TO_ADDRESS_LIST")
	String emailToAddressList
	
	@Inject
	@Named("EMAIL.IMAGE_TEMPLATE_VM")
	String emailImageTemplateVM

	@Override
	public AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(awsAccessKey, awsSecretKey)
	}

	@Override
	public EmailConfig getEmailConfig() {
		EmailConfig emailConfig = new EmailConfig(host:mailHost,user:mailUser,password:mailPassword)
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
		println "emailConfig:{} $emailConfig"
		return emailConfig
	}

	@Override
	SendEmailReq getBatchImageEmailRequest() {
		EmailContent content = new EmailContent(toWho:'',contentDetails:[
			'This email is for Sharing Pictures'
		],signature:'David')
		SendEmailReq req = new SendEmailReq(subject:'Pictures',from:'david.yuan@gmail.com',content:content)
		if(emailToAddressList){
			req.toArray = emailToAddressList.split(';')
		}
		return req
	}

	@Override
	String getImageEmailTemplateVm() {
		return emailImageTemplateVM
	}
}
