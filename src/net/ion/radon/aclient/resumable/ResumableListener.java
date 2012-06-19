package net.ion.radon.aclient.resumable;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ResumableListener {

	public void onBytesReceived(ByteBuffer byteBuffer) throws IOException;

	public void onAllBytesReceived();

	public long length();

}