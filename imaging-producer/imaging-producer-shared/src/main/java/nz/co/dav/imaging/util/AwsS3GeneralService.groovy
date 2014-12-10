package nz.co.dav.imaging.util

import groovy.util.logging.Slf4j

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.google.inject.Inject
import com.google.inject.name.Named

@Slf4j
class AwsS3GeneralService {

	@Inject
	@Named("AWS.S3_BUCKET_NAME")
	String awsS3Bucket

	@Inject
	AmazonS3 amazonS3

	public static final String FOLDER_SUFFIX = "/";

	void putAsset(final String key,final InputStream asset,final String contentType) {
		if (asset) {
			try {
				ObjectMetadata meta = new ObjectMetadata()
				meta.setContentLength(asset.available())
				if (contentType) {
					meta.setContentType(contentType)
				}
				amazonS3.putObject(new PutObjectRequest(awsS3Bucket, key, asset, meta))
			} catch (e) {
				throw new RuntimeException(e)
			}finally{
				asset.close()
			}
		}
	}

	List<String> getAssetList(final String prefix) {
		List<String> result = []
		ObjectListing objList = amazonS3.listObjects(
				awsS3Bucket,
				formatPath(prefix))
		if (objList) {
			for (S3ObjectSummary summary : objList.getObjectSummaries()) {
				// ignore folders
				if (!summary.getKey().endsWith(FOLDER_SUFFIX)) {
					result << summary.getKey().substring(prefix.length())
				}
			}
		}
		return result
	}





	public static String formatPath(final String path) {
		// remove root path: /
		String formattedPath = null;
		if (path.startsWith(FOLDER_SUFFIX)) {
			formattedPath = path.substring(1);
		} else {
			formattedPath = path + FOLDER_SUFFIX;
		}
		return formattedPath;
	}
}
