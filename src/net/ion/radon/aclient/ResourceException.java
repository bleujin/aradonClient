package net.ion.radon.aclient;

public class ResourceException extends RuntimeException {

	private static final long serialVersionUID = -8388735634530572261L;
	private final Status status;
	
	public ResourceException(int code) {
		this(new Status(code));
	}

	public ResourceException(int code, String name, String description,
			String uri) {
		this(new Status(code, name, description, uri));
	}

	public ResourceException(int code, String name, String description,
			String uri, Throwable cause) {
		this(new Status(code, cause, name, description, uri), cause);
	}

	public ResourceException(int code, Throwable cause) {
		this(new Status(code, cause), cause);
	}

	public ResourceException(Status status) {
		this(status, status != null ? status.getThrowable() : null);
	}

	public ResourceException(Status status, String description) {
		this(new Status(status, description));
	}

	public ResourceException(Status status, String description, Throwable cause) {
		this(new Status(status, cause, description), cause);
	}

	public ResourceException(Status status, Throwable cause) {
		super(status != null ? status.getReasonPhrase() : null, cause);
		this.status = status;
	}

	public ResourceException(Throwable cause) {
		this(new Status(Status.SERVER_ERROR_INTERNAL, cause), cause);
	}

	public Status getStatus() {
		return status;
	}

	public String toString() {
		return getStatus().toString();
	}

}
