package net.ion.radon.aclient.providers.netty;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

import net.ion.radon.aclient.RandomAccessBody;

import org.jboss.netty.channel.FileRegion;

/**
 * Adapts a {@link RandomAccessBody} to Netty's {@link FileRegion}.
 */
class BodyFileRegion implements FileRegion {

	private final RandomAccessBody body;

	public BodyFileRegion(RandomAccessBody body) {
		if (body == null) {
			throw new IllegalArgumentException("no body specified");
		}
		this.body = body;
	}

	public long getPosition() {
		return 0;
	}

	public long getCount() {
		return body.getContentLength();
	}

	public long transferTo(WritableByteChannel target, long position) throws IOException {
		return body.transferTo(position, Long.MAX_VALUE, target);
	}

	public void releaseExternalResources() {
		try {
			body.close();
		} catch (IOException e) {
			// we tried
		}
	}

}
