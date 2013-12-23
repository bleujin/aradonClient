package net.ion.radon.aclient.providers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.util.AsyncHttpProviderUtils;

import org.restlet.data.Status;

public abstract class ResponseBase implements Response {
	protected final static String DEFAULT_CHARSET = "ISO-8859-1";
	protected final static String HEADERS_NOT_COMPUTED = "Response's headers hasn't been computed by your AsyncHandler.";

	protected final List<HttpResponseBodyPart> bodyParts;
	protected final HttpResponseHeaders headers;
	protected final HttpResponseStatus status;

	protected ResponseBase(HttpResponseStatus status, HttpResponseHeaders headers, List<HttpResponseBodyPart> bodyParts) {
		this.bodyParts = bodyParts;
		this.headers = headers;
		this.status = status;
	}

	public Status getStatus() {
		return Status.valueOf(getStatusCode()) ;
	}
	
	/* @Override */
	public final int getStatusCode() {
		return status.getStatusCode();
	}

	/* @Override */
	public final String getStatusText() {
		return status.getStatusText();
	}

	/* @Override */
	public final URI getUri() /* throws MalformedURLException */{
		return status.getUrl();
	}

	/* @Override */
	public final String getContentType() {
		return getHeader("Content-Type");
	}

	/* @Override */
	public final String getHeader(String name) {
		return getHeaders().getFirstValue(name);
	}

	/* @Override */
	public final List<String> getHeaders(String name) {
		return getHeaders().get(name);
	}

	/* @Override */
	public final FluentCaseInsensitiveStringsMap getHeaders() {
		if (headers == null) {
			throw new IllegalStateException(HEADERS_NOT_COMPUTED);
		}
		return headers.getHeaders();
	}

	/* @Override */
	public final boolean isRedirected() {
		return (status.getStatusCode() >= 300) && (status.getStatusCode() <= 399);
	}

	/* @Override */
	public byte[] getBodyAsBytes() throws IOException {
		return AsyncHttpProviderUtils.contentToBytes(bodyParts);
	}

	/* @Override */
	public String getTextBody() throws IOException {
		String contentType = getContentType();
		String charset = null ;
		if (contentType != null) {
			charset = AsyncHttpProviderUtils.parseCharset(contentType);
		}

		return getTextBody(StringUtil.coalesce(charset, DEFAULT_CHARSET));
	}

	public String getUTF8Body() throws IOException {
		return getTextBody("utf-8");
	}

	public String getTextBody(String charset) throws IOException {
		String contentType = getContentType();
		if (contentType != null && charset == null) {
			charset = AsyncHttpProviderUtils.parseCharset(contentType);
		}

		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}

		return AsyncHttpProviderUtils.contentToString(bodyParts, charset);
	}

	/* @Override */
	public InputStream getBodyAsStream() throws IOException {
		return AsyncHttpProviderUtils.contentAsStream(bodyParts);
	}

	protected String calculateCharset() {
		String charset = null;
		String contentType = getContentType();
		if (contentType != null) {
			charset = AsyncHttpProviderUtils.parseCharset(contentType);
		}

		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}
		return charset;
	}

}
