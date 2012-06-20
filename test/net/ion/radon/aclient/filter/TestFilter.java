package net.ion.radon.aclient.filter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.TestBaseClient;
import net.ion.radon.aclient.ClientConfig.Builder;

import org.restlet.data.Method;

public class TestFilter extends TestBaseClient{

	// filter : intercept, transform, decorate, replay transactions
	
	// use only 2 connection at same time
	public void testThrottleRequestFilter() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.addRequestFilter(new ThrottleFilter(2)) ;
		
		NewClient c = NewClient.create(builder.build()) ;
		
		for (int i = 0; i < 10; i++) {
			c.prepareGet(getSlowUri() + "/" + (RandomUtil.nextInt(500) + 200)).execute() ;
		}
		c.close();
	}
	
	// if 204, redirect
	public void testResponseFilter() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.addResponseFilter(new ResponseFilter(){

			public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
				if (ctx.getResponseStatus().getStatusCode() == 204){
					FilterContext<T> result = new FilterContext.FilterContextBuilder<T>(ctx).request(new RequestBuilder(Method.GET).setUrl(getHelloUri()).build()).replayRequest(true).build();
					return result ;
				}
				return ctx ;
			}
		}) ;
		
		NewClient c = newHttpClient(builder.build()) ;
		Response res = c.prepareGet(getEchoUri()).execute().get() ;
		assertEquals(200, res.getStatusCode()) ;
		assertEquals("hello", res.getTextBody()) ;
	}
	
	
	// As an example, the following filter will resume an interrupted download instad of restarting downloading the file from the beginning
	public void xtestIOExceptionFilter() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		
		
		final File file = File.createTempFile("dd", "cc") ;
		builder.addIOExceptionFilter(new IOExceptionFilter() {
			public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {
				if (ctx.getIOException() != null ) {
					Request request = new RequestBuilder(ctx.getRequest()).setRangeOffset(file.length()).build() ;
					return new FilterContext.FilterContextBuilder(ctx).request(request).replayRequest(true).build();
				}
				return ctx;
			}
		}) ;
		
		NewClient c = newHttpClient(builder.build()) ;
		Response r = c.prepareGet("http://host:port/bigfile.avi").execute(new AsyncHandler<Response>(){
			private final Response.ResponseBuilder builder = new Response.ResponseBuilder() ;
			FileOutputStream output = new FileOutputStream(file) ;
			public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
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
			}).get() ;
		
	}
	
	
	
}

class ThrottleFilter implements RequestFilter {
	private final Semaphore available;
	private final int maxWait;

	public ThrottleFilter(int maxConnections) {
		this.maxWait = Integer.MAX_VALUE;
		available = new Semaphore(maxConnections, true);
	}

	public ThrottleFilter(int maxConnections, int maxWait) {
		this.maxWait = maxWait;
		available = new Semaphore(maxConnections, true);
	}

	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {

		try {
			Debug.debug("Current Throttling Status {}", available.availablePermits());
			if (!available.tryAcquire(maxWait, TimeUnit.MILLISECONDS)) {
				throw new FilterException(String.format("No slot available for processing Request %s with AsyncHandler %s", ctx.getRequest(), ctx.getAsyncHandler()));
			}
			;
		} catch (InterruptedException e) {
			throw new FilterException(String.format("Interrupted Request %s with AsyncHandler %s", ctx.getRequest(), ctx.getAsyncHandler()));
		}

		return new FilterContext.FilterContextBuilder<T>(ctx).asyncHandler(new AsyncHandlerWrapper<T>(ctx.getAsyncHandler())).build();
	}
	
	private class AsyncHandlerWrapper<T> implements AsyncHandler<T> {

		private final AsyncHandler<T> asyncHandler;

		public AsyncHandlerWrapper(AsyncHandler<T> asyncHandler) {
			this.asyncHandler = asyncHandler;
		}

		public void onThrowable(Throwable t) {
			try {
				asyncHandler.onThrowable(t);
			} finally {
				available.release();
				Debug.debug("Current Throttling Status after onThrowable {}", available.availablePermits());
			}
		}

		public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
			return asyncHandler.onBodyPartReceived(bodyPart);
		}

		public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
			return asyncHandler.onStatusReceived(responseStatus);
		}

		public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
			return asyncHandler.onHeadersReceived(headers);
		}

		public T onCompleted() throws Exception {
			available.release();
			Debug.info("Current Throttling Status {}", available.availablePermits());
			return asyncHandler.onCompleted();
		}
	}
}