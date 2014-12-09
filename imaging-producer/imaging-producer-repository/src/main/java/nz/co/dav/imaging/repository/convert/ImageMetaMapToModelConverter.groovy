package nz.co.dav.imaging.repository.convert

import java.text.SimpleDateFormat

import nz.co.dav.imaging.model.ImageMetaModel

import com.google.common.base.Function

class ImageMetaMapToModelConverter implements Function<Map<String,String>, ImageMetaModel> {
	SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss")

	@Override
	public ImageMetaModel apply(Map<String, String> input) {
		def createTimeStr = input['createTime']
		Date createTime
		if(createTimeStr){
			createTime = DATE_FORMAT.parse(createTimeStr)
		}
		return new ImageMetaModel(tag:input['tag'],name:input['name'],createTime:createTime,meta:input)
	}
}
