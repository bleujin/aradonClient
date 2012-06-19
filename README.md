Aradon Client
-----------------

// This project is an adaptation of the Apache AsyncHttpClient implementation
partionally modified for aradon server 


package net.ion.radon.aclient;
...

public class TestFirst extends TestCase{
	public void testSync() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon() ;
		aradon.startServer(9000) ;
		
		NewClient client = NewClient.create() ;
		Response response = client.prepareGet("http://127.0.0.1:9000/hello").execute().get() ;
		
		assertEquals(200, response.getStatusCode() ) ;
		aradon.stop() ;
	}
	
	public void testAsync() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon() ;
		aradon.startServer(9000) ;
		
		NewClient client = NewClient.create() ;
		ListenableFuture<String> future = client.prepareGet("http://127.0.0.1:9000/hello").execute(new AsyncHandler<String>(){
			
			private StringBuilder builder = new StringBuilder() ;
			public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
				builder.append(new String(bodyPart.getBodyPartBytes(), Charset.forName("UTF-8"))) ;
				return STATE.CONTINUE;
			}

			public String onCompleted() throws Exception {
				return builder.toString();
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
				return STATE.CONTINUE;
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
				return STATE.CONTINUE;
			}

			public void onThrowable(Throwable t) {
				t.printStackTrace() ;
			}}) ;

		Debug.line(future.get()) ;
		aradon.stop() ;
	}
	
}


