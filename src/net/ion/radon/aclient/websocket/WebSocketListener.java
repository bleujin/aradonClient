package net.ion.radon.aclient.websocket;

public interface WebSocketListener {

	void onOpen(WebSocket websocket);

	void onClose(WebSocket websocket);

	void onError(Throwable t);

}
