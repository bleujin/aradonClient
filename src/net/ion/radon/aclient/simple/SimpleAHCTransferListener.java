package net.ion.radon.aclient.simple;


public interface SimpleAHCTransferListener {

	void onStatus(String url, int statusCode, String statusText);

	void onHeaders(String url, HeaderMap headers);

	void onBytesReceived(String url, long amount, long current, long total);

	void onBytesSent(String url, long amount, long current, long total);

	void onCompleted(String url, int statusCode, String statusText);

}
