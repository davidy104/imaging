package nz.co.dav.imaging.consume.model

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class SendEmailReq{

	String[] toArray
	String[] ccArray
	String[] bccArray
	EmailContent content
	String[] attachedFileName
	String templateFile
	String subject
	String from
	String protocol
	String contentType
}
