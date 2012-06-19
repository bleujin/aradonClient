package net.ion.radon.aclient.providers.simple;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.HttpResponseHeaders;

/**
 * A class that represent the HTTP headers.
 */
public class ResponseHeaders extends HttpResponseHeaders {

	private final HttpURLConnection urlConnection;
	private final FluentCaseInsensitiveStringsMap headers;

	public ResponseHeaders(URI uri, HttpURLConnection urlConnection, AsyncHttpProvider provider) {
		super(uri, provider, false);
		this.urlConnection = urlConnection;
		headers = computerHeaders();
	}

	private FluentCaseInsensitiveStringsMap computerHeaders() {
		FluentCaseInsensitiveStringsMap h = new FluentCaseInsensitiveStringsMap();

		Map<String, List<String>> uh = urlConnection.getHeaderFields();

		for (Map.Entry<String, List<String>> e : uh.entrySet()) {
			if (e.getKey() != null) {
				h.add(e.getKey(), e.getValue());
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