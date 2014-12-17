package nz.co.dav.imaging.repository;

import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.model.Page

interface ImagingMetaDataRepository {
	String createImageMetaData(ImageMetaModel imageMetaModel) throws Exception
	Map<String,String> getImageMetaDataByTagAndName(String tag,String name) throws Exception
	Map<String,Map<String,String>> getImageMetaData(String tag)throws Exception
	void deleteImage(String tag) throws Exception
	void deleteImage(String tag,String name) throws Exception
	void deleteImageByNodeUri(String nodeUri)throws Exception
	Page paginateImage(Integer pageOffset,Integer pageSize,String tag) throws Exception
}
