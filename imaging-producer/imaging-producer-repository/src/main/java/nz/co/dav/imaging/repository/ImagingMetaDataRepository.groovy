package nz.co.dav.imaging.repository;

import nz.co.dav.imaging.model.ImageMetaModel

interface ImagingMetaDataRepository {
	String createImageMetaData(Map<String,String> metaMap) throws Exception
	ImageMetaModel getImageMetaDataByTagAndName(String tag,String name) throws Exception
	List<ImageMetaModel> getAllImageMetaModel(String tag)throws Exception
	void deleteAllImageMetaByTag(String tag) throws Exception
}
