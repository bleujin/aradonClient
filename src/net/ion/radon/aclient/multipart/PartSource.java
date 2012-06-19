package net.ion.radon.aclient.multipart;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is an adaptation of the Apache HttpClient implementation
 * 
 * @link http://hc.apache.org/httpclient-3.x/
 */
public interface PartSource {

	/**
	 * Gets the number of bytes contained in this source.
	 * 
	 * @return a value >= 0
	 */
	long getLength();

	/**
	 * Gets the name of the file this source represents.
	 * 
	 * @return the fileName used for posting a MultiPart file part
	 */
	String getFileName();

	/**
	 * Gets a new InputStream for reading this source. This method can be called more than once and should therefore return a new stream every time.
	 * 
	 * @return a new InputStream
	 * @throws java.io.IOException
	 *             if an error occurs when creating the InputStream
	 */
	InputStream createInputStream() throws IOException;

}
