package net.ion.radon.aclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

public abstract class HttpResponseBodyPart extends HttpContent {

	public HttpResponseBodyPart(URI uri, AsyncHttpProvider provider) {
		super(uri, provider);
	}

	abstract public int length();

	abstract public byte[] getBodyPartBytes();

	abstract public InputStream readBodyPartBytes();

	abstract public int writeTo(OutputStream outputStream) throws IOException;

	abstract public ByteBuffer getBodyByteBuffer();

	abstract public boolean isLast();

	abstract public void markUnderlyingConnectionAsClosed();

	abstract public boolean closeUnderlyingConnection();

}
