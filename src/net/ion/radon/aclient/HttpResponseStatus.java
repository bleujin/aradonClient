package net.ion.radon.aclient;

import java.net.URI;

public abstract class HttpResponseStatus extends HttpContent {

	public HttpResponseStatus(URI uri, AsyncHttpProvider provider) {
		super(uri, provider);
	}

	abstract public int getStatusCode();

	abstract public String getStatusText();

	abstract public String getProtocolName();

	abstract public int getProtocolMajorVersion();

	abstract public int getProtocolMinorVersion();

	abstract public String getProtocolText();
}
