package net.ion.radon.aclient.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayPartSource implements PartSource {

	private final String fileName;

	private final byte[] bytes;

	public ByteArrayPartSource(String fileName, byte[] bytes) {
		this.fileName = fileName;
		this.bytes = bytes;
	}

	public long getLength() {
		return bytes.length;
	}

	public String getFileName() {
		return fileName;
	}

	public InputStream createInputStream() throws IOException {
		return new ByteArrayInputStream(bytes);
	}

}
