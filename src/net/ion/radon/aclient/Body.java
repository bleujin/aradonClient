package net.ion.radon.aclient;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Body extends Closeable{

	long getContentLength();

	long read(ByteBuffer buffer) throws IOException;

}
