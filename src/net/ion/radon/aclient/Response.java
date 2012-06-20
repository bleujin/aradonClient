package net.ion.radon.aclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.restlet.data.Status;

public interface Response {

	public int getStatusCode();

	public String getStatusText();

	public byte[] getBodyAsBytes() throws IOException;

	public InputStream getBodyAsStream() throws IOException;

	public String getTextBodyExcerpt(int maxLength, String charset) throws IOException;

	public String getTextBody(String charset) throws IOException;

	public String getTextBodyExcept(int maxLength) throws IOException;

	public String getTextBody() throws IOException;

	public String getUTF8Body() throws IOException;

	public URI getUri() throws MalformedURLException;

	public String getContentType();

	public String getHeader(String name);

	public List<String> getHeaders(String name);

	public FluentCaseInsensitiveStringsMap getHeaders();

	boolean isRedirected();

	public String toString();

	public List<Cookie> getCookies();

	public boolean hasStatus();

	public boolean hasHeaders();

	public boolean hasBody();

	public static class ResponseBuilder {
		private final List<HttpResponseBodyPart> bodies = Collections.synchronizedList(new ArrayList<HttpResponseBodyPart>());
		private HttpResponseStatus status;
		private HttpResponseHeaders headers;

		public ResponseBuilder accumulate(HttpContent httpContent) {
			if (httpContent instanceof HttpResponseStatus) {
				status = (HttpResponseStatus) httpContent;
			} else if (httpContent instanceof HttpResponseHeaders) {
				headers = (HttpResponseHeaders) httpContent;
			} else if (httpContent instanceof HttpResponseBodyPart) {
				bodies.add((HttpResponseBodyPart) httpContent);
			}
			return this;
		}

		public Response build() {
			return status == null ? null : status.provider().prepareResponse(status, headers, bodies);
		}

		public void reset() {
			bodies.clear();
			status = null;
			headers = null;
		}
	}

}