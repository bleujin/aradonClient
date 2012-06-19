package net.ion.radon.aclient.providers.simple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.providers.ResponseBase;
import net.ion.radon.aclient.util.AsyncHttpProviderUtils;

public class SimpleResponse extends ResponseBase {
	private final List<Cookie> cookies = new ArrayList<Cookie>();

	public SimpleResponse(HttpResponseStatus status, HttpResponseHeaders headers, List<HttpResponseBodyPart> bodyParts) {
		super(status, headers, bodyParts);
	}

	/* @Override */

	public String getTextBodyExcept(int maxLength) throws IOException {
		return getTextBodyExcerpt(maxLength, DEFAULT_CHARSET);
	}

	public String getTextBodyExcerpt(int maxLength, String charset) throws IOException {
		// should be fine; except that it may split multi-byte chars (last char may become '?')
		byte[] b = AsyncHttpProviderUtils.contentToBytes(bodyParts, maxLength);
		return new String(b, charset);
	}

	/* @Override */
	public List<Cookie> getCookies() {
		if (headers == null) {
			throw new IllegalStateException(HEADERS_NOT_COMPUTED);
		}
		if (cookies.isEmpty()) {
			for (Map.Entry<String, List<String>> header : headers.getHeaders().entrySet()) {
				if (header.getKey().equalsIgnoreCase("Set-Cookie")) {
					// TODO: ask for parsed header
					List<String> v = header.getValue();
					for (String value : v) {
						Cookie cookie = AsyncHttpProviderUtils.parseCookie(value);
						cookies.add(cookie);
					}
				}
			}
		}
		return Collections.unmodifiableList(cookies);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public boolean hasStatus() {
		return (bodyParts != null ? true : false);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public boolean hasHeaders() {
		return (headers != null ? true : false);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public boolean hasBody() {
		return (bodyParts != null && bodyParts.size() > 0 ? true : false);
	}
}
