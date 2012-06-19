package net.ion.radon.aclient;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import net.ion.radon.aclient.filter.IOExceptionFilter;
import net.ion.radon.aclient.filter.RequestFilter;
import net.ion.radon.aclient.filter.ResponseFilter;
import net.ion.radon.aclient.util.ProxyUtils;

public class ClientConfigBean extends ClientConfig {

	public ClientConfigBean() {
		configureExecutors();
		configureDefaults();
		configureFilters();
	}

	void configureFilters() {
		requestFilters = new LinkedList<RequestFilter>();
		responseFilters = new LinkedList<ResponseFilter>();
		ioExceptionFilters = new LinkedList<IOExceptionFilter>();
	}

	void configureDefaults() {
		maxTotalConnections = Integer.getInteger(ASYNC_CLIENT + "defaultMaxTotalConnections", -1);
		maxConnectionPerHost = Integer.getInteger(ASYNC_CLIENT + "defaultMaxConnectionsPerHost", -1);
		connectionTimeOutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultConnectionTimeoutInMS", 60 * 1000);
		idleConnectionInPoolTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultIdleConnectionInPoolTimeoutInMS", 60 * 1000);
		idleConnectionTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultIdleConnectionTimeoutInMS", 60 * 1000);
		requestTimeoutInMs = Integer.getInteger(ASYNC_CLIENT + "defaultRequestTimeoutInMS", 60 * 1000);
		redirectEnabled = Boolean.getBoolean(ASYNC_CLIENT + "defaultRedirectsEnabled");
		maxDefaultRedirects = Integer.getInteger(ASYNC_CLIENT + "defaultMaxRedirects", 5);
		compressionEnabled = Boolean.getBoolean(ASYNC_CLIENT + "compressionEnabled");
		userAgent = System.getProperty(ASYNC_CLIENT + "userAgent", "AradonClient/1.0");

		boolean useProxyProperties = Boolean.getBoolean(ASYNC_CLIENT + "useProxyProperties");
		if (useProxyProperties) {
			proxyServer = ProxyUtils.createProxy(System.getProperties());
		}

		allowPoolingConnection = true;
		requestCompressionLevel = -1;
		maxRequestRetry = 5;
		allowSslConnectionPool = true;
		useRawUrl = false;
		removeQueryParamOnRedirect = true;
		hostnameVerifier = new HostnameVerifier() {

			public boolean verify(String s, SSLSession sslSession) {
				return true;
			}
		};
	}

	void configureExecutors() {
		reaper = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "AsyncHttpClient-Reaper");
				t.setDaemon(true);
				return t;
			}
		});
		applicationThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "AsyncHttpClient-Callback");
				t.setDaemon(true);
				return t;
			}
		});
	}

	public ClientConfigBean setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
		return this;
	}

	public ClientConfigBean setMaxConnectionPerHost(int maxConnectionPerHost) {
		this.maxConnectionPerHost = maxConnectionPerHost;
		return this;
	}

	public ClientConfigBean setConnectionTimeOutInMs(int connectionTimeOutInMs) {
		this.connectionTimeOutInMs = connectionTimeOutInMs;
		return this;
	}

	public ClientConfigBean setIdleConnectionInPoolTimeoutInMs(int idleConnectionInPoolTimeoutInMs) {
		this.idleConnectionInPoolTimeoutInMs = idleConnectionInPoolTimeoutInMs;
		return this;
	}

	public ClientConfigBean setIdleConnectionTimeoutInMs(int idleConnectionTimeoutInMs) {
		this.idleConnectionTimeoutInMs = idleConnectionTimeoutInMs;
		return this;
	}

	public ClientConfigBean setRequestTimeoutInMs(int requestTimeoutInMs) {
		this.requestTimeoutInMs = requestTimeoutInMs;
		return this;
	}

	public ClientConfigBean setRedirectEnabled(boolean redirectEnabled) {
		this.redirectEnabled = redirectEnabled;
		return this;
	}

	public ClientConfigBean setMaxDefaultRedirects(int maxDefaultRedirects) {
		this.maxDefaultRedirects = maxDefaultRedirects;
		return this;
	}

	public ClientConfigBean setCompressionEnabled(boolean compressionEnabled) {
		this.compressionEnabled = compressionEnabled;
		return this;
	}

	public ClientConfigBean setUserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public ClientConfigBean setAllowPoolingConnection(boolean allowPoolingConnection) {
		this.allowPoolingConnection = allowPoolingConnection;
		return this;
	}

	public ClientConfigBean setReaper(ScheduledExecutorService reaper) {
		if (this.reaper != null) {
			this.reaper.shutdownNow();
		}
		this.reaper = reaper;
		return this;
	}

	public ClientConfigBean setApplicationThreadPool(ExecutorService applicationThreadPool) {
		if (this.applicationThreadPool != null) {
			this.applicationThreadPool.shutdownNow();
		}
		this.applicationThreadPool = applicationThreadPool;
		return this;
	}

	public ClientConfigBean setProxyServer(ProxyServer proxyServer) {
		this.proxyServer = proxyServer;
		return this;
	}

	public ClientConfigBean setSslContext(SSLContext sslContext) {
		this.sslContext = sslContext;
		return this;
	}

	public ClientConfigBean setSslEngineFactory(SSLEngineFactory sslEngineFactory) {
		this.sslEngineFactory = sslEngineFactory;
		return this;
	}

	public ClientConfigBean setProviderConfig(AsyncHttpProviderConfig<?, ?> providerConfig) {
		this.providerConfig = providerConfig;
		return this;
	}

	public ClientConfigBean setConnectionsPool(ConnectionsPool<?, ?> connectionsPool) {
		this.connectionsPool = connectionsPool;
		return this;
	}

	public ClientConfigBean setRealm(Realm realm) {
		this.realm = realm;
		return this;
	}

	public ClientConfigBean addRequestFilter(RequestFilter requestFilter) {
		requestFilters.add(requestFilter);
		return this;
	}

	public ClientConfigBean addResponseFilters(ResponseFilter responseFilter) {
		responseFilters.add(responseFilter);
		return this;
	}

	public ClientConfigBean addIoExceptionFilters(IOExceptionFilter ioExceptionFilter) {
		ioExceptionFilters.add(ioExceptionFilter);
		return this;
	}

	public ClientConfigBean setRequestCompressionLevel(int requestCompressionLevel) {
		this.requestCompressionLevel = requestCompressionLevel;
		return this;
	}

	public ClientConfigBean setMaxRequestRetry(int maxRequestRetry) {
		this.maxRequestRetry = maxRequestRetry;
		return this;
	}

	public ClientConfigBean setAllowSslConnectionPool(boolean allowSslConnectionPool) {
		this.allowSslConnectionPool = allowSslConnectionPool;
		return this;
	}

	public ClientConfigBean setUseRawUrl(boolean useRawUrl) {
		this.useRawUrl = useRawUrl;
		return this;
	}

	public ClientConfigBean setRemoveQueryParamOnRedirect(boolean removeQueryParamOnRedirect) {
		this.removeQueryParamOnRedirect = removeQueryParamOnRedirect;
		return this;
	}

	public ClientConfigBean setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
		return this;
	}

	public ClientConfigBean setIoThreadMultiplier(int ioThreadMultiplier) {
		this.ioThreadMultiplier = ioThreadMultiplier;
		return this;
	}
}
