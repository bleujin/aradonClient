package net.ion.radon.aclient.extra;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.filter.FilterContext;
import net.ion.radon.aclient.filter.FilterException;
import net.ion.radon.aclient.filter.RequestFilter;

public class ThrottleRequestFilter implements RequestFilter {
	@SuppressWarnings("unused")
	private final int maxConnections;
	private final Semaphore available;
	private final int maxWait;

	public ThrottleRequestFilter(int maxConnections) {
		this.maxConnections = maxConnections;
		this.maxWait = Integer.MAX_VALUE;
		available = new Semaphore(maxConnections, true);
	}

	public ThrottleRequestFilter(int maxConnections, int maxWait) {
		this.maxConnections = maxConnections;
		this.maxWait = maxWait;
		available = new Semaphore(maxConnections, true);
	}

	public <T> FilterContext<T> filter(FilterContext<T> ctx) throws FilterException {

		try {
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
			return asyncHandler.onCompleted();
		}
	}
}
