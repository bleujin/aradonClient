package net.ion.radon.client.async.netty;

import net.ion.radon.client.AsyncHttpClient;
import net.ion.radon.client.AsyncHttpClientConfig;
import net.ion.radon.client.async.ChunkingTest;
import net.ion.radon.client.async.ProviderUtil;


public class NettyChunkingTest extends ChunkingTest {
	@Override
	public AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
		return ProviderUtil.nettyProvider(config);
	}
}
