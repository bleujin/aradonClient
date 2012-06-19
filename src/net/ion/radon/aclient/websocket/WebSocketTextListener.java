package net.ion.radon.aclient.websocket;

public interface WebSocketTextListener extends WebSocketListener {

	void onMessage(String message);

	void onFragment(String fragment, boolean last);

}
