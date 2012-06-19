package net.ion.radon.aclient.providers.simple;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.radon.aclient.AsyncHttpProviderConfig;

public class SimpleProviderConfig implements AsyncHttpProviderConfig<String, String> {

	public static final String FORCE_RESPONSE_BUFFERING = "bufferResponseInMemory";

	private final ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();

	@Override
	public SimpleProviderConfig addProperty(String name, String value) {
		properties.put(name, value);
		return this;
	}

	public String getProperty(String name) {
		return properties.get(name);
	}

	public String removeProperty(String name) {
		return properties.remove(name);
	}

	public Set<Map.Entry<String, String>> propertiesSet() {
		return properties.entrySet();
	}
}
