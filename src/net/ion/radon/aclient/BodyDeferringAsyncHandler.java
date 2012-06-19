package net.ion.radon.aclient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import net.ion.radon.aclient.Response.ResponseBuilder;

public class BodyDeferringAsyncHandler implements AsyncHandler<Response> {
	private final ResponseBuilder responseBuilder = new ResponseBuilder();

	private final CountDownLatch headersArrived = new CountDownLatch(1);

	private final OutputStream output;

	private volatile boolean responseSet;

	private volatile Response response;

	private volatile Throwable throwable;

	private final Semaphore semaphore = new Semaphore(1);

	public BodyDeferringAsyncHandler(final OutputStream os) {
		this.output = os;
		this.responseSet = false;
	}

	public void onThrowable(Throwable t) {
		this.throwable = t;
		// Counting down to handle error cases too.
		// In "premature exceptions" cases, the onBodyPartReceived() and
		// onCompleted()
		// methods will never be invoked, leaving caller of getResponse() method
		// blocked forever.
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// Ignore
		} finally {
			headersArrived.countDown();
			semaphore.release();
		}

		try {
			closeOut();
		} catch (IOException e) {
			// ignore
		}
	}

	public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
		responseBuilder.reset();
		responseBuilder.accumulate(responseStatus);
		return STATE.CONTINUE;
	}

	public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
		responseBuilder.accumulate(headers);
		return STATE.CONTINUE;
	}

	public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
		// body arrived, flush headers
		if (!responseSet) {
			response = responseBuilder.build();
			responseSet = true;
			headersArrived.countDown();
		}

		bodyPart.writeTo(output);
		return STATE.CONTINUE;
	}

	protected void closeOut() throws IOException {
		try {
			output.flush();
		} finally {
			output.close();
		}
	}

	public Response onCompleted() throws IOException {
		// Counting down to handle error cases too.
		// In "normal" cases, latch is already at 0 here
		// But in other cases, for example when because of some error
		// onBodyPartReceived() is never called, the caller
		// of getResponse() would remain blocked infinitely.
		// By contract, onCompleted() is always invoked, even in case of errors
		headersArrived.countDown();

		closeOut();

		try {
			semaphore.acquire();
			if (throwable != null) {
				IOException ioe = new IOException(throwable.getMessage());
				ioe.initCause(throwable);
				throw ioe;
			} else {
				// sending out current response
				return responseBuilder.build();
			}
		} catch (InterruptedException e) {
			return null;
		} finally {
			semaphore.release();
		}
	}

	public Response getResponse() throws InterruptedException, IOException {
		// block here as long as headers arrive
		headersArrived.await();

		try {
			semaphore.acquire();
			if (throwable != null) {
				IOException ioe = new IOException(throwable.getMessage());
				ioe.initCause(throwable);
				throw ioe;
			} else {
				return response;
			}
		} finally {
			semaphore.release();
		}
	}

	public static class BodyDeferringInputStream extends FilterInputStream {
		private final Future<Response> future;

		private final BodyDeferringAsyncHandler bdah;

		public BodyDeferringInputStream(final Future<Response> future, final BodyDeferringAsyncHandler bdah, final InputStream in) {
			super(in);
			this.future = future;
			this.bdah = bdah;
		}

		public void close() throws IOException {
			// close
			super.close();
			// "join" async request
			try {
				getLastResponse();
			} catch (Exception e) {
				IOException ioe = new IOException(e.getMessage());
				ioe.initCause(e);
				throw ioe;
			}
		}

		public Response getAsapResponse() throws InterruptedException, IOException {
			return bdah.getResponse();
		}

		public Response getLastResponse() throws InterruptedException, ExecutionException {
			return future.get();
		}
	}
}