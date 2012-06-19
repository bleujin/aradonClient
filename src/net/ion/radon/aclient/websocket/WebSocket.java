package net.ion.radon.aclient.websocket;

import java.io.Closeable;

public interface WebSocket extends Closeable{

	WebSocket sendMessage(byte[] message);

	WebSocket stream(byte[] fragment, boolean last);

	WebSocket stream(byte[] fragment, int offset, int len, boolean last);

	WebSocket sendTextMessage(String message);

	WebSocket streamText(String fragment, boolean last);

	WebSocket sendPing(byte[] payload);

	WebSocket sendPong(byte[] payload);

	WebSocket addWebSocketListener(WebSocketListener l);

	WebSocket removeWebSocketListener(WebSocketListener l);

	boolean isOpen();

	void close();
}
