package net.ion.radon.aclient;

import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import net.ion.radon.aclient.filter.IOExceptionFilter;
import net.ion.radon.aclient.filter.RequestFilter;
import net.ion.radon.aclient.filter.ResponseFilter;
import net.ion.radon.aclient.util.AllowAllHostnameVerifier;
import net.ion.radon.aclient.util.ProxyUtils;

public class ClientConfig {

	protected final static String ASYNC_CLIENT = ClientConfig.class.getName() + ".";

	protected int maxTotalConnections;
	protected int maxConnectionPerHost;
	protected int connectionTimeOutInMs;
	protected int webSocketIdleTimeoutInMs;
	protected int idleConnectionInPoolTimeoutInMs;
	protected int idleConnectionTimeoutInMs;
	protected int requestTimeoutInMs;
	protected boolean redirectEnabled;
	protected int maxDefaultRedirects;
	protected boolean compressionEnabled;
	protected String userAgent;
	protected boolean allowPoolingConnection;
	protected ScheduledExecutorService reaper;
	protected ExecutorService applicationThreadPool;
	protected ProxyServer proxyServer;
	protected SSLContext sslContext;
	protected SSLEngineFactory sslEngineFactory;
	protected AsyncHttpProviderConfig<?, ?> providerConfig;
	protected ConnectionsPool<?, ?> connectionsPool;
	protected Realm realm;
	protected List<RequestFilter> requestFilters;
	protected List<ResponseFilter> responseFilters;
	protected List<IOExceptionFilter> ioExceptionFilters;
	protected int requestCompressionLevel;
	protected int maxRequestRetry;
	protected boolean allowSslConnectionPool;
	protected boolean useRawUrl;
	protected boolean removeQueryParamOnRedirect;
	protected HostnameVerifier hostnameVerifier;
	protected int ioThreadMultiplier;
	protected boolean strict302Handling;

	protected ClientConfig() {
	}
	
	public final static Builder newBuilder() {
		return new Builder() ;
	}

	private ClientConfig(int maxTotalConnections, int maxConnectionPerHost, int connectionTimeOutInMs, int webSocketTimeoutInMs, int idleConnectionInPoolTimeoutInMs, int idleConnectionTimeoutInMs, int requestTimeoutInMs, boolean redirectEnabled, int maxDefaultRedirects,
			boolean compressionEnabled, String userAgent, boolean keepAlive, ScheduledExecutorService reaper, ExecutorService applicationThreadPool, ProxyServer proxyServer, SSLContext sslContext, SSLEngineFactory sslEngineFactory, AsyncHttpProviderConfig<?, ?> providerConfig,
			ConnectionsPool<?, ?> connectionsPool, Realm realm, List<RequestFilter> requestFilters, List<ResponseFilter> responseFilters, List<IOExceptionFilter> ioExceptionFilters, int requestCompressionLevel, int maxRequestRetry, boolean allowSslConnectionCaching, boolean useRawUrl,
			boolean removeQueryParamOnRedirect, HostnameVerifier hostnameVerifier, int ioThreadMultiplier, boolean strict302Handling) {

		this.maxTotalConnections = maxTotalConnections;
		this.maxConnectionPerHost = maxConnectionPerHost;
		this.connectionTimeOutInMs = connectionTimeOutInMs;
		this.webSocketIdleTimeoutInMs = webSocketTimeoutInMs;
		this.idleConnectionInPoolTimeoutInMs = idleConnectionInPoolTimeoutInMs;
		this.idleConnectionTimeoutInMs = idleConnectionTimeoutInMs;
		this.requestTimeoutInMs = requestTimeoutInMs;
		this.redirectEnabled = redirectEnabled;
		this.maxDefaultRedirects = maxDefaultRedirects;
		this.compressionEnabled = compressionEnabled;
		this.userAgent = userAgent;
		this.allowPoolingConnection = keepAlive;
		this.sslContext = sslContext;
		this.sslEngineFactory = sslEngineFactory;
		this.providerConfig = providerConfig;
		this.connectionsPool = connectionsPool;
		this.realm = realm;
		this.requestFilters = requestFilters;
		this.responseFilters = responseFilters;
		this.ioExceptionFilters = ioExceptionFilters;
		this.requestCompressionLevel = requestCompressionLevel;
		this.maxRequestRetry = maxRequestRetry;
		this.reaper = reaper;
		this.allowSslConnectionPool = allowSslConnectionCaching;
		this.removeQueryParamOnRedirect = removeQueryParamOnRedirect;
		this.hostnameVerifier = hostnameVerifier;
		this.ioThreadMultiplier = ioThreadMultiplier;
		this.strict302Handling = strict302Handling;

		if (applicationThreadPool == null) {
			this.applicationThreadPool = Executors.newCachedThreadPool();
		} else {
			this.applicationThreadPool = applicationThreadPool;
		}
		this.proxyServer = proxyServer;
		this.useRawUrl = useRawUrl;
	}

	public ScheduledExecutorService reaper() {
		return reaper;
	}

	public int getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public int getMaxConnectionPerHost() {
		return maxConnectionPerHost;
	}

	public int getConnectionTimeoutInMs() {
		return connectionTimeOutInMs;
	}

	public int getWebSocketIdleTimeoutInMs() {
		return webSocketIdleTimeoutInMs;
	}

	public int getIdleConnectionTimeoutInMs() {
		return idleConnectionTimeoutInMs;
	}

	public int getIdleConnectionInPoolTimeoutInMs() {
		return idleConnectionInPoolTimeoutInMs;
	}

	public int getRequestTimeoutInMs() {
		return requestTimeoutInMs;
	}

	public boolean isRedirectEnabled() {
		return redirectEnabled;
	}

	public int getMaxRedirects() {
		return maxDefaultRedirects;
	}

	public boolean getAllowPoolingConnection() {
		return allowPoolingConnection;
	}

	public boolean getKeepAlive() {
		return allowPoolingConnection;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public boolean isCompressionEnabled() {
		return compressionEnabled;
	}

	public ExecutorService executorService() {
		return applicationThreadPool;
	}

	public ProxyServer getProxyServer() {
		return proxyServer;
	}

	public SSLContext getSSLContext() {
		return sslContext;
	}

	public ConnectionsPool<?, ?> getConnectionsPool() {
		return connectionsPool;
	}

	public SSLEngineFactory getSSLEngineFactory() {
		if (sslEngineFactory == null) {
			return new SSLEngineFactory() {
				public SSLEngine newSSLEngine() {
					if (sslContext != null) {
						SSLEngine sslEngine = sslContext.createSSLEngine();
						sslEngine.setUseClientMode(true);
						return sslEngine;
					} else {
						return null;
					}
				}
			};
		}
		return sslEngineFactory;
	}

	public AsyncHttpProviderConfig<?, ?> getAsyncHttpProviderConfig() {
		return providerConfig;
	}

	public Realm getRealm() {
		return realm;
	}

	public List<RequestFilter> getRequestFilters() {
		return Collections.unmodifiableList(requestFilters);
	}

	public List<ResponseFilter> getResponseFilters() {
		return Collections.unmodifiableList(responseFilters);
	}

	public List<IOExceptionFilter> getIOExceptionFilters() {
		return Collections.unmodifiableList(ioExceptionFilters);
	}

	public int getRequestCompressionLevel() {
		return requestCompressionLevel;
	}

	public int getMaxRequestRetry() {
		return maxRequestRetry;
	}

	public boolean isSslConnectionPoolEnabled() {
		return allowSslConnectionPool;
	}

	public boolean isUseRawUrl() {
		return useRawUrl;
	}

	public boolean isRemoveQueryParamOnRedirect() {
		return removeQueryParamOnRedirect;
	}

	public boolean isClosed() {
		return applicationThreadPool.isShutdown() || reaper.isShutdown();
	}

	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	public int getIoThreadMultiplier() {
		return ioThreadMultiplier;
	}

	public boolean isStrict302Handling() {
		return strict302Handling;
	}

	public static class Builder {
		private int defaultMaxTotalConnections = Integer.getInteger(ASYNC_CLIENT + "defaultMaxTotalConnections", -1);
		private int defaultMaxConnectionPerHost = Integer.getInteger(ASYNC_CLIENT + "defaultMaxConnectionsPerHost", -1);
		private int defaultConnectionTimeOutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultConnectionTimeoutInMS", 60 * 1000);
		private int defaultWebsocketIdleTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultWebsocketTimoutInMS", 15 * 60 * 1000);
		private int defaultIdleConnectionInPoolTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultIdleConnectionInPoolTimeoutInMS", 60 * 1000);
		private int defaultIdleConnectionTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultIdleConnectionTimeoutInMS", 60 * 1000);
		private int defaultRequestTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultRequestTimeoutInMS", 60 * 1000);
		private boolean redirectEnabled = Boolean.getBoolean(ASYNC_CLIENT + "defaultRedirectsEnabled");
		private int maxDefaultRedirects = Integer.getInteger(ASYNC_CLIENT + "defaultMaxRedirects", 5);
		private boolean compressionEnabled = Boolean.getBoolean(ASYNC_CLIENT + "compressionEnabled");
		private String userAgent = System.getProperty(ASYNC_CLIENT + "userAgent", "AradonClient/1.0");
		private boolean useProxyProperties = Boolean.getBoolean(ASYNC_CLIENT + "useProxyProperties");
		private boolean allowPoolingConnection = true;
		private ScheduledExecutorService reaper = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "AsyncHttpClient-Reaper");
				t.setDaemon(true);
				return t;
			}
		});
		private ExecutorService applicationThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "AsyncHttpClient-Callback");
				t.setDaemon(true);
				return t;
			}
		});
		private ProxyServer proxyServer = null;
		private SSLContext sslContext;
		private SSLEngineFactory sslEngineFactory;
		private AsyncHttpProviderConfig<?, ?> providerConfig;
		private ConnectionsPool<?, ?> connectionsPool;
		private Realm realm;
		private int requestCompressionLevel = -1;
		private int maxRequestRetry = 5;
		private final List<RequestFilter> requestFilters = new LinkedList<RequestFilter>();
		private final List<ResponseFilter> responseFilters = new LinkedList<ResponseFilter>();
		private final List<IOExceptionFilter> ioExceptionFilters = new LinkedList<IOExceptionFilter>();
		private boolean allowSslConnectionPool = true;
		private boolean useRawUrl = false;
		private boolean removeQueryParamOnRedirect = true;
		private HostnameVerifier hostnameVerifier = new AllowAllHostnameVerifier();
		private int ioThreadMultiplier = 2;
		private boolean strict302Handling;

		public Builder() {
		}

		public Builder setMaximumConnectionsTotal(int defaultMaxTotalConnections) {
			this.defaultMaxTotalConnections = defaultMaxTotalConnections;
			return this;
		}

		public Builder setMaximumConnectionsPerHost(int defaultMaxConnectionPerHost) {
			this.defaultMaxConnectionPerHost = defaultMaxConnectionPerHost;
			return this;
		}

		public Builder setConnectionTimeoutInMs(int defaultConnectionTimeOutInMs) {
			this.defaultConnectionTimeOutInMs = defaultConnectionTimeOutInMs;
			return this;
		}

		public Builder setWebSocketIdleTimeoutInMs(int defaultWebSocketIdleTimeoutInMs) {
			this.defaultWebsocketIdleTimeoutInMs = defaultWebSocketIdleTimeoutInMs;
			return this;
		}

		public Builder setIdleConnectionTimeoutInMs(int defaultIdleConnectionTimeoutInMs) {
			this.defaultIdleConnectionTimeoutInMs = defaultIdleConnectionTimeoutInMs;
			return this;
		}

		public Builder setIdleConnectionInPoolTimeoutInMs(int defaultIdleConnectionInPoolTimeoutInMs) {
			this.defaultIdleConnectionInPoolTimeoutInMs = defaultIdleConnectionInPoolTimeoutInMs;
			return this;
		}

		public Builder setRequestTimeoutInMs(int defaultRequestTimeoutInMs) {
			this.defaultRequestTimeoutInMs = defaultRequestTimeoutInMs;
			return this;
		}

		public Builder setFollowRedirects(boolean redirectEnabled) {
			this.redirectEnabled = redirectEnabled;
			return this;
		}

		public Builder setMaximumNumberOfRedirects(int maxDefaultRedirects) {
			this.maxDefaultRedirects = maxDefaultRedirects;
			return this;
		}

		public Builder setCompressionEnabled(boolean compressionEnabled) {
			this.compressionEnabled = compressionEnabled;
			return this;
		}

		public Builder setUserAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		public Builder setAllowPoolingConnection(boolean allowPoolingConnection) {
			this.allowPoolingConnection = allowPoolingConnection;
			return this;
		}

		public Builder setKeepAlive(boolean allowPoolingConnection) {
			this.allowPoolingConnection = allowPoolingConnection;
			return this;
		}

		public Builder setScheduledExecutorService(ScheduledExecutorService reaper) {
			if (this.reaper != null)
				this.reaper.shutdown();
			this.reaper = reaper;
			return this;
		}

		public Builder setExecutorService(ExecutorService applicationThreadPool) {
			if (this.applicationThreadPool != null)
				this.applicationThreadPool.shutdown();
			this.applicationThreadPool = applicationThreadPool;
			return this;
		}

		public Builder setProxyServer(ProxyServer proxyServer) {
			this.proxyServer = proxyServer;
			return this;
		}

		public Builder setSSLEngineFactory(SSLEngineFactory sslEngineFactory) {
			this.sslEngineFactory = sslEngineFactory;
			return this;
		}

		public Builder setSSLContext(final SSLContext sslContext) {
			this.sslEngineFactory = new SSLEngineFactory() {
				public SSLEngine newSSLEngine() throws GeneralSecurityException {
					SSLEngine sslEngine = sslContext.createSSLEngine();
					sslEngine.setUseClientMode(true);
					return sslEngine;
				}
			};
			this.sslContext = sslContext;
			return this;
		}

		public Builder setAsyncHttpClientProviderConfig(AsyncHttpProviderConfig<?, ?> providerConfig) {
			this.providerConfig = providerConfig;
			return this;
		}

		public Builder setConnectionsPool(ConnectionsPool<?, ?> connectionsPool) {
			this.connectionsPool = connectionsPool;
			return this;
		}

		public Builder setRealm(Realm realm) {
			this.realm = realm;
			return this;
		}

		public Builder addRequestFilter(RequestFilter requestFilter) {
			requestFilters.add(requestFilter);
			return this;
		}

		public Builder removeRequestFilter(RequestFilter requestFilter) {
			requestFilters.remove(requestFilter);
			return this;
		}

		public Builder addResponseFilter(ResponseFilter responseFilter) {
			responseFilters.add(responseFilter);
			return this;
		}

		public Builder removeResponseFilter(ResponseFilter responseFilter) {
			responseFilters.remove(responseFilter);
			return this;
		}

		public Builder addIOExceptionFilter(IOExceptionFilter ioExceptionFilter) {
			ioExceptionFilters.add(ioExceptionFilter);
			return this;
		}

		public Builder removeIOExceptionFilter(IOExceptionFilter ioExceptionFilter) {
			ioExceptionFilters.remove(ioExceptionFilter);
			return this;
		}

		public int getRequestCompressionLevel() {
			return requestCompressionLevel;
		}

		public Builder setRequestCompressionLevel(int requestCompressionLevel) {
			this.requestCompressionLevel = requestCompressionLevel;
			return this;
		}

		public Builder setMaxRequestRetry(int maxRequestRetry) {
			this.maxRequestRetry = maxRequestRetry;
			return this;
		}

		public Builder setAllowSslConnectionPool(boolean allowSslConnectionPool) {
			this.allowSslConnectionPool = allowSslConnectionPool;
			return this;
		}

		public Builder setUseRawUrl(boolean useRawUrl) {
			this.useRawUrl = useRawUrl;
			return this;
		}

		public Builder setRemoveQueryParamsOnRedirect(boolean removeQueryParamOnRedirect) {
			this.removeQueryParamOnRedirect = removeQueryParamOnRedirect;
			return this;
		}

		public Builder setUseProxyProperties(boolean useProxyProperties) {
			this.useProxyProperties = useProxyProperties;
			return this;
		}

		public Builder setIOThreadMultiplier(int multiplier) {
			this.ioThreadMultiplier = multiplier;
			return this;
		}

		public Builder setHostnameVerifier(HostnameVerifier hostnameVerifier) {
			this.hostnameVerifier = hostnameVerifier;
			return this;
		}

		public Builder setStrict302Handling(final boolean strict302Handling) {
			this.strict302Handling = strict302Handling;
			return this;
		}

		public Builder(ClientConfig prototype) {
			allowPoolingConnection = prototype.getAllowPoolingConnection();
			providerConfig = prototype.getAsyncHttpProviderConfig();
			connectionsPool = prototype.getConnectionsPool();
			defaultConnectionTimeOutInMs = prototype.getConnectionTimeoutInMs();
			defaultIdleConnectionInPoolTimeoutInMs = prototype.getIdleConnectionInPoolTimeoutInMs();
			defaultIdleConnectionTimeoutInMs = prototype.getIdleConnectionTimeoutInMs();
			defaultMaxConnectionPerHost = prototype.getMaxConnectionPerHost();
			maxDefaultRedirects = prototype.getMaxRedirects();
			defaultMaxTotalConnections = prototype.getMaxTotalConnections();
			proxyServer = prototype.getProxyServer();
			realm = prototype.getRealm();
			defaultRequestTimeoutInMs = prototype.getRequestTimeoutInMs();
			sslContext = prototype.getSSLContext();
			sslEngineFactory = prototype.getSSLEngineFactory();
			userAgent = prototype.getUserAgent();
			redirectEnabled = prototype.isRedirectEnabled();
			compressionEnabled = prototype.isCompressionEnabled();
			reaper = prototype.reaper();
			applicationThreadPool = prototype.executorService();

			requestFilters.clear();
			responseFilters.clear();
			ioExceptionFilters.clear();

			requestFilters.addAll(prototype.getRequestFilters());
			responseFilters.addAll(prototype.getResponseFilters());
			ioExceptionFilters.addAll(prototype.getIOExceptionFilters());

			requestCompressionLevel = prototype.getRequestCompressionLevel();
			useRawUrl = prototype.isUseRawUrl();
			ioThreadMultiplier = prototype.getIoThreadMultiplier();
			maxRequestRetry = prototype.getMaxRequestRetry();
			allowSslConnectionPool = prototype.getAllowPoolingConnection();
			removeQueryParamOnRedirect = prototype.isRemoveQueryParamOnRedirect();
			hostnameVerifier = prototype.getHostnameVerifier();
			strict302Handling = prototype.isStrict302Handling();
		}

		public ClientConfig build() {

			if (applicationThreadPool.isShutdown()) {
				throw new IllegalStateException("ExecutorServices closed");
			}

			if (proxyServer == null && useProxyProperties) {
				proxyServer = ProxyUtils.createProxy(System.getProperties());
			}

			return new ClientConfig(defaultMaxTotalConnections, defaultMaxConnectionPerHost, defaultConnectionTimeOutInMs, defaultWebsocketIdleTimeoutInMs, defaultIdleConnectionInPoolTimeoutInMs, defaultIdleConnectionTimeoutInMs, defaultRequestTimeoutInMs, redirectEnabled,
					maxDefaultRedirects, compressionEnabled, userAgent, allowPoolingConnection, reaper, applicationThreadPool, proxyServer, sslContext, sslEngineFactory, providerConfig, connectionsPool, realm, requestFilters, responseFilters, ioExceptionFilters, requestCompressionLevel,
					maxRequestRetry, allowSslConnectionPool, useRawUrl, removeQueryParamOnRedirect, hostnameVerifier, ioThreadMultiplier, strict302Handling);
		}
	}
}
