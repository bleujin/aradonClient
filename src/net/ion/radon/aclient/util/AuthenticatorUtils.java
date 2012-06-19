package net.ion.radon.aclient.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import net.ion.radon.aclient.ProxyServer;
import net.ion.radon.aclient.Realm;

public final class AuthenticatorUtils {

	public static String computeBasicAuthentication(Realm realm) throws UnsupportedEncodingException {
		String s = realm.getPrincipal() + ":" + realm.getPassword();
		return "Basic " + Base64.encode(s.getBytes(realm.getEncoding()));
	}

	public static String computeBasicAuthentication(ProxyServer proxyServer) throws UnsupportedEncodingException {
		String s = proxyServer.getPrincipal() + ":" + proxyServer.getPassword();
		return "Basic " + Base64.encode(s.getBytes(proxyServer.getEncoding()));
	}

	public static String computeDigestAuthentication(Realm realm) throws NoSuchAlgorithmException, UnsupportedEncodingException {

		StringBuilder builder = new StringBuilder().append("Digest ");
		construct(builder, "username", realm.getPrincipal());
		construct(builder, "realm", realm.getRealmName());
		construct(builder, "nonce", realm.getNonce());
		construct(builder, "uri", realm.getUri());
		builder.append("algorithm").append('=').append(realm.getAlgorithm()).append(", ");

		construct(builder, "response", realm.getResponse());
		if (realm.getOpaque() != null && realm.getOpaque() != null && realm.getOpaque().equals("") == false)
			construct(builder, "opaque", realm.getOpaque());
		builder.append("qop").append('=').append(realm.getQop()).append(", ");
		builder.append("nc").append('=').append(realm.getNc()).append(", ");
		construct(builder, "cnonce", realm.getCnonce(), true);

		return new String(builder.toString().getBytes("ISO_8859_1"));
	}

	private static StringBuilder construct(StringBuilder builder, String name, String value) {
		return construct(builder, name, value, false);
	}

	private static StringBuilder construct(StringBuilder builder, String name, String value, boolean tail) {
		return builder.append(name).append('=').append('"').append(value).append(tail ? "\"" : "\", ");
	}
}
