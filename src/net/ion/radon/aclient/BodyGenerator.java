package net.ion.radon.aclient;

import java.io.IOException;

public interface BodyGenerator {

	Body createBody() throws IOException;

}
