package net.ion.radon.aclient;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

public class TestNewClient extends TestCase {

	private NewClient nc;


	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		this.nc = NewClient.create();
	}
	
	@Override
	protected void tearDown() throws Exception {
		nc.close(); 
		super.tearDown();
	}
	
	public void testBadRequest() throws Exception {

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
	
	
	public void testResponse() throws Exception {
		
		ExecutorService es = Executors.newCachedThreadPool() ;
		
		final UIHandler<Void> uh = new UIHandler<Void>() {
			@Override
			public Void handler(Response response) throws IOException {
				Debug.line(response.getTextBody());
				return null;
			}
			
			public Void onThrow(Throwable ex){
				Debug.line(ex.getMessage(), ex.getClass());
				return null;
			}
		};
		
		Future<Void> future = es.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ListenableFuture<Response> future = nc.prepareGet("http://wwww.daumdd.net").execute(new AsyncCompletionHandler<Response>() {

					@Override
					public Response onCompleted(Response response) throws Exception {
						return response ;
					}
					
					@Override
					public void onThrowable(Throwable e) {
						uh.onThrow(e) ;
					}
				});
				
				if (! future.isCancelled())
					uh.handler(future.get()) ;
				return null;
			}
		}) ;
		
		
		future.get() ;
	}
	
	
	
}


interface UIHandler<T> {
	public T handler(Response response) throws IOException ;
	public T onThrow(Throwable ex) ;
}