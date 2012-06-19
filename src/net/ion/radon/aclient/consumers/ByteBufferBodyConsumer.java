package net.ion.radon.aclient.consumers;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.BodyConsumer;

public class ByteBufferBodyConsumer implements BodyConsumer {

	private final ByteBuffer byteBuffer;

	public ByteBufferBodyConsumer(ByteBuffer byteBuffer) {
		this.byteBuffer = byteBuffer;
	}

	public void consume(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put(byteBuffer);
	}

	public void close() throws IOException {
		byteBuffer.flip();
	}
}
