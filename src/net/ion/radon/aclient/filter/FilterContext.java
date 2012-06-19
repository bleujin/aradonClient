package net.ion.radon.aclient.filter;

import java.io.IOException;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.Request;

public class FilterContext<T> {

	private final FilterContextBuilder<T> b;

	private FilterContext(FilterContextBuilder<T> b) {
		this.b = b;
	}

	public AsyncHandler<T> getAsyncHandler() {
		return b.asyncHandler;
	}

	public Request getRequest() {
		return b.request;
	}

	public HttpResponseStatus getResponseStatus() {
		return b.responseStatus;
	}

	public HttpResponseHeaders getResponseHeaders() {
		return b.headers;
	}

	public boolean replayRequest() {
		return b.replayRequest;
	}

	public IOException getIOException() {
		return b.ioException;
	}

	public static class FilterContextBuilder<T> {
		private AsyncHandler<T> asyncHandler = null;
		private Request request = null;
		private HttpResponseStatus responseStatus = null;
		private boolean replayRequest = false;
		private IOException ioException = null;
		private HttpResponseHeaders headers;

		public FilterContextBuilder() {
		}

		public FilterContextBuilder(FilterContext<T> clone) {
			asyncHandler = clone.getAsyncHandler();
			request = clone.getRequest();
			responseStatus = clone.getResponseStatus();
			replayRequest = clone.replayRequest();
			ioException = clone.getIOException();
		}

		public AsyncHandler<T> getAsyncHandler() {
			return asyncHandler;
		}

		public FilterContextBuilder<T> asyncHandler(AsyncHandler<T> asyncHandler) {
			this.asyncHandler = asyncHandler;
			return this;
		}

		public Request getRequest() {
			return request;
		}

		public FilterContextBuilder<T> request(Request request) {
			this.request = request;
			return this;
		}

		public FilterContextBuilder<T> responseStatus(HttpResponseStatus responseStatus) {
			this.responseStatus = responseStatus;
			return this;
		}

		public FilterContextBuilder<T> responseHeaders(HttpResponseHeaders headers) {
			this.headers = headers;
			return this;
		}

		public FilterContextBuilder<T> replayRequest(boolean replayRequest) {
			this.replayRequest = replayRequest;
			return this;
		}

		public FilterContextBuilder<T> ioException(IOException ioException) {
			this.ioException = ioException;
			return this;
		}

		public FilterContext<T> build() {
			return new FilterContext<T>(this);
		}
	}

}
