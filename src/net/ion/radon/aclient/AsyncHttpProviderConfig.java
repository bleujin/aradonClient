package net.ion.radon.aclient;

import java.util.Map;
import java.util.Set;

public interface AsyncHttpProviderConfig<U, V> {

	public AsyncHttpProviderConfig<U, V> addProperty(U name, V value);

	public V getProperty(U name);

	public V removeProperty(U name);

	public Set<Map.Entry<U, V>> propertiesSet();
}
