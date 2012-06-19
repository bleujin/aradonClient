package net.ion.radon.aclient.filter;

@SuppressWarnings("serial")
public class FilterException extends Exception {

	public FilterException(final String message) {
		super(message);
	}

	public FilterException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
