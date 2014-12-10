package nz.co.dav.imaging.util

import groovy.util.logging.Slf4j

import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class AwsS3GeneralService {

	@Inject
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket
}
