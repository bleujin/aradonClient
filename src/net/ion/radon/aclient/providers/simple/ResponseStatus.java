package net.ion.radon.aclient.providers.simple;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.HttpResponseStatus;

/**
 * A class that represent the HTTP response' status line (code + text)
 */
public class ResponseStatus extends HttpResponseStatus {

	private final HttpURLConnection urlConnection;

	public ResponseStatus(URI uri, HttpURLConnection urlConnection, AsyncHttpProvider provider) {
		super(uri, provider);
		this.urlConnection = urlConnection;
	}

	/**
	 * Return the response status code
	 * 
	 * @return the response status code
	 */
	public int getStatusCode() {
		try {
			return urlConnection.getResponseCode();
		} catch (IOException e) {
			return 500;
		}
	}

	/**
	 * Return the response status text
	 * 
	 * @return the response status text
	 */
	public String getStatusText() {
		try {
			return urlConnection.getResponseMessage();
		} catch (IOException e) {
			return "Internal Error";
		}
	}

	@Override
	public String getProtocolName() {
		return "http";
	}

	@Override
	public int getProtocolMajorVersion() {
		return 1;
	}

	@Override
	public int getProtocolMinorVersion() {
		return 1; // TODO
	}

	@Override
	public String getProtocolText() {
		return ""; // TODO
	}

}