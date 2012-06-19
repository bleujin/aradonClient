package net.ion.radon.aclient.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import net.ion.radon.aclient.FluentStringsMap;

import org.apache.log4j.spi.LoggerFactory;

public class MultipartRequestEntity implements RequestEntity {

	private static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";

	private static byte[] MULTIPART_CHARS = MultipartEncodingUtil.getAsciiBytes("-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");


	private static byte[] generateMultipartBoundary() {
		Random rand = new Random();
		byte[] bytes = new byte[rand.nextInt(11) + 30]; // a random size from 30 to 40
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)];
		}
		return bytes;
	}

	protected Part[] parts;

	private byte[] multipartBoundary;

	private FluentStringsMap methodParams;

	public MultipartRequestEntity(Part[] parts, FluentStringsMap methodParams) {
		if (parts == null) {
			throw new IllegalArgumentException("parts cannot be null");
		}
		if (methodParams == null) {
			methodParams = new FluentStringsMap();
		}
		this.parts = parts;
		this.methodParams = methodParams;
	}

	protected byte[] getMultipartBoundary() {
		if (multipartBoundary == null) {
			String temp = methodParams.get("") == null ? null : methodParams.get("").iterator().next();
			if (temp != null) {
				multipartBoundary = MultipartEncodingUtil.getAsciiBytes(temp);
			} else {
				multipartBoundary = generateMultipartBoundary();
			}
		}
		return multipartBoundary;
	}

	public boolean isRepeatable() {
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].isRepeatable()) {
				return false;
			}
		}
		return true;
	}
	public void writeRequest(OutputStream out) throws IOException {
		Part.sendParts(out, parts, getMultipartBoundary());
	}

	public long getContentLength() {
		try {
			return Part.getLengthOfParts(parts, getMultipartBoundary());
		} catch (Exception e) {
			e.printStackTrace() ;
			return 0;
		}
	}

	public String getContentType() {
		StringBuffer buffer = new StringBuffer(MULTIPART_FORM_CONTENT_TYPE);
		buffer.append("; boundary=");
		buffer.append(MultipartEncodingUtil.getAsciiString(getMultipartBoundary()));
		return buffer.toString();
	}

}
