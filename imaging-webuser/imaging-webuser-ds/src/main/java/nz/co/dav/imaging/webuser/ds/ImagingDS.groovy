package nz.co.dav.imaging.webuser.ds;

public interface ImagingDS {

	Set<String> getImagingUrisByTag(String tagName,String scalingType)throws Exception
}
