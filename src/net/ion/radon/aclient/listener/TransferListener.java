package net.ion.radon.aclient.listener;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;

public interface TransferListener {

	public void onRequestHeadersSent(FluentCaseInsensitiveStringsMap headers);

	public void onResponseHeadersReceived(FluentCaseInsensitiveStringsMap headers);

	public void onBytesReceived(ByteBuffer buffer) throws IOException;

	public void onBytesSent(ByteBuffer buffer);

	public void onRequestResponseCompleted();

	public void onThrowable(Throwable t);
}
