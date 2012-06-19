package net.ion.radon.aclient.extra;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import net.ion.radon.aclient.resumable.ResumableListener;

public class ResumableRandomAccessFileListener implements ResumableListener {
	private final RandomAccessFile file;

	public ResumableRandomAccessFileListener(RandomAccessFile file) {
		this.file = file;
	}

	public void onBytesReceived(ByteBuffer buffer) throws IOException {
		file.seek(file.length());
		file.write(buffer.array());
	}

	public void onAllBytesReceived() {
		if (file != null) {
			try {
				file.close();
			} catch (IOException e) {
				;
			}
		}
	}

	public long length() {
		try {
			return file.length();
		} catch (IOException e) {
			;
		}
		return 0;
	}

}
