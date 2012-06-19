package net.ion.radon.aclient.websocket;

public interface WebSocketPongListener extends WebSocketListener {

	void onPong(byte[] message);

}
