package net.ion.radon.aclient.util;

import java.net.URI;
import java.util.List;
import java.util.Properties;

import net.ion.radon.aclient.ProxyServer;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.ProxyServer.Protocol;

public class ProxyUtils {

	private static final String PROPERTY_PREFIX = "net.ion.radon.aclient.AsyncHttpClientConfig.proxy.";

	public static final String PROXY_HOST = "http.proxyHost";

	public static final String PROXY_PORT = "http.proxyPort";

	public static final String PROXY_PROTOCOL = PROPERTY_PREFIX + "protocol";

	public static final String PROXY_NONPROXYHOSTS = "http.nonProxyHosts";

	public static final String PROXY_USER = PROPERTY_PREFIX + "user";

	public static final String PROXY_PASSWORD = PROPERTY_PREFIX + "password";

	public static boolean avoidProxy(final ProxyServer proxyServer, final Request request) {
		return avoidProxy(proxyServer, AsyncHttpProviderUtils.getHost(URI.create(request.getUrl())));
	}

	public static boolean avoidProxy(final ProxyServer proxyServer, final String target) {
		if (proxyServer != null) {
			final String targetHost = target.toLowerCase();

			List<String> nonProxyHosts = proxyServer.getNonProxyHosts();

			if (nonProxyHosts != null && nonProxyHosts.size() > 0) {
				for (String nonProxyHost : nonProxyHosts) {
					if (nonProxyHost.startsWith("*") && nonProxyHost.length() > 1 && targetHost.endsWith(nonProxyHost.substring(1).toLowerCase())) {
						return true;
					} else if (nonProxyHost.equalsIgnoreCase(targetHost)) {
						return true;
					}
				}
			}

			return false;
		} else {
			return true;
		}
	}

	public static ProxyServer createProxy(Properties properties) {
		String host = System.getProperty(PROXY_HOST);

		if (host != null) {
			int port = Integer.valueOf(System.getProperty(PROXY_PORT, "80"));

			Protocol protocol;
			try {
				protocol = Protocol.valueOf(System.getProperty(PROXY_PROTOCOL, "HTTP"));
			} catch (IllegalArgumentException e) {
				protocol = Protocol.HTTP;
			}

			ProxyServer proxyServer = new ProxyServer(protocol, host, port, System.getProperty(PROXY_USER), System.getProperty(PROXY_PASSWORD));

			String nonProxyHosts = System.getProperties().getProperty(PROXY_NONPROXYHOSTS);
			if (nonProxyHosts != null) {
				for (String spec : nonProxyHosts.split("\\|")) {
					proxyServer.addNonProxyHost(spec);
				}
			}

			return proxyServer;
		}

		return null;
	}
}
