package nz.co.dav.imaging.consume.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.api.client.ClientResponse;

public class JerseyClientUtil {

	public static String getResponsePayload(final ClientResponse response)
			throws IOException {
		String result = null;
		try (InputStream in = response.getEntityInputStream()) {
			result = IOUtils.toString(in, "UTF-8");
		}
		return result;
	}
}
