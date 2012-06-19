package net.ion.radon.aclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of a {@code String -> List<String>} map that adds a fluent interface, i.e. methods that return this instance. This class differs from {@link FluentStringsMap} in that keys are treated in an case-insensitive matter, i.e. case of the key doesn't matter when retrieving values or changing the map. However, the map preserves the key case (of the first insert or replace) and returns
 * the keys in their original case in the appropriate methods (e.g. {@link FluentCaseInsensitiveStringsMap#keySet()}).
 */
public class FluentCaseInsensitiveStringsMap implements Map<String, List<String>>, Iterable<Map.Entry<String, List<String>>> {
	private final Map<String, List<String>> values = new LinkedHashMap<String, List<String>>();
	private final Map<String, String> keyLookup = new LinkedHashMap<String, String>();

	public FluentCaseInsensitiveStringsMap() {
	}

	public FluentCaseInsensitiveStringsMap(FluentCaseInsensitiveStringsMap src) {
		if (src != null) {
			for (Map.Entry<String, List<String>> header : src) {
				add(header.getKey(), header.getValue());
			}
		}
	}

	public FluentCaseInsensitiveStringsMap(Map<String, Collection<String>> src) {
		if (src != null) {
			for (Map.Entry<String, Collection<String>> header : src.entrySet()) {
				add(header.getKey(), header.getValue());
			}
		}
	}

	public FluentCaseInsensitiveStringsMap add(String key, String... values) {
		if ((values != null) && (values.length > 0)) {
			add(key, Arrays.asList(values));
		}
		return this;
	}

	private List<String> fetchValues(Collection<String> values) {
		List<String> result = null;

		if (values != null) {
			for (String value : values) {
				if (value == null) {
					value = "";
				}
				if (result == null) {
					// lazy initialization
					result = new ArrayList<String>();
				}
				result.add(value);
			}
		}
		return result;
	}

	public FluentCaseInsensitiveStringsMap add(String key, Collection<String> values) {
		if (key != null) {
			List<String> nonNullValues = fetchValues(values);

			if (nonNullValues != null) {
				String lcKey = key.toLowerCase();
				String realKey = keyLookup.get(lcKey);
				List<String> curValues = null;

				if (realKey == null) {
					realKey = key;
					keyLookup.put(lcKey, key);
				} else {
					curValues = this.values.get(realKey);
				}

				if (curValues == null) {
					curValues = new ArrayList<String>();
					this.values.put(realKey, curValues);
				}
				curValues.addAll(nonNullValues);
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap addAll(FluentCaseInsensitiveStringsMap src) {
		if (src != null) {
			for (Map.Entry<String, List<String>> header : src) {
				add(header.getKey(), header.getValue());
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap addAll(Map<String, Collection<String>> src) {
		if (src != null) {
			for (Map.Entry<String, Collection<String>> header : src.entrySet()) {
				add(header.getKey(), header.getValue());
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap replace(final String key, final String... values) {
		return replace(key, Arrays.asList(values));
	}

	public FluentCaseInsensitiveStringsMap replace(final String key, final Collection<String> values) {
		if (key != null) {
			List<String> nonNullValues = fetchValues(values);
			String lcKkey = key.toLowerCase();
			String realKey = keyLookup.get(lcKkey);

			if (nonNullValues == null) {
				keyLookup.remove(lcKkey);
				if (realKey != null) {
					this.values.remove(realKey);
				}
			} else {
				if (!key.equals(realKey)) {
					keyLookup.put(lcKkey, key);
					this.values.remove(realKey);
				}
				this.values.put(key, nonNullValues);
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap replaceAll(FluentCaseInsensitiveStringsMap src) {
		if (src != null) {
			for (Map.Entry<String, List<String>> header : src) {
				replace(header.getKey(), header.getValue());
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap replaceAll(Map<? extends String, ? extends Collection<String>> src) {
		if (src != null) {
			for (Map.Entry<? extends String, ? extends Collection<String>> header : src.entrySet()) {
				replace(header.getKey(), header.getValue());
			}
		}
		return this;
	}

	public List<String> put(String key, List<String> value) {
		if (key == null) {
			throw new NullPointerException("Null keys are not allowed");
		}

		List<String> oldValue = get(key);

		replace(key, value);
		return oldValue;
	}

	public void putAll(Map<? extends String, ? extends List<String>> values) {
		replaceAll(values);
	}

	public FluentCaseInsensitiveStringsMap delete(String key) {
		if (key != null) {
			String lcKey = key.toLowerCase();
			String realKey = keyLookup.remove(lcKey);

			if (realKey != null) {
				values.remove(realKey);
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap deleteAll(String... keys) {
		if (keys != null) {
			for (String key : keys) {
				remove(key);
			}
		}
		return this;
	}

	public FluentCaseInsensitiveStringsMap deleteAll(Collection<String> keys) {
		if (keys != null) {
			for (String key : keys) {
				remove(key);
			}
		}
		return this;
	}

	public List<String> remove(Object key) {
		if (key == null) {
			return null;
		} else {
			List<String> oldValues = get(key.toString());

			delete(key.toString());
			return oldValues;
		}
	}

	public void clear() {
		keyLookup.clear();
		values.clear();
	}

	public Iterator<Map.Entry<String, List<String>>> iterator() {
		return Collections.unmodifiableSet(values.entrySet()).iterator();
	}

	public Set<String> keySet() {
		return new LinkedHashSet<String>(keyLookup.values());
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Set<Entry<String, List<String>>> entrySet() {
		return values.entrySet();
	}

	public int size() {
		return values.size();
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public boolean containsKey(Object key) {
		return key == null ? false : keyLookup.containsKey(key.toString().toLowerCase());
	}

	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	public String getFirstValue(String key) {
		List<String> values = get(key);

		if (values == null) {
			return null;
		} else if (values.isEmpty()) {
			return "";
		} else {
			return values.get(0);
		}
	}

	public String getJoinedValue(String key, String delimiter) {
		List<String> values = get(key);

		if (values == null) {
			return null;
		} else if (values.size() == 1) {
			return values.get(0);
		} else {
			StringBuilder result = new StringBuilder();

			for (String value : values) {
				if (result.length() > 0) {
					result.append(delimiter);
				}
				result.append(value);
			}
			return result.toString();
		}
	}

	public List<String> get(Object key) {
		if (key == null) {
			return null;
		}

		String lcKey = key.toString().toLowerCase();
		String realKey = keyLookup.get(lcKey);

		if (realKey == null) {
			return null;
		} else {
			return values.get(realKey);
		}
	}

	public Collection<List<String>> values() {
		return values.values();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final FluentCaseInsensitiveStringsMap other = (FluentCaseInsensitiveStringsMap) obj;

		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return values == null ? 0 : values.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		for (Map.Entry<String, List<String>> entry : values.entrySet()) {
			if (result.length() > 0) {
				result.append("; ");
			}
			result.append("\"");
			result.append(entry.getKey());
			result.append("=");

			boolean needsComma = false;

			for (String value : entry.getValue()) {
				if (needsComma) {
					result.append(", ");
				} else {
					needsComma = true;
				}
				result.append(value);
			}
			result.append("\"");
		}
		return result.toString();
	}
}
