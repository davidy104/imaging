package nz.co.dav.imaging.consume.config;

import com.amazonaws.auth.AWSCredentials

interface ConfigurationService {
	AWSCredentials getAWSCredentials()
	String getNeo4jRestHostUri()
	String getAwsS3BucketName()
}
