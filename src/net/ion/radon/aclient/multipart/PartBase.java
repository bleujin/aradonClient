package net.ion.radon.aclient.multipart;

import java.nio.charset.Charset;

/**
 * This class is an adaptation of the Apache HttpClient implementation
 * 
 * @link http://hc.apache.org/httpclient-3.x/
 */
public abstract class PartBase extends Part {

	/**
	 * Name of the file part.
	 */
	private String name;

	/**
	 * Content type of the file part.
	 */
	private String contentType;

	private Charset charSet;

	private String transferEncoding;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the part
	 * @param contentType
	 *            The content type, or <code>null</code>
	 * @param charSet
	 *            The character encoding, or <code>null</code>
	 * @param transferEncoding
	 *            The transfer encoding, or <code>null</code>
	 */
	public PartBase(String name, String contentType, Charset charSet, String transferEncoding) {

		if (name == null) {
			throw new IllegalArgumentException("Name must not be null");
		}
		this.name = name;
		this.contentType = contentType;
		this.charSet = charSet;
		this.transferEncoding = transferEncoding;
	}

	/**
	 * Returns the name.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the content type of this part.
	 * 
	 * @return String The name.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Return the character encoding of this part.
	 * 
	 * @return String The name.
	 */
	public Charset getCharSet() {
		return this.charSet;
	}

	/**
	 * Returns the transfer encoding of this part.
	 * 
	 * @return String The name.
	 */
	public String getTransferEncoding() {
		return transferEncoding;
	}

	/**
	 * Sets the character encoding.
	 * 
	 * @param charSet
	 *            the character encoding, or <code>null</code> to exclude the character encoding header
	 */
	public void setCharSet(Charset charSet) {
		this.charSet = charSet;
	}

	/**
	 * Sets the content type.
	 * 
	 * @param contentType
	 *            the content type, or <code>null</code> to exclude the content type header
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the part name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null");
		}
		this.name = name;
	}

	/**
	 * Sets the transfer encoding.
	 * 
	 * @param transferEncoding
	 *            the transfer encoding, or <code>null</code> to exclude the transfer encoding header
	 */
	public void setTransferEncoding(String transferEncoding) {
		this.transferEncoding = transferEncoding;
	}

}
