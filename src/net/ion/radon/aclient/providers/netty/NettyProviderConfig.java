package net.ion.radon.aclient.providers.netty;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.radon.aclient.AsyncHttpProviderConfig;

public class NettyProviderConfig implements AsyncHttpProviderConfig<String, Object> {

	/**
	 * Use Netty's blocking IO stategy.
	 */
	public final static String USE_BLOCKING_IO = "useBlockingIO";

	public final static String USE_DIRECT_BYTEBUFFER = "bufferFactory";

	public final static String EXECUTE_ASYNC_CONNECT = "asyncConnect";

	public final static String DISABLE_NESTED_REQUEST = "disableNestedRequest";

	public final static String BOSS_EXECUTOR_SERVICE = "bossExecutorService";

	public final static String REUSE_ADDRESS = "reuseAddress";

	private final ConcurrentHashMap<String, Object> properties = new ConcurrentHashMap<String, Object>();

	public NettyProviderConfig() {
		properties.put(REUSE_ADDRESS, "false");
	}

	public NettyProviderConfig addProperty(String name, Object value) {
		properties.put(name, value);
		return this;
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public Object removeProperty(String name) {
		return properties.remove(name);
	}

	public Set<Map.Entry<String, Object>> propertiesSet() {
		return properties.entrySet();
	}
}
