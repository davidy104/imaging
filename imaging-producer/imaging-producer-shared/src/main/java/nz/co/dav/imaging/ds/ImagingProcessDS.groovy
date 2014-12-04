package nz.co.dav.imaging.ds;

interface ImagingProcessDS {
	String process(String scalingConfig,String tags, Map<String,byte[]> imagesMap)throws Exception
}
