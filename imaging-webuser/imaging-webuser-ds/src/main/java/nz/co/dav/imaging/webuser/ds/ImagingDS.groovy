package nz.co.dav.imaging.webuser.ds;

import nz.co.dav.imaging.webuser.model.ImageInfo

public interface ImagingDS {

	Set<ImageInfo> getImagingUrisByTag(String tagName,String scalingType)throws Exception
}
