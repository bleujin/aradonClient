package net.ion.radon.aclient.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import net.ion.radon.aclient.Cookie;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.Response;

import org.restlet.data.Status;
import org.w3c.dom.Document;

public class WebDavResponse implements Response {

	private final Response response;
	private final Document document;

	public WebDavResponse(Response response, Document document) {
		this.response = response;
		this.document = document;
	}

	public Status getStatus() {
		return response.getStatus() ;
	}	
	
	public int getStatusCode() {
		return response.getStatusCode();
	}

	public String getStatusText() {
		return response.getStatusText();
	}

	/* @Override */
	public byte[] getBodyAsBytes() throws IOException {
		return response.getBodyAsBytes();
	}

	public InputStream getBodyAsStream() throws IOException {
		return response.getBodyAsStream();
	}

	public String getTextBodyExcept(int maxLength) throws IOException {
		return response.getTextBodyExcept(maxLength);
	}

	public String getTextBodyExcerpt(int maxLength, String charset) throws IOException {
		return response.getTextBodyExcerpt(maxLength, charset);
	}

	public String getTextBody() throws IOException {
		return response.getTextBody();
	}

	public String getUTF8Body() throws IOException {
		return response.getTextBody("UTF-8");
	}

	public String getTextBody(String charset) throws IOException {
		return response.getTextBody(charset);
	}

	public URI getUri() throws MalformedURLException {
		return response.getUri();
	}

	public String getContentType() {
		return response.getContentType();
	}

	public String getHeader(String name) {
		return response.getHeader(name);
	}

	public List<String> getHeaders(String name) {
		return response.getHeaders(name);
	}

	public FluentCaseInsensitiveStringsMap getHeaders() {
		return response.getHeaders();
	}

	public boolean isRedirected() {
		return response.isRedirected();
	}

	public List<Cookie> getCookies() {
		return response.getCookies();
	}

	public boolean hasStatus() {
		return response.hasStatus();
	}

	public boolean hasHeaders() {
		return response.hasHeaders();
	}

	public boolean hasBody() {
		return response.hasBody();
	}

	public Document getBodyAsXML() {
		return document;
	}
}
