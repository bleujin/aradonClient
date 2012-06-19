package net.ion.radon.aclient.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SslUtils {

	public static SSLEngine getSSLEngine() throws GeneralSecurityException, IOException {
		SSLEngine engine = null;

		SSLContext context = getSSLContext();
		if (context != null) {
			engine = context.createSSLEngine();
			engine.setUseClientMode(true);
		}

		return engine;
	}

	public static SSLContext getSSLContext() throws GeneralSecurityException, IOException {
		SSLConfig config = new SSLConfig();
		if (config.keyStoreLocation == null || config.trustStoreLocation == null) {
			return getLooseSSLContext();
		} else {
			return getStrictSSLContext(config);
		}
	}

	static SSLContext getStrictSSLContext(SSLConfig config) throws GeneralSecurityException, IOException {
		KeyStore keyStore = KeyStore.getInstance(config.keyStoreType);
		InputStream keystoreInputStream = new FileInputStream(config.keyStoreLocation);
		try {
			keyStore.load(keystoreInputStream, (config.keyStorePassword == null) ? null : config.keyStorePassword.toCharArray());
		} finally {
			keystoreInputStream.close();
		}

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(config.keyManagerAlgorithm);
		keyManagerFactory.init(keyStore, (config.keyManagerPassword == null) ? null : config.keyManagerPassword.toCharArray());
		KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

		KeyStore trustStore = KeyStore.getInstance(config.trustStoreType);
		InputStream truststoreInputStream = new FileInputStream(config.trustStoreLocation);
		try {
			trustStore.load(truststoreInputStream, (config.trustStorePassword == null) ? null : config.trustStorePassword.toCharArray());
		} finally {
			truststoreInputStream.close();
		}

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(config.trustManagerAlgorithm);
		trustManagerFactory.init(trustStore);
		TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagers, trustManagers, null);

		return context;
	}

	static SSLContext getLooseSSLContext() throws GeneralSecurityException {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { LooseTrustManager.INSTANCE }, new SecureRandom());
		return sslContext;
	}

	static class LooseTrustManager implements X509TrustManager {

		public static final LooseTrustManager INSTANCE = new LooseTrustManager();

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}
	}

	private final static class SSLConfig {

		public String keyStoreLocation;

		public String keyStoreType = "JKS";

		public String keyStorePassword = "changeit";

		public String keyManagerAlgorithm = "SunX509";

		public String keyManagerPassword = "changeit";

		public String trustStoreLocation;

		public String trustStoreType = "JKS";

		public String trustStorePassword = "changeit";

		public String trustManagerAlgorithm = "SunX509";

		public SSLConfig() {
			keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
			keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword", "changeit");
			keyStoreType = System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType());
			keyManagerAlgorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");

			if (keyManagerAlgorithm == null) {
				keyManagerAlgorithm = "SunX509";
			}

			keyManagerPassword = System.getProperty("javax.net.ssl.keyStorePassword", "changeit");

			trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
			if (trustStoreLocation == null) {
				trustStoreLocation = keyStoreLocation;
				trustStorePassword = keyStorePassword;
				trustStoreType = keyStoreType;
			} else {
				trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword", "changeit");
				trustStoreType = System.getProperty("javax.net.ssl.trustStoreType", KeyStore.getDefaultType());
			}
			trustManagerAlgorithm = Security.getProperty("ssl.TrustManagerFactory.algorithm");

			if (trustManagerAlgorithm == null) {
				trustManagerAlgorithm = "SunX509";
			}
		}
	}

}
