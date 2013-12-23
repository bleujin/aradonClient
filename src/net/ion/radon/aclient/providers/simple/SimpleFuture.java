package net.ion.radon.aclient.providers.simple;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.listenable.AbstractListenableFuture;

public class SimpleFuture<V> extends AbstractListenableFuture<V> {

	protected Future<V> innerFuture;
	protected final AsyncHandler<V> asyncHandler;
	protected final int responseTimeoutInMs;
	protected final AtomicBoolean cancelled = new AtomicBoolean(false);
	protected final AtomicBoolean timedOut = new AtomicBoolean(false);
	protected final AtomicBoolean isDone = new AtomicBoolean(false);
	protected final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
	protected final AtomicLong touch = new AtomicLong(System.currentTimeMillis());
	protected final AtomicBoolean contentProcessed = new AtomicBoolean(false);
	protected final HttpURLConnection urlConnection;
	private boolean writeHeaders;
	private boolean writeBody;

	public SimpleFuture(AsyncHandler<V> asyncHandler, int responseTimeoutInMs, HttpURLConnection urlConnection) {
		this.asyncHandler = asyncHandler;
		this.responseTimeoutInMs = responseTimeoutInMs;
		this.urlConnection = urlConnection;
		writeHeaders = true;
		writeBody = true;
	}

	protected void setInnerFuture(Future<V> innerFuture) {
		this.innerFuture = innerFuture;
	}

	public void done(Callable<?> callable) {
		isDone.set(true);
		super.done();
	}

	public void abort(Throwable t) {
		exception.set(t);
		if (innerFuture != null) {
			innerFuture.cancel(true);
		}
		if (!timedOut.get() && !cancelled.get()) {
			asyncHandler.onThrowable(t);
		}
		super.done();
	}

	public void content(V v) {
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		if (!cancelled.get() && innerFuture != null) {
			urlConnection.disconnect();
			asyncHandler.onThrowable(new CancellationException());
			cancelled.set(true);
			super.done();
			return innerFuture.cancel(mayInterruptIfRunning);
		} else {
			super.done();
			return false;
		}
	}

	public boolean isCancelled() {
		if (innerFuture != null) {
			return innerFuture.isCancelled();
		} else {
			return false;
		}
	}

	public boolean isDone() {
		contentProcessed.set(true);
		return innerFuture.isDone();
	}

	public V get() throws InterruptedException, ExecutionException {
		try {
			return get(responseTimeoutInMs, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new ExecutionException(e);
		}
	}

	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		V content = null;
		try {
			if (innerFuture != null) {
				content = innerFuture.get(timeout, unit);
			}
		} catch (TimeoutException t) {
			if (!contentProcessed.get() && timeout != -1 && ((System.currentTimeMillis() - touch.get()) <= responseTimeoutInMs)) {
				return get(timeout, unit);
			}

			if (exception.get() == null) {
				timedOut.set(true);
				throw new ExecutionException(new TimeoutException(String.format("No response received after %s", responseTimeoutInMs)));
			}
		} catch (CancellationException ce) {
		}

		if (exception.get() != null) {
			throw new ExecutionException(exception.get());
		}
		return content;
	}

	/**
	 * Is the Future still valid
	 * 
	 * @return <code>true</code> if response has expired and should be terminated.
	 */
	public boolean hasExpired() {
		return responseTimeoutInMs != -1 && ((System.currentTimeMillis() - touch.get()) > responseTimeoutInMs);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public void touch() {
		touch.set(System.currentTimeMillis());
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public boolean getAndSetWriteHeaders(boolean writeHeaders) {
		boolean b = this.writeHeaders;
		this.writeHeaders = writeHeaders;
		return b;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public boolean getAndSetWriteBody(boolean writeBody) {
		boolean b = this.writeBody;
		this.writeBody = writeBody;
		return b;
	}
}
