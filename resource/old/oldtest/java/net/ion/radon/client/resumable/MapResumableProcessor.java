package net.ion.radon.client.resumable;


import java.util.HashMap;
import java.util.Map;

import net.ion.radon.client.resumable.ResumableAsyncHandler.ResumableProcessor;

/**
 * @author Benjamin Hanzelmann
 */
public class MapResumableProcessor implements ResumableProcessor {

	Map<String, Long> map = new HashMap<String, Long>();

	public void put(String key, long transferredBytes) {
		map.put(key, transferredBytes);
	}

	public void remove(String key) {
		map.remove(key);
	}

	/**
	 * NOOP
	 */
	public void save(Map<String, Long> map) {

	}

	/**
	 * NOOP
	 */
	public Map<String, Long> load() {
		return map;
	}
}