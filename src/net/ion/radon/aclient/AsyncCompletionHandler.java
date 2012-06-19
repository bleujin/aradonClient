package net.ion.radon.aclient;


public abstract class AsyncCompletionHandler<T> implements AsyncHandler<T>, ProgressAsyncHandler<T> {

	private final Response.ResponseBuilder builder = new Response.ResponseBuilder();

	public STATE onBodyPartReceived(final HttpResponseBodyPart content) throws Exception {
		builder.accumulate(content);
		return STATE.CONTINUE;
	}

	public STATE onStatusReceived(final HttpResponseStatus status) throws Exception {
		builder.reset();
		builder.accumulate(status);
		return STATE.CONTINUE;
	}

	public STATE onHeadersReceived(final HttpResponseHeaders headers) throws Exception {
		builder.accumulate(headers);
		return STATE.CONTINUE;
	}

	public final T onCompleted() throws Exception {
		return onCompleted(builder.build());
	}

	public void onThrowable(Throwable t) {
		t.printStackTrace() ;
	}

	abstract public T onCompleted(Response response) throws Exception;

	public STATE onHeaderWriteCompleted() {
		return STATE.CONTINUE;
	}

	public STATE onContentWriteCompleted() {
		return STATE.CONTINUE;
	}

	public STATE onContentWriteProgress(long amount, long current, long total) {
		return STATE.CONTINUE;
	}
}
