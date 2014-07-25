package net.ion.radon.aclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpMethod;

public interface Request {

	public static interface EntityWriter {
		public void writeEntity(OutputStream out) throws IOException;
	}

	public HttpMethod getMethod();

	public String getUrl();

	public InetAddress getInetAddress();

	public InetAddress getLocalAddress();

	public String getRawUrl();

	public FluentCaseInsensitiveStringsMap getHeaders();

	public Collection<Cookie> getCookies();

	public byte[] getByteData();

	public String getStringData();

	public InputStream getStreamData();

	public EntityWriter getEntityWriter();

	public BodyGenerator getBodyGenerator();

	public long getLength();

	public long getContentLength();

	public FluentStringsMap getParams();

	public List<Part> getParts();

	public String getVirtualHost();

	public FluentStringsMap getQueryParams();

	public ProxyServer getProxyServer();

	public Realm getRealm();

	public File getFile();

	public boolean isRedirectEnabled();

	public boolean isRedirectOverrideSet();

	public PerRequestConfig getPerRequestConfig();

	public long getRangeOffset();

	public String getBodyEncoding();

	public boolean isUseRawUrl();

}
