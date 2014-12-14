package nz.co.dav.imaging.consume.util;

import nz.co.dav.imaging.consume.model.SendEmailReq

class ImagingUtils {

	def emailPattern =/[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})/

	void validateEmailRequest(final SendEmailReq sendEmailReq){
		String[] toArray = sendEmailReq.toArray
		if(!toArray){
			throw new IllegalArgumentException("To account can not be empty")
		}

		toArray.each {
			boolean match = it==~ emailPattern
			if(!match){
				throw new IllegalArgumentException("To email address[$it] is invalid.")
			}
		}
	}

	def static arrayToStr = {Object[] array->
		if(array){
			def str = array.join(',')
			str
		}
	}

	def static arrayToStrWithSeperator = {Object[] array,seperator->
		if(array){
			def str = array.join(seperator)
			str
		}
	}

	static boolean checkBytesSizeLessMax(byte[] data, int maximumSize) {
		double megabytes = data.length / 1024 / 1024
		if (megabytes < maximumSize) {
			return true
		}
		return false
	}
}
