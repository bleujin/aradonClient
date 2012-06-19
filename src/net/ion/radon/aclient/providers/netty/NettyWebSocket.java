package net.ion.radon.aclient.providers.netty;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketByteListener;
import net.ion.radon.aclient.websocket.WebSocketCloseCodeReasonListener;
import net.ion.radon.aclient.websocket.WebSocketListener;
import net.ion.radon.aclient.websocket.WebSocketTextListener;

import org.apache.log4j.spi.LoggerFactory;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class NettyWebSocket implements WebSocket {
	private final Channel channel;
	private final ConcurrentLinkedQueue<WebSocketListener> listeners = new ConcurrentLinkedQueue<WebSocketListener>();

	public NettyWebSocket(Channel channel) {
		this.channel = channel;
	}

	public WebSocket sendMessage(byte[] message) {
		channel.write(new BinaryWebSocketFrame(wrappedBuffer(message)));
		return this;
	}

	public WebSocket stream(byte[] fragment, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket stream(byte[] fragment, int offset, int len, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket sendTextMessage(String message) {
		channel.write(new TextWebSocketFrame(message));
		return this;
	}

	public WebSocket streamText(String fragment, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket sendPing(byte[] payload) {
		channel.write(new PingWebSocketFrame(wrappedBuffer(payload)));
		return this;
	}

	public WebSocket sendPong(byte[] payload) {
		channel.write(new PongWebSocketFrame(wrappedBuffer(payload)));
		return this;
	}

	public WebSocket addWebSocketListener(WebSocketListener l) {
		listeners.add(l);
		return this;
	}

	public WebSocket removeWebSocketListener(WebSocketListener l) {
		listeners.remove(l);
		return this;
	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public void close() {
		onClose();
		listeners.clear();
		channel.close();
	}

	protected void onMessage(byte[] message) {
		for (WebSocketListener l : listeners) {
			if (WebSocketByteListener.class.isAssignableFrom(l.getClass())) {
				try {
					WebSocketByteListener.class.cast(l).onMessage(message);
				} catch (Exception ex) {
					l.onError(ex);
				}
			}
		}
	}

	protected void onTextMessage(String message) {
		for (WebSocketListener l : listeners) {
			if (WebSocketTextListener.class.isAssignableFrom(l.getClass())) {
				try {
					WebSocketTextListener.class.cast(l).onMessage(message);
				} catch (Exception ex) {
					l.onError(ex);
				}
			}
		}
	}

	protected void onError(Throwable t) {
		for (WebSocketListener l : listeners) {
			try {
				l.onError(t);
			} catch (Throwable ignore) {
				ignore.printStackTrace() ;
			}

		}
	}

	protected void onClose() {
		onClose(1000, "Normal closure; the connection successfully completed whatever purpose for which it was created.");
	}

	protected void onClose(int code, String reason) {
		for (WebSocketListener l : listeners) {
			try {
				if (WebSocketCloseCodeReasonListener.class.isAssignableFrom(l.getClass())) {
					WebSocketCloseCodeReasonListener.class.cast(l).onClose(this, code, reason);
				}
				l.onClose(this);
			} catch (Throwable t) {
				l.onError(t);
			}
		}
	}

	@Override
	public String toString() {
		return "NettyWebSocket{" + "channel=" + channel + '}';
	}
}
