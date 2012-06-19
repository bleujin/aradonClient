package net.ion.radon.aclient.resumable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

import org.apache.log4j.spi.LoggerFactory;

public class PropertiesBasedResumableProcessor implements ResumableAsyncHandler.ResumableProcessor {
	private final static File TMP = new File(System.getProperty("java.io.tmpdir"), "ahc");
	private final static String storeName = "ResumableAsyncHandler.properties";
	private final ConcurrentHashMap<String, Long> properties = new ConcurrentHashMap<String, Long>();

	public void put(String url, long transferredBytes) {
		properties.put(url, transferredBytes);
	}

	public void remove(String uri) {
		if (uri != null) {
			properties.remove(uri);
		}
	}

	public void save(Map<String, Long> map) {
		FileOutputStream os = null;
		try {

			if (!TMP.mkdirs()) {
				throw new IllegalStateException("Unable to create directory: " + TMP.getAbsolutePath());
			}
			File f = new File(TMP, storeName);
			if (!f.createNewFile()) {
				throw new IllegalStateException("Unable to create temp file: " + f.getAbsolutePath());
			}
			if (!f.canWrite()) {
				throw new IllegalStateException();
			}

			os = new FileOutputStream(f);

			for (Map.Entry<String, Long> e : properties.entrySet()) {
				os.write((append(e)).getBytes("UTF-8"));
			}
			os.flush();
		} catch (Throwable e) {
			Debug.warn(e.getMessage(), e);
		} finally {
			IOUtil.closeQuietly(os) ;
		}
	}

	private static String append(Map.Entry<String, Long> e) {
		return new StringBuffer(e.getKey()).append("=").append(e.getValue()).append("\n").toString();
	}

	public Map<String, Long> load() {
		try {
			Scanner scan = new Scanner(new File(TMP, storeName), "UTF-8");
			scan.useDelimiter("[=\n]");

			String key;
			String value;
			while (scan.hasNext()) {
				key = scan.next().trim();
				value = scan.next().trim();
				properties.put(key, Long.valueOf(value));
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (Throwable ex) {
			// Survive any exceptions
			ex.printStackTrace();
		}
		return properties;
	}
}
