package net.ion.radon.aclient;

import java.io.IOException;
import java.util.List;

public interface AsyncHttpProvider {

	public <T> ListenableFuture<T> execute(Request request, AsyncHandler<T> handler) throws IOException;
	public void close();
	public Response prepareResponse(HttpResponseStatus status, HttpResponseHeaders headers, List<HttpResponseBodyPart> bodyParts);
}
