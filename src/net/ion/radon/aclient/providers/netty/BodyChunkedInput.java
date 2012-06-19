package net.ion.radon.aclient.providers.netty;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.Body;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.stream.ChunkedInput;

/**
 * Adapts a {@link Body} to Netty's {@link ChunkedInput}.
 */
class BodyChunkedInput implements ChunkedInput {

	private final Body body;

	private final int chunkSize = 1024 * 8;

	private ByteBuffer nextChunk;

	private static final ByteBuffer EOF = ByteBuffer.allocate(0);

	public BodyChunkedInput(Body body) {
		if (body == null) {
			throw new IllegalArgumentException("no body specified");
		}
		this.body = body;
	}

	private ByteBuffer peekNextChuck() throws IOException {

		if (nextChunk == null) {
			ByteBuffer buffer = ByteBuffer.allocate(chunkSize);
			if (body.read(buffer) < 0) {
				nextChunk = EOF;
			} else {
				buffer.flip();
				nextChunk = buffer;
			}
		}
		return nextChunk;
	}

	public boolean hasNextChunk() throws Exception {
		return !isEndOfInput();
	}

	public Object nextChunk() throws Exception {
		ByteBuffer buffer = peekNextChuck();
		if (buffer == EOF) {
			return null;
		}
		nextChunk = null;
		return ChannelBuffers.wrappedBuffer(buffer);
	}

	public boolean isEndOfInput() throws Exception {
		return peekNextChuck() == EOF;
	}

	public void close() throws Exception {
		body.close();
	}

}
