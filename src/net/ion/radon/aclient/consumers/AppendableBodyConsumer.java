package net.ion.radon.aclient.consumers;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.BodyConsumer;

public class AppendableBodyConsumer implements BodyConsumer {

	private final Appendable appendable;
	private final String encoding;

	public AppendableBodyConsumer(Appendable appendable, String encoding) {
		this.appendable = appendable;
		this.encoding = encoding;
	}

	public AppendableBodyConsumer(Appendable appendable) {
		this.appendable = appendable;
		this.encoding = "UTF-8";
	}

	public void consume(ByteBuffer byteBuffer) throws IOException {
		appendable.append(new String(byteBuffer.array(), encoding));
	}

	public void close() throws IOException {
		if (Closeable.class.isAssignableFrom(appendable.getClass())) {
			Closeable.class.cast(appendable).close();
		}
	}
}
