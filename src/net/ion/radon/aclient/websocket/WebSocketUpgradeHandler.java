package net.ion.radon.aclient.websocket;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.UpgradeHandler;

public class WebSocketUpgradeHandler implements UpgradeHandler<WebSocket>, AsyncHandler<WebSocket> {

	private WebSocket webSocket;
	private final ConcurrentLinkedQueue<WebSocketListener> l;
	@SuppressWarnings("unused")
	private final String protocol;
	@SuppressWarnings("unused")
	private final long maxByteSize;
	@SuppressWarnings("unused")
	private final long maxTextSize;
	private final AtomicBoolean ok = new AtomicBoolean(false);

	private WebSocketUpgradeHandler(Builder b) {
		l = b.l;
		protocol = b.protocol;
		maxByteSize = b.maxByteSize;
		maxTextSize = b.maxTextSize;
	}

	@Override
	public final void onThrowable(Throwable t) {
		onFailure(t);
	}

	@Override
	public final STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
		return STATE.CONTINUE;
	}

	@Override
	public final STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
		if (responseStatus.getStatusCode() == 101) {
			return STATE.UPGRADE;
		} else {
			throw new IllegalStateException("Invalid upgrade protocol, status should be 101 but was " + responseStatus.getStatusCode());
		}
	}

	@Override
	public final STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
		return STATE.CONTINUE;
	}

	@Override
	public final WebSocket onCompleted() {
		if (webSocket == null) {
			throw new IllegalStateException("WebSocket is null");
		}
		return webSocket;
	}

	@Override
	public final void onSuccess(WebSocket webSocket) {
		this.webSocket = webSocket;
		for (WebSocketListener w : l) {
			webSocket.addWebSocketListener(w);
			w.onOpen(webSocket);
		}
		ok.set(true);
	}

	@Override
	public final void onFailure(Throwable t) {
		for (WebSocketListener w : l) {
			if (!ok.get() && webSocket != null) {
				webSocket.addWebSocketListener(w);
			}
			w.onError(t);
		}
	}

	public final static class Builder {
		private ConcurrentLinkedQueue<WebSocketListener> l = new ConcurrentLinkedQueue<WebSocketListener>();
		private String protocol = "";
		private long maxByteSize = 8192;
		private long maxTextSize = 8192;

		public Builder addWebSocketListener(WebSocketListener listener) {
			l.add(listener);
			return this;
		}

		public Builder removeWebSocketListener(WebSocketListener listener) {
			l.remove(listener);
			return this;
		}

		public Builder setProtocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder setMaxByteSize(long maxByteSize) {
			this.maxByteSize = maxByteSize;
			return this;
		}

		public Builder setMaxTextSize(long maxTextSize) {
			this.maxTextSize = maxTextSize;
			return this;
		}

		public WebSocketUpgradeHandler build() {
			return new WebSocketUpgradeHandler(this);
		}
	}
}
