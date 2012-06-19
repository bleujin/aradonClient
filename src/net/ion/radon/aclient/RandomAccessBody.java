package net.ion.radon.aclient;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface RandomAccessBody extends Body {

	long transferTo(long position, long count, WritableByteChannel target) throws IOException;

}
