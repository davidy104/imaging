package nz.co.dav.imaging.consume.model

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class SendEmailReq{

	String[] toArray
	String[] ccArray
	String[] bccArray
	EmailContent content
	String subject
	String from
	String protocol='smtp'
	String contentType='text/html'
}
