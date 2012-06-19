package net.ion.radon.aclient.websocket;

public interface WebSocketPingListener extends WebSocketListener {

	void onPing(byte[] message);

}
