package net.ion.radon.aclient.consumers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.ResumableBodyConsumer;

public class FileBodyConsumer implements ResumableBodyConsumer {

	private final RandomAccessFile file;

	public FileBodyConsumer(RandomAccessFile file) {
		this.file = file;
	}

	public void consume(ByteBuffer byteBuffer) throws IOException {
		// TODO: Channel.transferFrom may be a good idea to investigate.
		file.write(byteBuffer.array());
	}

	public void close() throws IOException {
		file.close();
	}

	public long getTransferredBytes() throws IOException {
		return file.length();
	}

	public void resume() throws IOException {
		file.seek(getTransferredBytes());
	}
}
