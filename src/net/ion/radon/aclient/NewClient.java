package net.ion.radon.aclient;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.AsyncHandler.STATE;
import net.ion.radon.aclient.Request.EntityWriter;
import net.ion.radon.aclient.filter.FilterContext;
import net.ion.radon.aclient.filter.FilterException;
import net.ion.radon.aclient.filter.RequestFilter;
import net.ion.radon.aclient.providers.netty.NettyProvider;
import net.ion.radon.aclient.providers.simple.SimpleProvider;
import net.ion.radon.aclient.resumable.ResumableAsyncHandler;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

// This project is an adaptation of the Apache AsyncHttpClient implementation

public class NewClient implements Closeable {

	private final static String DEFAULT_PROVIDER = NettyProvider.class.getCanonicalName();
	private final AsyncHttpProvider httpProvider;
	private final ClientConfig config;
	private final AtomicBoolean isClosed = new AtomicBoolean(false);

	protected SignatureCalculator signatureCalculator;

	private NewClient(AsyncHttpProvider httpProvider, ClientConfig config) {
		this.config = config;
		this.httpProvider = httpProvider;
	}

	public final static NewClient create() {
		ClientConfig config = new ClientConfig.Builder().build();
		return create(loadDefaultProvider(DEFAULT_PROVIDER, config), config);
	}

	public final static NewClient create(ClientConfig config) {
		return create(loadDefaultProvider(DEFAULT_PROVIDER, config), config);
	}

	public final static NewClient create(AsyncHttpProvider httpProvider, ClientConfig config) {
		return new NewClient(httpProvider, config);
	}

	public class BoundRequestBuilder extends RequestBuilderBase<BoundRequestBuilder> {
		protected SignatureCalculator signatureCalculator;

		protected String baseURL;

		private BoundRequestBuilder(Method method, boolean useRawUrl) {
			super(BoundRequestBuilder.class, method, useRawUrl);
		}

		private BoundRequestBuilder(Request prototype) {
			super(BoundRequestBuilder.class, prototype);
		}

		public <T> ListenableFuture<T> execute(AsyncHandler<T> handler) throws IOException {
			return NewClient.this.executeRequest(build(), handler);
		}

		public ListenableFuture<Response> execute() throws IOException {
			return NewClient.this.executeRequest(build(), new AsyncCompletionHandlerBase());
		}

		// Note: For now we keep the delegates in place even though they are not needed
		// since otherwise Clojure (and maybe other languages) won't be able to
		// access these methods - see Clojure tickets 126 and 259

		@Override
		public BoundRequestBuilder addBodyPart(Part part) throws IllegalArgumentException {
			return super.addBodyPart(part);
		}

		@Override
		public BoundRequestBuilder addCookie(Cookie cookie) {
			return super.addCookie(cookie);
		}

		@Override
		public BoundRequestBuilder addHeader(String name, String value) {
			return super.addHeader(name, value);
		}

		@Override
		public BoundRequestBuilder addParameter(String key, String value) throws IllegalArgumentException {
			return super.addParameter(key, value);
		}

		@Override
		public BoundRequestBuilder addQueryParameter(String name, String value) {
			return super.addQueryParameter(name, value);
		}

		@Override
		public Request build() {
			/*
			 * Let's first calculate and inject signature, before finalizing actual build (order does not matter with current implementation but may in future)
			 */
			if (signatureCalculator != null) {
				String url = baseURL;
				// Should not include query parameters, ensure:
				int i = url.indexOf('?');
				if (i >= 0) {
					url = url.substring(0, i);
				}
				signatureCalculator.calculateAndAddSignature(url, request, this);
			}
			return super.build();
		}

		@Override
		public BoundRequestBuilder setBody(byte[] data) throws IllegalArgumentException {
			return super.setBody(data);
		}

		@Override
		public BoundRequestBuilder setBody(EntityWriter dataWriter, long length) throws IllegalArgumentException {
			return super.setBody(dataWriter, length);
		}

		@Override
		public BoundRequestBuilder setBody(EntityWriter dataWriter) {
			return super.setBody(dataWriter);
		}

		@Override
		public BoundRequestBuilder setBody(InputStream stream) throws IllegalArgumentException {
			return super.setBody(stream);
		}

		@Override
		public BoundRequestBuilder setBody(String data) throws IllegalArgumentException {
			return super.setBody(data);
		}

		@Override
		public BoundRequestBuilder setHeader(String name, String value) {
			return super.setHeader(name, value);
		}

		@Override
		public BoundRequestBuilder setHeaders(FluentCaseInsensitiveStringsMap headers) {
			return super.setHeaders(headers);
		}

		@Override
		public BoundRequestBuilder setHeaders(Map<String, Collection<String>> headers) {
			return super.setHeaders(headers);
		}

		@Override
		public BoundRequestBuilder setParameters(Map<String, Collection<String>> parameters) throws IllegalArgumentException {
			return super.setParameters(parameters);
		}

		@Override
		public BoundRequestBuilder setParameters(FluentStringsMap parameters) throws IllegalArgumentException {
			return super.setParameters(parameters);
		}

		@Override
		public BoundRequestBuilder setUrl(String url) {
			baseURL = url;
			return super.setUrl(url);
		}

		@Override
		public BoundRequestBuilder setVirtualHost(String virtualHost) {
			return super.setVirtualHost(virtualHost);
		}

		public BoundRequestBuilder setSignatureCalculator(SignatureCalculator signatureCalculator) {
			this.signatureCalculator = signatureCalculator;
			return this;
		}
	}

	public AsyncHttpProvider getProvider() {
		return httpProvider;
	}

	public void close() {
		httpProvider.close();
		isClosed.set(true);
	}

	public void closeAsynchronously() {
		config.applicationThreadPool.submit(new Runnable() {

			public void run() {
				httpProvider.close();
				isClosed.set(true);
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (!isClosed.get()) {
				httpProvider.close();
				Debug.error("AsyncHttpClient.close() hasn't been invoked, which may produce file descriptor leaks");
			}
		} finally {
			super.finalize();
		}
	}

	public boolean isClosed() {
		return isClosed.get();
	}

	public ClientConfig getConfig() {
		return config;
	}

	public NewClient setSignatureCalculator(SignatureCalculator signatureCalculator) {
		this.signatureCalculator = signatureCalculator;
		return this;
	}

	public BoundRequestBuilder prepareGet(String url) {
		return requestBuilder(Method.GET, url);
	}

	public BoundRequestBuilder prepareConnect(String url) {
		return requestBuilder(Method.CONNECT, url);
	}

	public BoundRequestBuilder prepareOptions(String url) {
		return requestBuilder(Method.OPTIONS, url);
	}

	public BoundRequestBuilder prepareHead(String url) {
		return requestBuilder(Method.HEAD, url);
	}

	public BoundRequestBuilder preparePost(String url) {
		return requestBuilder(Method.POST, url);
	}

	public BoundRequestBuilder preparePut(String url) {
		return requestBuilder(Method.PUT, url);
	}

	public BoundRequestBuilder prepareDelete(String url) {
		return requestBuilder(Method.DELETE, url);
	}

	public BoundRequestBuilder prepareRequest(Request request) {
		return requestBuilder(request);
	}

	public <T> ListenableFuture<T> executeRequest(Request request, AsyncHandler<T> handler) throws IOException {

		FilterContext<T> fc = new FilterContext.FilterContextBuilder<T>().asyncHandler(handler).request(request).build();
		fc = preProcessRequest(fc);

		return httpProvider.execute(fc.getRequest(), fc.getAsyncHandler());
	}

	public ListenableFuture<Response> executeRequest(Request request) throws IOException {
		FilterContext<Response> fc = new FilterContext.FilterContextBuilder<Response>().asyncHandler(new AsyncCompletionHandlerBase()).request(request).build();
		fc = preProcessRequest(fc);
		return httpProvider.execute(fc.getRequest(), fc.getAsyncHandler());
	}

	private <T> FilterContext<T> preProcessRequest(FilterContext<T> fc) throws IOException {
		for (RequestFilter asyncFilter : config.getRequestFilters()) {
			try {
				fc = asyncFilter.filter(fc);
				if (fc == null) {
					throw new NullPointerException("FilterContext is null");
				}
			} catch (FilterException e) {
				IOException ex = new IOException();
				ex.initCause(e);
				throw ex;
			}
		}

		Request request = fc.getRequest();
		if (ResumableAsyncHandler.class.isAssignableFrom(fc.getAsyncHandler().getClass())) {
			request = ResumableAsyncHandler.class.cast(fc.getAsyncHandler()).adjustRequestRange(request);
		}

		if (request.getRangeOffset() != 0) {
			RequestBuilder builder = new RequestBuilder(request);
			builder.setHeader("Range", "bytes=" + request.getRangeOffset() + "-");
			request = builder.build();
		}
		fc = new FilterContext.FilterContextBuilder<T>(fc).request(request).build();
		return fc;
	}

	@SuppressWarnings("unchecked")
	private final static AsyncHttpProvider loadDefaultProvider(String className, ClientConfig config) {
		try {
			Class<AsyncHttpProvider> providerClass = (Class<AsyncHttpProvider>) Thread.currentThread().getContextClassLoader().loadClass(className);
			return providerClass.getDeclaredConstructor(new Class[] { ClientConfig.class }).newInstance(new Object[] { config });
		} catch (Throwable t) {

			// Let's try with another classloader
			try {
				Class<AsyncHttpProvider> providerClass = (Class<AsyncHttpProvider>) NewClient.class.getClassLoader().loadClass(className);
				return providerClass.getDeclaredConstructor(new Class[] { ClientConfig.class }).newInstance(new Object[] { config });
			} catch (Throwable t2) {
			}

			return new SimpleProvider(config);
		}
	}

	protected BoundRequestBuilder requestBuilder(Method method, String url) {
		return new BoundRequestBuilder(method, config.isUseRawUrl()).setUrl(url).setSignatureCalculator(signatureCalculator);
	}

	protected BoundRequestBuilder requestBuilder(Request prototype) {
		return new BoundRequestBuilder(prototype).setSignatureCalculator(signatureCalculator);
	}

	public ISerialAsyncRequest createSerialRequest(String fullPath) {
		return HttpSerialRequest.create(this, fullPath);
	}
}

class HttpSerialRequest implements ISerialAsyncRequest {

	private NewClient client;

	private RequestBuilder builder = new RequestBuilder();

	public HttpSerialRequest(NewClient newClient, String fullPath) {
		this.client = newClient;
		builder.setUrl(fullPath);
	}

	public RequestBuilder builder() {
		return builder;
	}

	public static ISerialAsyncRequest create(NewClient newClient, String fullPath) {
		return new HttpSerialRequest(newClient, fullPath);
	}

	public ISerialAsyncRequest addHeader(String name, String value) {
		builder.addHeader(name, value);
		return this;
	}

	public <V> ListenableFuture<V> delete(Class<? extends V> clz) {
		return handle(Method.DELETE, "", clz);
	}

	public <V> ListenableFuture<V> get(Class<? extends V> clz) {
		return handle(Method.GET, "", clz);
	}

	public <T, V> ListenableFuture<V> handle(Method method, T arg, final Class<? extends V> clz) {
		builder.setMethod(method);
		builder.addHeader("Content-Type", "application/x-java-serialized-object") ;

		try {
			if (!(method.equals(Method.GET) || method.equals(Method.DELETE) || method.equals(Method.HEAD))) {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				ObjectOutputStream output = new ObjectOutputStream(bout);
				output.writeObject(arg);
				byte[] data = bout.toByteArray();
				output.close();
				builder.setBody(data);
			}
		} catch (IOException ex) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, ex.getMessage());
		}

		Request req = builder.build();
		try {
			AsyncHandler<V> handler = new AsyncCompletionHandler<V>(){
				public V onCompleted(Response response) throws Exception{
					Status st = Status.valueOf(response.getStatusCode());
					
					if (!st.isSuccess())
						throw new ResourceException(st, response.getTextBody());

					ObjectInputStream oinput = new ObjectInputStream(response.getBodyAsStream());
					V result = clz.cast(oinput.readObject());

					return result;
				}
			} ;
			
			ListenableFuture<V> future = client.prepareRequest(req).execute(handler);
			return future;
		} catch (IOException ex) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, ex.getMessage()) ;
		}

	}

	public <T, V> ListenableFuture<V> post(T arg, Class<? extends V> clz) {
		return handle(Method.POST, arg, clz);
	}

	public <T, V> ListenableFuture<V> put(T arg, Class<? extends V> clz) {
		return handle(Method.PUT, arg, clz);
	}

}
