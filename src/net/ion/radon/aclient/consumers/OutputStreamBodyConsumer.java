package net.ion.radon.aclient.consumers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.BodyConsumer;

public class OutputStreamBodyConsumer implements BodyConsumer {

	private final OutputStream outputStream;

	public OutputStreamBodyConsumer(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void consume(ByteBuffer byteBuffer) throws IOException {
		outputStream.write(byteBuffer.array());
	}

	public void close() throws IOException {
		outputStream.close();
	}
}
