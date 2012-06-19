package net.ion.radon.aclient;


public interface AsyncHandler<T> {

	public static enum STATE {

		ABORT, CONTINUE, UPGRADE
	}

	void onThrowable(Throwable ex);

	STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception;

	STATE onStatusReceived(HttpResponseStatus status) throws Exception;

	STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception;

	T onCompleted() throws Exception;
}
