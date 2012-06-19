package net.ion.radon.aclient;

import java.io.IOException;

public interface ResumableBodyConsumer extends BodyConsumer {

	void resume() throws IOException;

	long getTransferredBytes() throws IOException;

}
