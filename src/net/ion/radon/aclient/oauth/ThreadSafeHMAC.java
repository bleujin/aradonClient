package net.ion.radon.aclient.oauth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.ion.radon.aclient.util.UTF8Codec;

public class ThreadSafeHMAC {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private final Mac mac;

	public ThreadSafeHMAC(ConsumerKey consumerAuth, RequestToken userAuth) {
		byte[] keyBytes = UTF8Codec.toUTF8(consumerAuth.getSecret() + "&" + userAuth.getSecret());
		SecretKeySpec signingKey = new SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM);

		// Get an hmac_sha1 instance and initialize with the signing key
		try {
			mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

	public synchronized byte[] digest(byte[] message) {
		mac.reset();
		return mac.doFinal(message);
	}
}
