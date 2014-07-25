package net.ion.radon.aclient;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import net.ion.radon.aclient.Request.EntityWriter;

import org.jboss.netty.handler.codec.http.HttpMethod;


public class RequestBuilder extends RequestBuilderBase<RequestBuilder> {

	public RequestBuilder() {
		super(RequestBuilder.class, HttpMethod.GET, false);
	}

	public RequestBuilder(HttpMethod method) {
		super(RequestBuilder.class, method, false);
	}

	public RequestBuilder(HttpMethod method, boolean useRawUrl) {
		super(RequestBuilder.class, method, useRawUrl);
	}

	public RequestBuilder(Request prototype) {
		super(RequestBuilder.class, prototype);
	}

	// Note: For now we keep the delegates in place even though they are not needed
	// since otherwise Clojure (and maybe other languages) won't be able to
	// access these methods - see Clojure tickets 126 and 259

	@Override
	public RequestBuilder addBodyPart(Part part) throws IllegalArgumentException {
		return super.addBodyPart(part);
	}

	@Override
	public RequestBuilder addCookie(Cookie cookie) {
		return super.addCookie(cookie);
	}

	@Override
	public RequestBuilder addHeader(String name, String value) {
		return super.addHeader(name, value);
	}

	@Override
	public RequestBuilder addParameter(String key, String value) throws IllegalArgumentException {
		return super.addParameter(key, value);
	}

	@Override
	public RequestBuilder addQueryParameter(String name, String value) {
		return super.addQueryParameter(name, value);
	}

	@Override
	public RequestBuilder setQueryParameters(FluentStringsMap parameters) {
		return super.setQueryParameters(parameters);
	}

	@Override
	public Request build() {
		return super.build();
	}

	@Override
	public RequestBuilder setBody(byte[] data) throws IllegalArgumentException {
		return super.setBody(data);
	}

	@Override
	public RequestBuilder setBody(EntityWriter dataWriter, long length) throws IllegalArgumentException {
		return super.setBody(dataWriter, length);
	}

	@Override
	public RequestBuilder setBody(EntityWriter dataWriter) {
		return super.setBody(dataWriter);
	}

	@Override
	@Deprecated
	public RequestBuilder setBody(InputStream stream) throws IllegalArgumentException {
		return super.setBody(stream);
	}

	@Override
	public RequestBuilder setBody(String data) throws IllegalArgumentException {
		return super.setBody(data);
	}

	@Override
	public RequestBuilder setHeader(String name, String value) {
		return super.setHeader(name, value);
	}

	@Override
	public RequestBuilder setHeaders(FluentCaseInsensitiveStringsMap headers) {
		return super.setHeaders(headers);
	}

	@Override
	public RequestBuilder setHeaders(Map<String, Collection<String>> headers) {
		return super.setHeaders(headers);
	}

	@Override
	public RequestBuilder setParameters(Map<String, Collection<String>> parameters) throws IllegalArgumentException {
		return super.setParameters(parameters);
	}

	@Override
	public RequestBuilder setParameters(FluentStringsMap parameters) throws IllegalArgumentException {
		return super.setParameters(parameters);
	}

	@Override
	public RequestBuilder setMethod(HttpMethod method) {
		return super.setMethod(method);
	}

	@Override
	public RequestBuilder setUrl(String url) {
		return super.setUrl(url);
	}

	@Override
	public RequestBuilder setProxyServer(ProxyServer proxyServer) {
		return super.setProxyServer(proxyServer);
	}

	@Override
	public RequestBuilder setVirtualHost(String virtualHost) {
		return super.setVirtualHost(virtualHost);
	}

	@Override
	public RequestBuilder setFollowRedirects(boolean followRedirects) {
		return super.setFollowRedirects(followRedirects);
	}

	@Override
	public RequestBuilder addOrReplaceCookie(Cookie c) {
		return super.addOrReplaceCookie(c);
	}
}
