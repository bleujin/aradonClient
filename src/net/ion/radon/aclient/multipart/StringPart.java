package net.ion.radon.aclient.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * This class is an adaptation of the Apache HttpClient implementation
 * 
 * @link http://hc.apache.org/httpclient-3.x/
 */
public class StringPart extends PartBase {

	/**
	 * Default content encoding of string parameters.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "text/plain";

	/**
	 * Default charset of string parameters
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * Default transfer encoding of string parameters
	 */
	public static final String DEFAULT_TRANSFER_ENCODING = "8bit";

	/**
	 * Contents of this StringPart.
	 */
	private byte[] content;

	/**
	 * The String value of this part.
	 */
	private String value;

	public StringPart(String name, String value, Charset charset) {
		super(name, DEFAULT_CONTENT_TYPE, charset == null ? DEFAULT_CHARSET : charset, DEFAULT_TRANSFER_ENCODING);
		if (value == null) {
			throw new IllegalArgumentException("Value may not be null");
		}
		if (value.indexOf(0) != -1) {
			// See RFC 2048, 2.8. "8bit Data"
			throw new IllegalArgumentException("NULs may not be present in string parts");
		}
		this.value = value;
	}

	public StringPart(String name, String value, String charsetName) {
		this(name, value, Charset.isSupported(charsetName) ? Charset.forName(charsetName) : null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the part
	 * @param value
	 *            the string to post
	 */
	public StringPart(String name, String value) {
		this(name, value, DEFAULT_CHARSET);
	}

	/**
	 * Gets the content in bytes. Bytes are lazily created to allow the charset to be changed after the part is created.
	 * 
	 * @return the content in bytes
	 */
	private byte[] getContent() {
		if (content == null) {
			content = MultipartEncodingUtil.getBytes(value, getCharSet().name());
		}
		return content;
	}

	/**
	 * Writes the data to the given OutputStream.
	 * 
	 * @param out
	 *            the OutputStream to write to
	 * @throws java.io.IOException
	 *             if there is a write error
	 */
	protected void sendData(OutputStream out) throws IOException {
		out.write(getContent());
	}

	/**
	 * Return the length of the data.
	 * 
	 * @return The length of the data.
	 * @throws IOException
	 *             If an IO problem occurs
	 */
	protected long lengthOfData() throws IOException {
		return getContent().length;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.httpclient.methods.multipart.BasePart#setCharSet(java.lang.String)
	 */
	public void setCharSet(Charset charSet) {
		super.setCharSet(charSet);
		this.content = null;
	}

}
