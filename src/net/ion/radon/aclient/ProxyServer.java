package net.ion.radon.aclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProxyServer {

	public enum Protocol {
		HTTP("http"), HTTPS("https"), NTLM("NTLM"), KERBEROS("KERBEROS"), SPNEGO("SPNEGO");

		private final String protocol;

		private Protocol(final String protocol) {
			this.protocol = protocol;
		}

		public String getProtocol() {
			return protocol;
		}

		@Override
		public String toString() {
			return getProtocol();
		}
	}

	private String encoding = "UTF-8";
	private final List<String> nonProxyHosts = new ArrayList<String>();
	private final Protocol protocol;
	private final String host;
	private final String principal;
	private final String password;
	private int port;
	private String ntlmDomain = System.getProperty("http.auth.ntlm.domain", "");

	public ProxyServer(final Protocol protocol, final String host, final int port, String principal, String password) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.principal = principal;
		this.password = password;
	}

	public ProxyServer(final String host, final int port, String principal, String password) {
		this(Protocol.HTTP, host, port, principal, password);
	}

	public ProxyServer(final Protocol protocol, final String host, final int port) {
		this(protocol, host, port, null, null);
	}

	public ProxyServer(final String host, final int port) {
		this(Protocol.HTTP, host, port, null, null);
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public String getProtocolAsString() {
		return protocol.toString();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getPrincipal() {
		return principal;
	}

	public String getPassword() {
		return password;
	}

	public ProxyServer setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public String getEncoding() {
		return encoding;
	}

	public ProxyServer addNonProxyHost(String uri) {
		nonProxyHosts.add(uri);
		return this;
	}

	public ProxyServer removeNonProxyHost(String uri) {
		nonProxyHosts.remove(uri);
		return this;
	}

	public List<String> getNonProxyHosts() {
		return Collections.unmodifiableList(nonProxyHosts);
	}

	public ProxyServer setNtlmDomain(String ntlmDomain) {
		this.ntlmDomain = ntlmDomain;
		return this;
	}

	public String getNtlmDomain() {
		return ntlmDomain;
	}

	@Override
	public String toString() {
		return String.format("%s://%s:%d", protocol.toString(), host, port);
	}
}
