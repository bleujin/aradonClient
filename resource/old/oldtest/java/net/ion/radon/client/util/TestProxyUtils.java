/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package net.ion.radon.client.util;

import junit.framework.TestCase;
import net.ion.radon.client.ProxyServer;
import net.ion.radon.client.Request;
import net.ion.radon.client.RequestBuilder;
import net.ion.radon.client.util.ProxyUtils;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestProxyUtils extends TestCase{
	
	public void testBasics() {
		ProxyServer proxyServer;
		Request req;

		// should avoid, there is no proxy (is null)
		req = new RequestBuilder("GET").setUrl("http://somewhere.com/foo").build();
		Assert.assertTrue(ProxyUtils.avoidProxy(null, req));

		// should avoid, it's in non-proxy hosts
		req = new RequestBuilder("GET").setUrl("http://somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("somewhere.com");
		Assert.assertTrue(ProxyUtils.avoidProxy(proxyServer, req));

		// should avoid, it's in non-proxy hosts (with "*")
		req = new RequestBuilder("GET").setUrl("http://sub.somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("*.somewhere.com");
		Assert.assertTrue(ProxyUtils.avoidProxy(proxyServer, req));

		// should use it
		req = new RequestBuilder("GET").setUrl("http://sub.somewhere.com/foo").build();
		proxyServer = new ProxyServer("foo", 1234);
		proxyServer.addNonProxyHost("*.somewhere.org");
		Assert.assertFalse(ProxyUtils.avoidProxy(proxyServer, req));
	}
}
