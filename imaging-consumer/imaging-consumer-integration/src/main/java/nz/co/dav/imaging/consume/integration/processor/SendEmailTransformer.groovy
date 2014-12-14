package nz.co.dav.imaging.consume.integration.processor;

import groovy.util.logging.Slf4j

import javax.activation.DataHandler
import javax.mail.util.ByteArrayDataSource

import nz.co.dav.imaging.consume.model.EmailContent
import nz.co.dav.imaging.consume.model.SendEmailReq
import nz.co.dav.imaging.consume.util.ImagingUtils

import org.apache.camel.Exchange
import org.apache.camel.Expression
import org.apache.camel.Message

@Slf4j
class SendEmailTransformer implements Expression{

	@Override
	<T> T evaluate(Exchange exchange, Class<T> type) {
		Message messageIn = exchange.in
		SendEmailReq sendEmailReq = messageIn.getBody(SendEmailReq.class)
		EmailContent content = sendEmailReq.content

		messageIn.setHeader("To", ImagingUtils.arrayToStrWithSeperator(sendEmailReq.toArray,';'))
		messageIn.setHeader("Subject", sendEmailReq.subject)
		messageIn.setHeader("From", sendEmailReq.from)

		String[] ccArray = sendEmailReq.ccArray
		if(ccArray){
			String ccArrayStr = ImagingUtils.arrayToStrWithSeperator(ccArray,';')
			messageIn.setHeader("CC", ccArrayStr)
		}


		String[] bccArray = sendEmailReq.getBccArray()
		if (bccArray) {
			String bccStr = ImagingUtils.arrayToStrWithSeperator(bccArray,
					';')
			messageIn.setHeader("BCC", bccStr)
		}

		if (!sendEmailReq.content) {
			content = new EmailContent()
		}

		Map<String, byte[]> attachmentMap = exchange.properties["attachmentMap"]

		if (attachmentMap) {
			messageIn.attachments.clear()

			attachmentMap.each() {String key,byte[] value ->
				messageIn.addAttachment(key,
						new DataHandler(new ByteArrayDataSource(
						value,'application/octet-stream')))
			}
		}
		exchange.setProperty("SendEmailReq", sendEmailReq)
		return (T) content
	}
}
