package net.ion.radon.aclient;

public class ByteArrayPart implements Part {
	private String name;
	private String fileName;
	private byte[] data;
	private String mimeType;
	private String charSet;

	public ByteArrayPart(String name, String fileName, byte[] data, String mimeType, String charSet) {
		this.name = name;
		this.fileName = fileName;
		this.data = data;
		this.mimeType = mimeType;
		this.charSet = charSet;
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getData() {
		return data;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getCharSet() {
		return charSet;
	}
}
