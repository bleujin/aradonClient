package net.ion.radon.aclient.simple;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;

public class HeaderMap implements Map<String, List<String>> {

	private FluentCaseInsensitiveStringsMap headers;

	public HeaderMap(FluentCaseInsensitiveStringsMap headers) {
		this.headers = headers;
	}

	public Set<String> keySet() {
		return headers.keySet();
	}

	public Set<java.util.Map.Entry<String, List<String>>> entrySet() {
		return headers.entrySet();
	}

	public int size() {
		return headers.size();
	}

	public boolean isEmpty() {
		return headers.isEmpty();
	}

	public boolean containsKey(Object key) {
		return headers.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return headers.containsValue(value);
	}

	public String getFirstValue(String key) {
		return headers.getFirstValue(key);
	}

	public String getJoinedValue(String key, String delimiter) {
		return headers.getJoinedValue(key, delimiter);
	}

	public List<String> get(Object key) {
		return headers.get(key);
	}

	public List<String> put(String key, List<String> value) {
		throw new UnsupportedOperationException("Only read access is supported.");
	}

	public List<String> remove(Object key) {
		throw new UnsupportedOperationException("Only read access is supported.");
	}

	public void putAll(Map<? extends String, ? extends List<String>> t) {
		throw new UnsupportedOperationException("Only read access is supported.");

	}

	public void clear() {
		throw new UnsupportedOperationException("Only read access is supported.");
	}

	public Collection<List<String>> values() {
		return headers.values();
	}

}
