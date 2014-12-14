package nz.co.dav.imaging.consume.config;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["mailHost","user","password"])
class EmailConfig {
	String host
	String user
	String password
	boolean debug
	boolean smtpAuth
	int connectionTimeOut =10000
	int smtpPort=25
	int smtpsPort=465
	int redeliveryDelay=3000
	int maximumRedeliveries=2
	int attachedSize=8
}
