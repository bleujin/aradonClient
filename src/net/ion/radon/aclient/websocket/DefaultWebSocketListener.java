package net.ion.radon.aclient.websocket;

public class DefaultWebSocketListener implements WebSocketByteListener, WebSocketTextListener, WebSocketPingListener, WebSocketPongListener {

	protected WebSocket webSocket;

	@Override
	public void onMessage(byte[] message) {
	}

	@Override
	public void onFragment(byte[] fragment, boolean last) {
	}

	@Override
	public void onPing(byte[] message) {
	}

	@Override
	public void onPong(byte[] message) {
	}

	@Override
	public void onMessage(String message) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onFragment(String fragment, boolean last) {
	}

	@Override
	public void onOpen(WebSocket websocket) {
		this.webSocket = websocket;
	}

	@Override
	public void onClose(WebSocket websocket) {
		this.webSocket = null;
	}

	@Override
	public void onError(Throwable t) {
	}
}
