package net.ion.radon.aclient;

import java.net.URI;

public abstract class HttpResponseHeaders extends HttpContent {

	private final boolean traillingHeaders;

	public HttpResponseHeaders(URI uri, AsyncHttpProvider provider) {
		super(uri, provider);
		this.traillingHeaders = false;
	}

	public HttpResponseHeaders(URI uri, AsyncHttpProvider provider, boolean traillingHeaders) {
		super(uri, provider);
		this.traillingHeaders = traillingHeaders;
	}

	abstract public FluentCaseInsensitiveStringsMap getHeaders();

	public boolean isTraillingHeadersReceived() {
		return traillingHeaders;
	}
}
