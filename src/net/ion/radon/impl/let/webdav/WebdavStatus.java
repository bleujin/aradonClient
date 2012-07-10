package net.ion.radon.impl.let.webdav;

import java.util.Hashtable;

import org.restlet.data.Status;

public class WebdavStatus {

	// ----------------------------------------------------- Instance Variables

	/**
	 * This Hashtable contains the mapping of HTTP and WebDAV status codes to descriptive text. This is a static variable.
	 */
	private static Hashtable<Integer, String> _mapStatusCodes = new Hashtable<Integer, String>();

	// ------------------------------------------------------ HTTP Status Codes

	/**
	 * Status code (200) indicating the request succeeded normally.
	 */
	public static final int SC_OK =  Status.SUCCESS_OK.getCode();

	/**
	 * Status code (201) indicating the request succeeded and created a new resource on the server.
	 */
	public static final int SC_CREATED = Status.SUCCESS_CREATED.getCode();

	/**
	 * Status code (202) indicating that a request was accepted for processing, but was not completed.
	 */
	public static final int SC_ACCEPTED = Status.SUCCESS_ACCEPTED.getCode();

	/**
	 * Status code (204) indicating that the request succeeded but that there was no new information to return.
	 */
	public static final int SC_NO_CONTENT = Status.SUCCESS_NO_CONTENT.getCode();

	/**
	 * Status code (301) indicating that the resource has permanently moved to a new location, and that future references should use a new URI with their requests.
	 */
	public static final int SC_MOVED_PERMANENTLY = Status.REDIRECTION_PERMANENT.getCode();

	/**
	 * Status code (302) indicating that the resource has temporarily moved to another location, but that future references should still use the original URI to access the resource.
	 */
	public static final int SC_MOVED_TEMPORARILY = Status.REDIRECTION_TEMPORARY.getCode();

	/**
	 * Status code (304) indicating that a conditional GET operation found that the resource was available and not modified.
	 */
	public static final int SC_NOT_MODIFIED = Status.REDIRECTION_NOT_MODIFIED.getCode();

	/**
	 * Status code (400) indicating the request sent by the client was syntactically incorrect.
	 */
	public static final int SC_BAD_REQUEST = Status.CLIENT_ERROR_BAD_REQUEST.getCode();

	/**
	 * Status code (401) indicating that the request requires HTTP authentication.
	 */
	public static final int SC_UNAUTHORIZED = Status.CLIENT_ERROR_UNAUTHORIZED.getCode();

	/**
	 * Status code (403) indicating the server understood the request but refused to fulfill it.
	 */
	public static final int SC_FORBIDDEN = Status.CLIENT_ERROR_FORBIDDEN.getCode();

	/**
	 * Status code (404) indicating that the requested resource is not available.
	 */
	public static final int SC_NOT_FOUND = Status.CLIENT_ERROR_NOT_FOUND.getCode();

	/**
	 * Status code (500) indicating an error inside the HTTP service which prevented it from fulfilling the request.
	 */
	public static final int SC_INTERNAL_SERVER_ERROR = Status.SERVER_ERROR_INTERNAL.getCode();

	/**
	 * Status code (501) indicating the HTTP service does not support the functionality needed to fulfill the request.
	 */
	public static final int SC_NOT_IMPLEMENTED = Status.SERVER_ERROR_NOT_IMPLEMENTED.getCode();

	/**
	 * Status code (502) indicating that the HTTP server received an invalid response from a server it consulted when acting as a proxy or gateway.
	 */
	public static final int SC_BAD_GATEWAY = Status.SERVER_ERROR_BAD_GATEWAY.getCode();

	/**
	 * Status code (503) indicating that the HTTP service is temporarily overloaded, and unable to handle the request.
	 */
	public static final int SC_SERVICE_UNAVAILABLE = Status.SERVER_ERROR_SERVICE_UNAVAILABLE.getCode();

	/**
	 * Status code (100) indicating the client may continue with its request. This interim response is used to inform the client that the initial part of the request has been received and has not yet been rejected by the server.
	 */
	public static final int SC_CONTINUE = 100;

	/**
	 * Status code (405) indicating the method specified is not allowed for the resource.
	 */
	public static final int SC_METHOD_NOT_ALLOWED = 405;

	/**
	 * Status code (409) indicating that the request could not be completed due to a conflict with the current state of the resource.
	 */
	public static final int SC_CONFLICT = 409;

	/**
	 * Status code (412) indicating the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
	 */
	public static final int SC_PRECONDITION_FAILED = 412;

	/**
	 * Status code (413) indicating the server is refusing to process a request because the request entity is larger than the server is willing or able to process.
	 */
	public static final int SC_REQUEST_TOO_LONG = 413;

	/**
	 * Status code (415) indicating the server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.
	 */
	public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;

	// -------------------------------------------- Extended WebDav status code

	/**
	 * Status code (207) indicating that the response requires providing status for multiple independent operations.
	 */
	public static final int SC_MULTI_STATUS = 207;

	// This one colides with HTTP 1.1
	// "207 Parital Update OK"

	/**
	 * Status code (418) indicating the entity body submitted with the PATCH method was not understood by the resource.
	 */
	public static final int SC_UNPROCESSABLE_ENTITY = 418;

	// This one colides with HTTP 1.1
	// "418 Reauthentication Required"

	/**
	 * Status code (419) indicating that the resource does not have sufficient space to record the state of the resource after the execution of this method.
	 */
	public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419;

	// This one colides with HTTP 1.1
	// "419 Proxy Reauthentication Required"

	/**
	 * Status code (420) indicating the method was not executed on a particular resource within its scope because some part of the method's execution failed causing the entire method to be aborted.
	 */
	public static final int SC_METHOD_FAILURE = 420;

	/**
	 * Status code (423) indicating the destination resource of a method is locked, and either the request did not contain a valid Lock-Info header, or the Lock-Info header identifies a lock held by another principal.
	 */
	public static final int SC_LOCKED = 423;

	// ------------------------------------------------------------ Initializer

	static {
		// HTTP 1.0 Status Code
		addStatusCodeMap(SC_OK, "OK");
		addStatusCodeMap(SC_CREATED, "Created");
		addStatusCodeMap(SC_ACCEPTED, "Accepted");
		addStatusCodeMap(SC_NO_CONTENT, "No Content");
		addStatusCodeMap(SC_MOVED_PERMANENTLY, "Moved Permanently");
		addStatusCodeMap(SC_MOVED_TEMPORARILY, "Moved Temporarily");
		addStatusCodeMap(SC_NOT_MODIFIED, "Not Modified");
		addStatusCodeMap(SC_BAD_REQUEST, "Bad Request");
		addStatusCodeMap(SC_UNAUTHORIZED, "Unauthorized");
		addStatusCodeMap(SC_FORBIDDEN, "Forbidden");
		addStatusCodeMap(SC_NOT_FOUND, "Not Found");
		addStatusCodeMap(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
		addStatusCodeMap(SC_NOT_IMPLEMENTED, "Not Implemented");
		addStatusCodeMap(SC_BAD_GATEWAY, "Bad Gateway");
		addStatusCodeMap(SC_SERVICE_UNAVAILABLE, "Service Unavailable");
		addStatusCodeMap(SC_CONTINUE, "Continue");
		addStatusCodeMap(SC_METHOD_NOT_ALLOWED, "Method Not Allowed");
		addStatusCodeMap(SC_CONFLICT, "Conflict");
		addStatusCodeMap(SC_PRECONDITION_FAILED, "Precondition Failed");
		addStatusCodeMap(SC_REQUEST_TOO_LONG, "Request Too Long");
		addStatusCodeMap(SC_UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type");
		// WebDav Status Codes
		addStatusCodeMap(SC_MULTI_STATUS, "Multi-Status");
		addStatusCodeMap(SC_UNPROCESSABLE_ENTITY, "Unprocessable Entity");
		addStatusCodeMap(SC_INSUFFICIENT_SPACE_ON_RESOURCE, "Insufficient Space On Resource");
		addStatusCodeMap(SC_METHOD_FAILURE, "Method Failure");
		addStatusCodeMap(SC_LOCKED, "Locked");
	}

	public static String getStatusText(int nHttpStatusCode) {
		Integer intKey = new Integer(nHttpStatusCode);

		if (!_mapStatusCodes.containsKey(intKey)) {
			return "";
		} else {
			return (String) _mapStatusCodes.get(intKey);
		}
	}

	private static void addStatusCodeMap(int nKey, String strVal) {
		_mapStatusCodes.put(new Integer(nKey), strVal);
	}

};
