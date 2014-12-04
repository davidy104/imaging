package nz.co.dav.imaging.config;

import com.amazonaws.auth.AWSCredentials

interface ConfigurationService {
	AWSCredentials getAWSCredentials()
	String getNeo4jRestHostUri()
	String getAwsS3BucketName()
}
