package net.ion.radon.aclient.providers.netty;

import java.net.URI;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.HttpResponseHeaders;

import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * A class that represent the HTTP headers.
 */
public class ResponseHeaders extends HttpResponseHeaders {

	private final HttpChunkTrailer trailingHeaders;
	private final HttpResponse response;
	private final FluentCaseInsensitiveStringsMap headers;

	public ResponseHeaders(URI uri, HttpResponse response, AsyncHttpProvider provider) {
		super(uri, provider, false);
		this.trailingHeaders = null;
		this.response = response;
		headers = computerHeaders();
	}

	public ResponseHeaders(URI uri, HttpResponse response, AsyncHttpProvider provider, HttpChunkTrailer traillingHeaders) {
		super(uri, provider, true);
		this.trailingHeaders = traillingHeaders;
		this.response = response;
		headers = computerHeaders();
	}

	private FluentCaseInsensitiveStringsMap computerHeaders() {
		FluentCaseInsensitiveStringsMap h = new FluentCaseInsensitiveStringsMap();
		for (String s : response.getHeaderNames()) {
			for (String header : response.getHeaders(s)) {
				h.add(s, header);
			}
		}

		if (trailingHeaders != null && trailingHeaders.getHeaderNames().size() > 0) {
			for (final String s : trailingHeaders.getHeaderNames()) {
				for (String header : response.getHeaders(s)) {
					h.add(s, header);
				}
			}
		}

		return h;
	}

	/**
	 * Return the HTTP header
	 * 
	 * @return an {@link net.ion.radon.aclient.FluentCaseInsensitiveStringsMap}
	 */
	@Override
	public FluentCaseInsensitiveStringsMap getHeaders() {
		return headers;
	}
}
