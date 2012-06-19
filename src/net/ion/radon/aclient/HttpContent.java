package net.ion.radon.aclient;

import java.net.URI;

public class HttpContent {
	protected final AsyncHttpProvider provider;
	protected final URI uri;

	protected HttpContent(URI url, AsyncHttpProvider provider) {
		this.provider = provider;
		this.uri = url;
	}

	public final AsyncHttpProvider provider() {
		return provider;
	}

	public final URI getUrl() {
		return uri;
	}
}
