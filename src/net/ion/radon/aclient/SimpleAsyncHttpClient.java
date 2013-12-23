package net.ion.radon.aclient;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import javax.net.ssl.SSLContext;

import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.resumable.ResumableAsyncHandler;
import net.ion.radon.aclient.resumable.ResumableIOExceptionFilter;
import net.ion.radon.aclient.simple.HeaderMap;
import net.ion.radon.aclient.simple.SimpleAHCTransferListener;

import org.restlet.data.Method;

public class SimpleAsyncHttpClient {

	private final ClientConfig config;
	private final RequestBuilder requestBuilder;
	private NewClient asyncHttpClient;
	private final ThrowableHandler defaultThrowableHandler;
	private final boolean resumeEnabled;
	private final ErrorDocumentBehaviour errorDocumentBehaviour;
	private final SimpleAHCTransferListener listener;
	private final boolean derived;

	private SimpleAsyncHttpClient(ClientConfig config, RequestBuilder requestBuilder, ThrowableHandler defaultThrowableHandler, ErrorDocumentBehaviour errorDocumentBehaviour, boolean resumeEnabled, NewClient ahc, SimpleAHCTransferListener listener) {
		this.config = config;
		this.requestBuilder = requestBuilder;
		this.defaultThrowableHandler = defaultThrowableHandler;
		this.resumeEnabled = resumeEnabled;
		this.errorDocumentBehaviour = errorDocumentBehaviour;
		this.asyncHttpClient = ahc;
		this.listener = listener;

		this.derived = ahc != null;
	}

	public Future<Response> post(Part... parts) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.POST);

		for (Part part : parts) {
			r.addBodyPart(part);
		}

		return execute(r, null, null);
	}

	public Future<Response> post(BodyConsumer consumer, Part... parts) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.POST);

		for (Part part : parts) {
			r.addBodyPart(part);
		}

		return execute(r, consumer, null);
	}

	public Future<Response> post(BodyGenerator bodyGenerator) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.POST);
		r.setBody(bodyGenerator);
		return execute(r, null, null);
	}

	public Future<Response> post(BodyGenerator bodyGenerator, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.POST);
		r.setBody(bodyGenerator);
		return execute(r, null, throwableHandler);
	}

	public Future<Response> post(BodyGenerator bodyGenerator, BodyConsumer bodyConsumer) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.POST);
		r.setBody(bodyGenerator);
		return execute(r, bodyConsumer, null);
	}

	public Future<Response> post(BodyGenerator bodyGenerator, BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);
		r.setBody(bodyGenerator);
		return execute(r, bodyConsumer, throwableHandler);
	}

	public Future<Response> put(Part... parts) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);

		for (Part part : parts) {
			r.addBodyPart(part);
		}

		return execute(r, null, null);
	}

	public Future<Response> put(BodyConsumer consumer, Part... parts) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);

		for (Part part : parts) {
			r.addBodyPart(part);
		}

		return execute(r, consumer, null);
	}

	public Future<Response> put(BodyGenerator bodyGenerator, BodyConsumer bodyConsumer) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);
		r.setBody(bodyGenerator);
		return execute(r, bodyConsumer, null);
	}

	public Future<Response> put(BodyGenerator bodyGenerator, BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);
		r.setBody(bodyGenerator);
		return execute(r, bodyConsumer, throwableHandler);
	}

	public Future<Response> put(BodyGenerator bodyGenerator) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);
		r.setBody(bodyGenerator);
		return execute(r, null, null);
	}

	public Future<Response> put(BodyGenerator bodyGenerator, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.PUT);
		r.setBody(bodyGenerator);
		return execute(r, null, throwableHandler);
	}

	public Future<Response> get() throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		return execute(r, null, null);
	}

	public Future<Response> get(ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		return execute(r, null, throwableHandler);
	}

	public Future<Response> get(BodyConsumer bodyConsumer) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		return execute(r, bodyConsumer, null);
	}

	public Future<Response> get(BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		return execute(r, bodyConsumer, throwableHandler);
	}

	public Future<Response> delete() throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.DELETE);
		return execute(r, null, null);
	}

	public Future<Response> delete(ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.DELETE);
		return execute(r, null, throwableHandler);
	}

	public Future<Response> delete(BodyConsumer bodyConsumer) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.DELETE);
		return execute(r, bodyConsumer, null);
	}

	public Future<Response> delete(BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.DELETE);
		return execute(r, bodyConsumer, throwableHandler);
	}

	public Future<Response> head() throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.HEAD);
		return execute(r, null, null);
	}

	public Future<Response> head(ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.HEAD);
		return execute(r, null, throwableHandler);
	}

	public Future<Response> options() throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.OPTIONS);
		return execute(r, null, null);
	}

	public Future<Response> options(ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.OPTIONS);
		return execute(r, null, throwableHandler);
	}

	public Future<Response> options(BodyConsumer bodyConsumer) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.OPTIONS);
		return execute(r, bodyConsumer, null);
	}

	public Future<Response> options(BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		RequestBuilder r = rebuildRequest(requestBuilder.build());
		r.setMethod(Method.OPTIONS);
		return execute(r, bodyConsumer, throwableHandler);
	}

	private RequestBuilder rebuildRequest(Request rb) {
		return new RequestBuilder(rb);
	}

	private Future<Response> execute(RequestBuilder rb, BodyConsumer bodyConsumer, ThrowableHandler throwableHandler) throws IOException {
		if (throwableHandler == null) {
			throwableHandler = defaultThrowableHandler;
		}

		Request request = rb.build();
		ProgressAsyncHandler<Response> handler = new BodyConsumerAsyncHandler(bodyConsumer, throwableHandler, errorDocumentBehaviour, request.getUrl(), listener);

		if (resumeEnabled && request.getMethod().equals(Method.GET) && bodyConsumer != null && bodyConsumer instanceof ResumableBodyConsumer) {
			ResumableBodyConsumer fileBodyConsumer = (ResumableBodyConsumer) bodyConsumer;
			long length = fileBodyConsumer.getTransferredBytes();
			fileBodyConsumer.resume();
			handler = new ResumableBodyConsumerAsyncHandler(length, handler);
		}

		return asyncHttpClient().executeRequest(request, handler);
	}

	private NewClient asyncHttpClient() {
		synchronized (config) {
			if (asyncHttpClient == null) {
				asyncHttpClient = NewClient.create(config);
			}
		}
		return asyncHttpClient;
	}

	public void close() {
		if (!derived && asyncHttpClient != null) {
			asyncHttpClient.close();
		}
	}

	public DerivedBuilder derive() {
		return new Builder(this);
	}

	public enum ErrorDocumentBehaviour {

		WRITE, ACCUMULATE, OMIT;
	}

	public interface DerivedBuilder {

		DerivedBuilder setFollowRedirects(boolean followRedirects);

		DerivedBuilder setVirtualHost(String virtualHost);

		DerivedBuilder setUrl(String url);

		DerivedBuilder setParameters(FluentStringsMap parameters) throws IllegalArgumentException;

		DerivedBuilder setParameters(Map<String, Collection<String>> parameters) throws IllegalArgumentException;

		DerivedBuilder setHeaders(Map<String, Collection<String>> headers);

		DerivedBuilder setHeaders(FluentCaseInsensitiveStringsMap headers);

		DerivedBuilder setHeader(String name, String value);

		DerivedBuilder addQueryParameter(String name, String value);

		DerivedBuilder addParameter(String key, String value) throws IllegalArgumentException;

		DerivedBuilder addHeader(String name, String value);

		DerivedBuilder addCookie(Cookie cookie);

		DerivedBuilder addBodyPart(Part part) throws IllegalArgumentException;

		DerivedBuilder setResumableDownload(boolean resume);

		SimpleAsyncHttpClient build();
	}

	public final static class Builder implements DerivedBuilder {

		private final RequestBuilder requestBuilder;
		private final ClientConfig.Builder configBuilder = new ClientConfig.Builder();
		private Realm.RealmBuilder realmBuilder = null;
		private ProxyServer.Protocol proxyProtocol = null;
		private String proxyHost = null;
		private String proxyPrincipal = null;
		private String proxyPassword = null;
		private int proxyPort = 80;
		private ThrowableHandler defaultThrowableHandler = null;
		private boolean enableResumableDownload = false;
		private ErrorDocumentBehaviour errorDocumentBehaviour = ErrorDocumentBehaviour.WRITE;
		private NewClient ahc = null;
		private SimpleAHCTransferListener listener = null;

		public Builder() {
			requestBuilder = new RequestBuilder(Method.GET, false);
		}

		private Builder(SimpleAsyncHttpClient client) {
			this.requestBuilder = new RequestBuilder(client.requestBuilder.build());
			this.defaultThrowableHandler = client.defaultThrowableHandler;
			this.errorDocumentBehaviour = client.errorDocumentBehaviour;
			this.enableResumableDownload = client.resumeEnabled;
			this.ahc = client.asyncHttpClient();
			this.listener = client.listener;
		}

		public Builder addBodyPart(Part part) throws IllegalArgumentException {
			requestBuilder.addBodyPart(part);
			return this;
		}

		public Builder addCookie(Cookie cookie) {
			requestBuilder.addCookie(cookie);
			return this;
		}

		public Builder addHeader(String name, String value) {
			requestBuilder.addHeader(name, value);
			return this;
		}

		public Builder addParameter(String key, String value) throws IllegalArgumentException {
			requestBuilder.addParameter(key, value);
			return this;
		}

		public Builder addQueryParameter(String name, String value) {
			requestBuilder.addQueryParameter(name, value);
			return this;
		}

		public Builder setHeader(String name, String value) {
			requestBuilder.setHeader(name, value);
			return this;
		}

		public Builder setHeaders(FluentCaseInsensitiveStringsMap headers) {
			requestBuilder.setHeaders(headers);
			return this;
		}

		public Builder setHeaders(Map<String, Collection<String>> headers) {
			requestBuilder.setHeaders(headers);
			return this;
		}

		public Builder setParameters(Map<String, Collection<String>> parameters) throws IllegalArgumentException {
			requestBuilder.setParameters(parameters);
			return this;
		}

		public Builder setParameters(FluentStringsMap parameters) throws IllegalArgumentException {
			requestBuilder.setParameters(parameters);
			return this;
		}

		public Builder setUrl(String url) {
			requestBuilder.setUrl(url);
			return this;
		}

		public Builder setVirtualHost(String virtualHost) {
			requestBuilder.setVirtualHost(virtualHost);
			return this;
		}

		public Builder setFollowRedirects(boolean followRedirects) {
			requestBuilder.setFollowRedirects(followRedirects);
			return this;
		}

		public Builder setMaximumConnectionsTotal(int defaultMaxTotalConnections) {
			configBuilder.setMaximumConnectionsTotal(defaultMaxTotalConnections);
			return this;
		}

		public Builder setMaximumConnectionsPerHost(int defaultMaxConnectionPerHost) {
			configBuilder.setMaximumConnectionsPerHost(defaultMaxConnectionPerHost);
			return this;
		}

		public Builder setConnectionTimeoutInMs(int connectionTimeuot) {
			configBuilder.setConnectionTimeoutInMs(connectionTimeuot);
			return this;
		}

		public Builder setIdleConnectionInPoolTimeoutInMs(int defaultIdleConnectionInPoolTimeoutInMs) {
			configBuilder.setIdleConnectionInPoolTimeoutInMs(defaultIdleConnectionInPoolTimeoutInMs);
			return this;
		}

		public Builder setRequestTimeoutInMs(int defaultRequestTimeoutInMs) {
			configBuilder.setRequestTimeoutInMs(defaultRequestTimeoutInMs);
			return this;
		}

		public Builder setMaximumNumberOfRedirects(int maxDefaultRedirects) {
			configBuilder.setMaximumNumberOfRedirects(maxDefaultRedirects);
			return this;
		}

		public Builder setCompressionEnabled(boolean compressionEnabled) {
			configBuilder.setCompressionEnabled(compressionEnabled);
			return this;
		}

		public Builder setUserAgent(String userAgent) {
			configBuilder.setUserAgent(userAgent);
			return this;
		}

		public Builder setAllowPoolingConnection(boolean allowPoolingConnection) {
			configBuilder.setAllowPoolingConnection(allowPoolingConnection);
			return this;
		}

		public Builder setScheduledExecutorService(ScheduledExecutorService reaper) {
			configBuilder.setScheduledExecutorService(reaper);
			return this;
		}

		public Builder setExecutorService(ExecutorService applicationThreadPool) {
			configBuilder.setExecutorService(applicationThreadPool);
			return this;
		}

		public Builder setSSLEngineFactory(SSLEngineFactory sslEngineFactory) {
			configBuilder.setSSLEngineFactory(sslEngineFactory);
			return this;
		}

		public Builder setSSLContext(final SSLContext sslContext) {
			configBuilder.setSSLContext(sslContext);
			return this;
		}

		public Builder setRequestCompressionLevel(int requestCompressionLevel) {
			configBuilder.setRequestCompressionLevel(requestCompressionLevel);
			return this;
		}

		public Builder setRealmDomain(String domain) {
			realm().setDomain(domain);
			return this;
		}

		public Builder setRealmPrincipal(String principal) {
			realm().setPrincipal(principal);
			return this;
		}

		public Builder setRealmPassword(String password) {
			realm().setPassword(password);
			return this;
		}

		public Builder setRealmScheme(Realm.AuthScheme scheme) {
			realm().setScheme(scheme);
			return this;
		}

		public Builder setRealmName(String realmName) {
			realm().setRealmName(realmName);
			return this;
		}

		public Builder setRealmUsePreemptiveAuth(boolean usePreemptiveAuth) {
			realm().setUsePreemptiveAuth(usePreemptiveAuth);
			return this;
		}

		public Builder setRealmEnconding(String enc) {
			realm().setEnconding(enc);
			return this;
		}

		public Builder setProxyProtocol(ProxyServer.Protocol protocol) {
			this.proxyProtocol = protocol;
			return this;
		}

		public Builder setProxyHost(String host) {
			this.proxyHost = host;
			return this;
		}

		public Builder setProxyPrincipal(String principal) {
			this.proxyPrincipal = principal;
			return this;
		}

		public Builder setProxyPassword(String password) {
			this.proxyPassword = password;
			return this;
		}

		public Builder setProxyPort(int port) {
			this.proxyPort = port;
			return this;
		}

		public Builder setDefaultThrowableHandler(ThrowableHandler throwableHandler) {
			this.defaultThrowableHandler = throwableHandler;
			return this;
		}

		/**
		 * This setting controls whether an error document should be written via the {@link BodyConsumer} after an error status code was received (e.g. 404). Default is {@link ErrorDocumentBehaviour#WRITE}.
		 */
		public Builder setErrorDocumentBehaviour(ErrorDocumentBehaviour behaviour) {
			this.errorDocumentBehaviour = behaviour;
			return this;
		}

		/**
		 * Enable resumable downloads for the SimpleAHC. Resuming downloads will only work for GET requests with an instance of {@link ResumableBodyConsumer}.
		 */
		public Builder setResumableDownload(boolean enableResumableDownload) {
			this.enableResumableDownload = enableResumableDownload;
			return this;
		}

		private Realm.RealmBuilder realm() {
			if (realmBuilder == null) {
				realmBuilder = new Realm.RealmBuilder();
			}
			return realmBuilder;
		}

		public Builder setListener(SimpleAHCTransferListener listener) {
			this.listener = listener;
			return this;
		}

		public Builder setMaxRequestRetry(int maxRequestRetry) {
			configBuilder.setMaxRequestRetry(maxRequestRetry);
			return this;
		}

		public SimpleAsyncHttpClient build() {

			if (realmBuilder != null) {
				configBuilder.setRealm(realmBuilder.build());
			}

			if (proxyHost != null) {
				configBuilder.setProxyServer(new ProxyServer(proxyProtocol, proxyHost, proxyPort, proxyPrincipal, proxyPassword));
			}

			configBuilder.addIOExceptionFilter(new ResumableIOExceptionFilter());

			SimpleAsyncHttpClient sc = new SimpleAsyncHttpClient(configBuilder.build(), requestBuilder, defaultThrowableHandler, errorDocumentBehaviour, enableResumableDownload, ahc, listener);

			return sc;
		}
	}

	private final static class ResumableBodyConsumerAsyncHandler extends ResumableAsyncHandler implements ProgressAsyncHandler<Response> {

		private final ProgressAsyncHandler<Response> delegate;

		public ResumableBodyConsumerAsyncHandler(long byteTransferred, ProgressAsyncHandler<Response> delegate) {
			super(byteTransferred, delegate);
			this.delegate = delegate;
		}

		public net.ion.radon.aclient.AsyncHandler.STATE onHeaderWriteCompleted() {
			return delegate.onHeaderWriteCompleted();
		}

		public net.ion.radon.aclient.AsyncHandler.STATE onContentWriteCompleted() {
			return delegate.onContentWriteCompleted();
		}

		public net.ion.radon.aclient.AsyncHandler.STATE onContentWriteProgress(long amount, long current, long total) {
			return delegate.onContentWriteProgress(amount, current, total);
		}
	}

	private final static class BodyConsumerAsyncHandler extends AsyncCompletionHandlerBase {

		private final BodyConsumer bodyConsumer;
		private final ThrowableHandler exceptionHandler;
		private final ErrorDocumentBehaviour errorDocumentBehaviour;
		private final String url;
		private final SimpleAHCTransferListener listener;

		private boolean accumulateBody = false;
		private boolean omitBody = false;
		private int amount = 0;
		private long total = -1;

		public BodyConsumerAsyncHandler(BodyConsumer bodyConsumer, ThrowableHandler exceptionHandler, ErrorDocumentBehaviour errorDocumentBehaviour, String url, SimpleAHCTransferListener listener) {
			this.bodyConsumer = bodyConsumer;
			this.exceptionHandler = exceptionHandler;
			this.errorDocumentBehaviour = errorDocumentBehaviour;
			this.url = url;
			this.listener = listener;
		}

		@Override
		public void onThrowable(Throwable t) {
			try {
				if (exceptionHandler != null) {
					exceptionHandler.onThrowable(t);
				} else {
					super.onThrowable(t);
				}
			} finally {
				closeConsumer();
			}
		}

		public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
			fireReceived(content);
			if (omitBody) {
				return STATE.CONTINUE;
			}

			if (!accumulateBody && bodyConsumer != null) {
				bodyConsumer.consume(content.getBodyByteBuffer());
			} else {
				return super.onBodyPartReceived(content);
			}
			return STATE.CONTINUE;
		}

		public Response onCompleted(Response response) throws Exception {
			fireCompleted(response);
			closeConsumer();
			return super.onCompleted(response);
		}

		private void closeConsumer() {
			IOUtil.closeQuietly(bodyConsumer) ;
		}

		@Override
		public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
			fireStatus(status);

			if (isErrorStatus(status)) {
				switch (errorDocumentBehaviour) {
				case ACCUMULATE:
					accumulateBody = true;
					break;
				case OMIT:
					omitBody = true;
					break;
				default:
					break;
				}
			}
			return super.onStatusReceived(status);
		}

		private boolean isErrorStatus(HttpResponseStatus status) {
			return status.getStatusCode() >= 400;
		}

		@Override
		public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
			calculateTotal(headers);

			fireHeaders(headers);

			return super.onHeadersReceived(headers);
		}

		private void calculateTotal(HttpResponseHeaders headers) {
			String length = headers.getHeaders().getFirstValue("Content-Length");

			try {
				total = Integer.valueOf(length);
			} catch (Exception e) {
				total = -1;
			}
		}

		@Override
		public STATE onContentWriteProgress(long amount, long current, long total) {
			fireSent(url, amount, current, total);
			return super.onContentWriteProgress(amount, current, total);
		}

		private void fireStatus(HttpResponseStatus status) {
			if (listener != null) {
				listener.onStatus(url, status.getStatusCode(), status.getStatusText());
			}
		}

		private void fireReceived(HttpResponseBodyPart content) {
			int remaining = content.getBodyByteBuffer().remaining();

			amount += remaining;

			if (listener != null) {
				listener.onBytesReceived(url, amount, remaining, total);
			}
		}

		private void fireHeaders(HttpResponseHeaders headers) {
			if (listener != null) {
				listener.onHeaders(url, new HeaderMap(headers.getHeaders()));
			}
		}

		private void fireSent(String url, long amount, long current, long total) {
			if (listener != null) {
				listener.onBytesSent(url, amount, current, total);
			}
		}

		private void fireCompleted(Response response) {
			if (listener != null) {
				listener.onCompleted(url, response.getStatusCode(), response.getStatusText());
			}
		}
	}

}
