/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.ion.radon.client.async;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Future;

import junit.framework.TestCase;

import net.ion.radon.client.AsyncHttpClient;
import net.ion.radon.client.AsyncHttpClientConfig;
import net.ion.radon.client.RequestBuilder;
import net.ion.radon.client.Response;
import net.ion.radon.client.generators.InputStreamBodyGenerator;
import net.ion.radon.core.Aradon;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;

public class BodyChunkTest extends ClientBaseTest {

	private final static String MY_MESSAGE = "my message";

	
	protected AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
		return new AsyncHttpClient(config);
	}
	
	public void testNegativeContentType() throws Exception {

		AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
		builder = builder.setConnectionTimeoutInMs(100);
		builder = builder.setMaximumConnectionsTotal(50);
		builder = builder.setRequestTimeoutInMs(5 * 60 * 1000); // 5 minutes

		// Create client
		AsyncHttpClient client = getAsyncHttpClient(builder.build());

		RequestBuilder requestBuilder = new RequestBuilder("POST").setUrl(getTargetUrl()).setHeader("Content-Type", "message/rfc822");

		requestBuilder.setBody(new InputStreamBodyGenerator(new ByteArrayInputStream(MY_MESSAGE.getBytes())));

		Future<Response> future = client.executeRequest(requestBuilder.build());

		Response response = future.get();
		assertEquals(response.getStatusCode(), 200);
		assertEquals(response.getResponseBody(), MY_MESSAGE);

		client.close();
	}


}
