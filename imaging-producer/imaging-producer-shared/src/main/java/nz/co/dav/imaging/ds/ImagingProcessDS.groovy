package nz.co.dav.imaging.ds;

import nz.co.dav.imaging.model.ImageMetaModel

interface ImagingProcessDS {
	String process(String scalingConfig,String tag, Map<String,byte[]> imagesMap)throws Exception
	ImageMetaModel getImageMetaDataByTagAndName(String tag,String name) throws Exception
	List<ImageMetaModel> getAllImageMetaModel(String tag)throws Exception
	void deleteAllImageMetaByTag(String tag) throws Exception
}
