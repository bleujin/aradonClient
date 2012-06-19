package net.ion.radon.aclient.providers.netty;

import java.net.URI;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.HttpResponseStatus;

import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * A class that represent the HTTP response' status line (code + text)
 */
public class ResponseStatus extends HttpResponseStatus {

	private final HttpResponse response;

	public ResponseStatus(URI uri, HttpResponse response, AsyncHttpProvider provider) {
		super(uri, provider);
		this.response = response;
	}

	public int getStatusCode() {
		return response.getStatus().getCode();
	}

	public String getStatusText() {
		return response.getStatus().getReasonPhrase();
	}

	@Override
	public String getProtocolName() {
		return response.getProtocolVersion().getProtocolName();
	}

	@Override
	public int getProtocolMajorVersion() {
		return response.getProtocolVersion().getMajorVersion();
	}

	@Override
	public int getProtocolMinorVersion() {
		return response.getProtocolVersion().getMinorVersion();
	}

	@Override
	public String getProtocolText() {
		return response.getProtocolVersion().getText();
	}

}
