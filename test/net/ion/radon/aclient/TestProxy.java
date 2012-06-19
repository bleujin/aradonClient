package net.ion.radon.aclient;

import java.util.concurrent.Future;

public class TestProxy extends TestBaseClient{

	public void testProxy() throws Exception {
		NewClient c = newClient() ;
		Future<Response> future = c.prepareGet(getHelloUri())
			.setProxyServer(new ProxyServer("127.0.0.1", 8080)).execute() ;
	}
	
	
	public void testSSLProxy() throws Exception {
		NewClient c = newClient() ;
		Future<Response> future = c.prepareGet(getHelloUri())
			.setProxyServer(new ProxyServer(ProxyServer.Protocol.HTTPS, "127.0.0.1", 8080)).execute() ;
	}
}
