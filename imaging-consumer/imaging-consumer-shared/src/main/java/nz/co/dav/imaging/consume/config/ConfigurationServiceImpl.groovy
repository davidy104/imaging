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
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket

	@Inject
	@Named("NEO4J.HOST_URI")
	String neo4jHostUri

	@Inject
	@Named("AWS.SQS_EVENT_QUEUE_NAME")
	String awsSqsQueueName

	@Override
	public AWSCredentials getAWSCredentials() {
		return new BasicAWSCredentials(awsAccessKey, awsSecretKey)
	}

	@Override
	public String getNeo4jRestHostUri() {
		return neo4jHostUri
	}

	@Override
	public String getAwsS3BucketName() {
		return awsS3Bucket
	}


}
