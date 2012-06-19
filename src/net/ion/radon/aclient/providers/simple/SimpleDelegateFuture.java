package net.ion.radon.aclient.providers.simple;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.ListenableFuture;

public class SimpleDelegateFuture<V> extends SimpleFuture<V> {

	private final ListenableFuture<V> delegateFuture;

	public SimpleDelegateFuture(AsyncHandler<V> asyncHandler, int responseTimeoutInMs, ListenableFuture<V> delegateFuture, HttpURLConnection urlConnection) {
		super(asyncHandler, responseTimeoutInMs, urlConnection);
		this.delegateFuture = delegateFuture;
	}

	public void done(Callable<?> callable) {
		delegateFuture.done(callable);
		super.done(callable);
	}

	public void abort(Throwable t) {
		if (innerFuture != null) {
			innerFuture.cancel(true);
		}
		delegateFuture.abort(t);
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		delegateFuture.cancel(mayInterruptIfRunning);
		if (innerFuture != null) {
			return innerFuture.cancel(mayInterruptIfRunning);
		} else {
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

	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		V content = null;
		try {
			if (innerFuture != null) {
				content = innerFuture.get(timeout, unit);
			}
		} catch (Throwable t) {
			if (!contentProcessed.get() && timeout != -1 && ((System.currentTimeMillis() - touch.get()) <= responseTimeoutInMs)) {
				return get(timeout, unit);
			}
			timedOut.set(true);
			delegateFuture.abort(t);
		}

		if (exception.get() != null) {
			delegateFuture.abort(new ExecutionException(exception.get()));
		}
		delegateFuture.content(content);
		delegateFuture.done(null);
		return content;
	}
}
