package net.ion.radon.aclient.websocket;

public interface WebSocketByteListener extends WebSocketListener {

	void onMessage(byte[] message);

	void onFragment(byte[] fragment, boolean last);

}
