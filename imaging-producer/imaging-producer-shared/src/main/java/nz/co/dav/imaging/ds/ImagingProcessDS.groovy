package nz.co.dav.imaging.ds;

import nz.co.dav.imaging.model.ImageMetaModel

import com.amazonaws.services.s3.model.S3Object

interface ImagingProcessDS {
	String process(String scalingConfig,String tag, Map<String,byte[]> imagesMap)throws Exception
	ImageMetaModel getImageMetaDataByTagAndName(String tag,String name) throws Exception
	List<ImageMetaModel> getAllImageMetaModel(String tag)throws Exception
	void deleteAllImageMeta(String tag) throws Exception
	S3Object getImage(String tag,String name,String scalingType) throws Exception
}
