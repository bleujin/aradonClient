package net.ion.radon.aclient.providers.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.AsyncHttpProvider;
import net.ion.radon.aclient.HttpResponseBodyPart;

/**
 * A callback class used when an HTTP response body is received.
 */
public class ResponseBodyPart extends HttpResponseBodyPart {

	private final byte[] chunk;
	private final boolean isLast;
	private boolean closeConnection;

	public ResponseBodyPart(URI uri, byte[] chunk, AsyncHttpProvider provider, boolean last) {
		super(uri, provider);
		this.chunk = chunk;
		isLast = last;
	}

	/**
	 * Return the response body's part bytes received.
	 * 
	 * @return the response body's part bytes received.
	 */
	public byte[] getBodyPartBytes() {
		return chunk;
	}

	@Override
	public InputStream readBodyPartBytes() {
		return new ByteArrayInputStream(chunk);
	}

	@Override
	public int length() {
		return chunk.length;
	}

	@Override
	public int writeTo(OutputStream outputStream) throws IOException {
		outputStream.write(chunk);
		return chunk.length;
	}

	@Override
	public ByteBuffer getBodyByteBuffer() {
		return ByteBuffer.wrap(chunk);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLast() {
		return isLast;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markUnderlyingConnectionAsClosed() {
		closeConnection = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean closeUnderlyingConnection() {
		return closeConnection;
	}
}