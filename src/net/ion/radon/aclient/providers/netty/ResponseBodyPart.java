package net.ion.radon.aclient.providers.netty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.HttpResponseBodyPart;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * A callback class used when an HTTP response body is received.
 */
public class ResponseBodyPart extends HttpResponseBodyPart {
	// Empty arrays are immutable, can freely reuse
	private final static byte[] NO_BYTES = new byte[0];

	private final HttpChunk chunk;
	private final HttpResponse response;
	private final AtomicReference<byte[]> bytes = new AtomicReference<byte[]>(null);
	private final boolean isLast;
	private boolean closeConnection = false;


	public ResponseBodyPart(URI uri, HttpResponse response, AsyncHttpProvider provider, boolean last) {
		this(uri, response, provider, null, last);
	}

	public ResponseBodyPart(URI uri, HttpResponse response, AsyncHttpProvider provider, HttpChunk chunk, boolean last) {
		super(uri, provider);
		this.chunk = chunk;
		this.response = response;
		isLast = last;
	}


	public byte[] getBodyPartBytes() {
		byte[] bp = bytes.get();
		if (bp != null) {
			return bp;
		}

		ChannelBuffer b = (chunk != null) ? chunk.getContent() : response.getContent();
		int available = b.readableBytes();

		final byte[] rb = (available == 0) ? NO_BYTES : new byte[available];
		b.getBytes(b.readerIndex(), rb, 0, available);
		return rb;
	}

	@Override
	public InputStream readBodyPartBytes() {
		return new ByteArrayInputStream(getBodyPartBytes());
	}

	@Override
	public int length() {
		ChannelBuffer b = (chunk != null) ? chunk.getContent() : response.getContent();
		return b.readableBytes();
	}

	@Override
	public int writeTo(OutputStream outputStream) throws IOException {
		ChannelBuffer b = (chunk != null) ? chunk.getContent() : response.getContent();
		int available = b.readableBytes();
		if (available > 0) {
			b.getBytes(b.readerIndex(), outputStream, available);
		}
		return available;
	}

	@Override
	public ByteBuffer getBodyByteBuffer() {
		return ByteBuffer.wrap(getBodyPartBytes());
	}


	public boolean isLast() {
		return isLast;
	}


	public void markUnderlyingConnectionAsClosed() {
		closeConnection = true;
	}


	public boolean closeUnderlyingConnection() {
		return closeConnection;
	}

	protected HttpChunk chunk() {
		return chunk;
	}
}
