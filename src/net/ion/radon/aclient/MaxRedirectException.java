package net.ion.radon.aclient;

public class MaxRedirectException extends Exception {
	private static final long serialVersionUID = 1L;

	public MaxRedirectException() {
		super();
	}

	public MaxRedirectException(String msg) {
		super(msg);
	}

	public MaxRedirectException(Throwable cause) {
		super(cause);
	}

	public MaxRedirectException(String message, Throwable cause) {
		super(message, cause);
	}
}