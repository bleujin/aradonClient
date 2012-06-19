package net.ion.radon.aclient.multipart;

import java.io.UnsupportedEncodingException;

/**
 * This class is an adaptation of the Apache HttpClient implementation
 * 
 * @link http://hc.apache.org/httpclient-3.x/
 */
public class MultipartEncodingUtil {

	public static byte[] getAsciiBytes(String data) {
		try {
			return data.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAsciiString(final byte[] data) {
		if (data == null) {
			throw new IllegalArgumentException("Parameter may not be null");
		}

		try {
			return new String(data, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getBytes(final String data, String charset) {

		if (data == null) {
			throw new IllegalArgumentException("data may not be null");
		}

		if (charset == null || charset.length() == 0) {
			throw new IllegalArgumentException("charset may not be null or empty");
		}

		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(String.format("Unsupported encoding: %s", charset));
		}
	}
}
