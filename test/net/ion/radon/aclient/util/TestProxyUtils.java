package net.ion.radon.aclient.util;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.ion.radon.aclient.ProxyServer;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;

import org.jboss.netty.handler.codec.http.HttpMethod;

public class TestProxyUtils extends TestCase{
	
	public void testBasics() {
		ProxyServer proxyServer;
		Request req;

		// should avoid, there is no proxy (is null)
		req = new RequestBuilder(HttpMethod.GET).setUrl("http://somewhere.com/foo").build();
		Assert.assertTrue(ProxyUtils.avoidProxy(null, req));

		// should avoid, it's in non-proxy hosts
		req = new RequestBuilder(HttpMethod.GET).setUrl("http://somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("somewhere.com");
		Assert.assertTrue(ProxyUtils.avoidProxy(proxyServer, req));

		// should avoid, it's in non-proxy hosts (with "*")
		req = new RequestBuilder(HttpMethod.GET).setUrl("http://sub.somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("*.somewhere.com");
		Assert.assertTrue(ProxyUtils.avoidProxy(proxyServer, req));

		// should use it
		req = new RequestBuilder(HttpMethod.GET).setUrl("http://sub.somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("*.somewhere.org");
		Assert.assertFalse(ProxyUtils.avoidProxy(proxyServer, req));
	}
}
