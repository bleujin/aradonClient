package net.ion.radon.aclient.websocket;

public interface WebSocketCloseCodeReasonListener {

	void onClose(WebSocket websocket, int code, String reason);
}
