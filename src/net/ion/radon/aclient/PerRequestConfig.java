package net.ion.radon.aclient;

/**
 * @deprecated Per request properties are set on request directly or via builder. This class will be gone in next major release.
 */
public class PerRequestConfig {
	private final ProxyServer proxyServer;
	private int requestTimeoutInMs;

	public PerRequestConfig() {
		this(null, 0);
	}

	public PerRequestConfig(ProxyServer proxyServer, int requestTimeoutInMs) {
		this.proxyServer = proxyServer;
		this.requestTimeoutInMs = requestTimeoutInMs;
	}

	public ProxyServer getProxyServer() {
		return proxyServer;
	}

	public int getRequestTimeoutInMs() {
		return requestTimeoutInMs;
	}

	public void setRequestTimeoutInMs(int requestTimeoutInMs) {
		this.requestTimeoutInMs = requestTimeoutInMs;
	}
}
