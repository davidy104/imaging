package nz.co.dav.imaging.repository.convert

import nz.co.dav.imaging.model.ImageMetaModel

import com.google.common.base.Function

class ImageMetaMapToModelConverter implements Function<Map<String,String>, ImageMetaModel> {

	@Override
	public ImageMetaModel apply(Map<String, String> input) {
		return new ImageMetaModel(tag:input['tag'],name:input['name'],createTime:input['DateTimeOriginal'],metaMap:input,s3Prefix:input['s3Prefix'],s3Path:input['imagesS3Keis'])
	}
}
