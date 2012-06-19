package net.ion.radon.aclient.generators;

import java.io.IOException;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.Body;
import net.ion.radon.aclient.BodyGenerator;

public class ByteArrayBodyGenerator implements BodyGenerator {

	private final byte[] bytes;

	public ByteArrayBodyGenerator(byte[] bytes) {
		this.bytes = bytes;
	}

	protected final class ByteBody implements Body {
		private boolean eof = false;
		private int lastPosition = 0;

		public long getContentLength() {
			return bytes.length;
		}

		public long read(ByteBuffer byteBuffer) throws IOException {

			if (eof) {
				return -1;
			}

			final int remaining = bytes.length - lastPosition;
			if (remaining <= byteBuffer.capacity()) {
				byteBuffer.put(bytes, lastPosition, remaining);
				eof = true;
				return remaining;
			} else {
				byteBuffer.put(bytes, lastPosition, byteBuffer.capacity());
				lastPosition = lastPosition + byteBuffer.capacity();
				return byteBuffer.capacity();
			}
		}

		public void close() throws IOException {
			lastPosition = 0;
			eof = false;
		}
	}

	public Body createBody() throws IOException {
		return new ByteBody();
	}
}
