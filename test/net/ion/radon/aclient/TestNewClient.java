package net.ion.radon.aclient;

import net.ion.framework.util.Debug;
import net.ion.radon.aclient.AsyncHandler.STATE;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;

import org.apache.ecs.vxml.Throw;
import org.restlet.representation.Representation;

import junit.framework.TestCase;

public class TestNewClient extends TestCase {

	public void testBadRequest() throws Exception {
		NewClient nc = NewClient.create();

		try {
			ListenableFuture<Response> res = nc.prepareDelete("http://127.0.0.1:/aradon/shutdown?timeout=100").execute(new AsyncHandler<Response>() {
				private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
				public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
					builder.accumulate(bodyPart);
					return STATE.CONTINUE;
				}

				public Response onCompleted() throws Exception {
					return builder.build();
				}

				public net.ion.radon.aclient.AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
					return STATE.CONTINUE;
				}

				public net.ion.radon.aclient.AsyncHandler.STATE onStatusReceived(HttpResponseStatus status) throws Exception {
					return STATE.CONTINUE;
				}

				public void onThrowable(Throwable ex) {
					Debug.line("i try to stop, but server is not started : ");
				}
			});
			Debug.line(res.get());
		} catch (Throwable ex) {
		}
	}
}
