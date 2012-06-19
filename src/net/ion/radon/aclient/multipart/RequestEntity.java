package net.ion.radon.aclient.multipart;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is an adaptation of the Apache HttpClient implementation
 * 
 * @link http://hc.apache.org/httpclient-3.x/
 */
public interface RequestEntity {

	/**
	 * Tests if {@link #writeRequest(java.io.OutputStream)} can be called more than once.
	 * 
	 * @return <tt>true</tt> if the entity can be written to {@link java.io.OutputStream} more than once, <tt>false</tt> otherwise.
	 */
	boolean isRepeatable();

	/**
	 * Writes the request entity to the given stream.
	 * 
	 * @param out
	 * @throws java.io.IOException
	 */
	void writeRequest(OutputStream out) throws IOException;

	/**
	 * Gets the request entity's length. This method should return a non-negative value if the content length is known or a negative value if it is not. In the latter case the EntityEnclosingMethod will use chunk encoding to transmit the request entity.
	 * 
	 * @return a non-negative value when content length is known or a negative value when content length is not known
	 */
	long getContentLength();

	/**
	 * Gets the entity's content type. This content type will be used as the value for the "Content-Type" header.
	 * 
	 * @return the entity's content type
	 */
	String getContentType();

}
