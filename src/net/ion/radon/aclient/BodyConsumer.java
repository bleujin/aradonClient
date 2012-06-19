package net.ion.radon.aclient;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface BodyConsumer extends Closeable{

	void consume(ByteBuffer byteBuffer) throws IOException;

}
