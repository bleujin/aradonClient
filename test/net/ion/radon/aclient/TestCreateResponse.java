package net.ion.radon.aclient;

import java.io.File;
import java.io.FileOutputStream;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;

public class TestCreateResponse extends TestBaseClient{

	public void testCreateResponse() throws Exception {
		
		AsyncHandler<Response> ahandler = new AsyncHandler<Response>(){

			private final Response.ResponseBuilder builder = new Response.ResponseBuilder() ;
			public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
				builder.accumulate(bodyPart) ;
				return STATE.CONTINUE;
			}

			public Response onCompleted() throws Exception {
				return builder.build() ;
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
				builder.accumulate(headers);
				return STATE.CONTINUE ;
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onStatusReceived(HttpResponseStatus status) throws Exception {
				builder.accumulate(status);
				return STATE.CONTINUE ;
			}

			public void onThrowable(Throwable ex) {
				ex.printStackTrace() ;
			}
			
		};
		
		Response response = newClient().prepareGet(getHelloUri()).execute(ahandler).get() ;
		assertEquals("hello", response.getTextBody()) ;
	}
	
	public void testCompletedHandler() throws Exception {
		JsonObject json = newClient().prepareGet(getHelloUri()).execute(new AsyncCompletionHandler<JsonObject>() {
			@Override
			public JsonObject onCompleted(Response response) throws Exception {
				return JsonObject.fromString("{greeting:'" + response.getTextBody() + "'}");
			}
		}).get() ;
		
		assertEquals("hello", json.asString("greeting")) ;
	}
	
	
	public void testZeroByteCopy() throws Exception {

		final File file = File.createTempFile("tmp", "aclient") ;
		
		AsyncHandler<Response> ahandler = new AsyncHandler<Response>(){
			private final Response.ResponseBuilder builder = new Response.ResponseBuilder() ;
			FileOutputStream output = new FileOutputStream(file) ;

			public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
				// to avoid loading bytes in memory and unnecessary copy
				bodyPart.writeTo(output) ;
				return STATE.CONTINUE ;
			}

			public Response onCompleted() throws Exception {
				output.close() ;
				return builder.build() ;
			}

			public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
				builder.accumulate(headers);
				return STATE.CONTINUE ;
			}

			public STATE onStatusReceived(HttpResponseStatus status) throws Exception {
				builder.accumulate(status);
				return STATE.CONTINUE ;
			}

			public void onThrowable(Throwable ex) {
				ex.printStackTrace() ;
			}
			
		};

		Response response = newClient().prepareGet(getHelloUri()).execute(ahandler).get() ;
		String body = IOUtil.toString(file.toURI()) ;
		assertEquals("hello", body) ;
	}
	
}
