package net.ion.radon.aclient.providers.netty;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.ion.framework.util.Debug;
import net.ion.nradon.netty.codec.http.websocketx.BinaryWebSocketFrame;
import net.ion.nradon.netty.codec.http.websocketx.PingWebSocketFrame;
import net.ion.nradon.netty.codec.http.websocketx.PongWebSocketFrame;
import net.ion.nradon.netty.codec.http.websocketx.TextWebSocketFrame;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketByteListener;
import net.ion.radon.aclient.websocket.WebSocketCloseCodeReasonListener;
import net.ion.radon.aclient.websocket.WebSocketListener;
import net.ion.radon.aclient.websocket.WebSocketTextListener;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

public class NettyWebSocket implements WebSocket {
	private final Channel channel;
	private final ConcurrentLinkedQueue<WebSocketListener> listeners = new ConcurrentLinkedQueue<WebSocketListener>();
	private ChannelFuture future;

	public NettyWebSocket(Channel channel) {
		this.channel = channel;
	}

	public WebSocket stream(byte[] fragment, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket stream(byte[] fragment, int offset, int len, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket sendMessage(byte[] message) {
		this.future = channel.write(new BinaryWebSocketFrame(wrappedBuffer(message)));
		return this;
	}

	public WebSocket sendTextMessage(String message) {
		this.future = channel.write(new TextWebSocketFrame(message));
		return this;
	}
	
	public void flush() {
		if (future == null) return ;
		
		try {
			future.await() ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public WebSocket streamText(String fragment, boolean last) {
		throw new UnsupportedOperationException("Streaming currently not supported.");
	}

	public WebSocket sendPing(byte[] payload) {
		this.future = channel.write(new PingWebSocketFrame(wrappedBuffer(payload)));
		return this;
	}

	public WebSocket sendPong(byte[] payload) {
		this.future = channel.write(new PongWebSocketFrame(wrappedBuffer(payload)));
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
		flush() ;
		
		onClose();
		listeners.clear();
		channel.close();
	}

	protected void onMessage(byte[] message) {
		for (WebSocketListener listener : listeners) {
			if (WebSocketByteListener.class.isAssignableFrom(listener.getClass())) {
				try {
					WebSocketByteListener.class.cast(listener).onMessage(message);
				} catch (Exception ex) {
					listener.onError(ex);
				}
			}
		}
	}

	protected void onTextMessage(String message) {
		for (WebSocketListener listener : listeners) {
			if (WebSocketTextListener.class.isAssignableFrom(listener.getClass())) {
				try {
					WebSocketTextListener.class.cast(listener).onMessage(message);
				} catch (Exception ex) {
					listener.onError(ex);
				}
			}
		}
	}

	protected void onError(Throwable t) {
		for (WebSocketListener listener : listeners) {
			try {
				listener.onError(t);
			} catch (Throwable ignore) {
				ignore.printStackTrace() ;
			}

		}
	}

	protected void onClose() {
		onClose(1000, "Normal closure; the connection successfully completed whatever purpose for which it was created.");
	}

	protected void onClose(int code, String reason) {
		for (WebSocketListener listener : listeners) {
			try {
				if (WebSocketCloseCodeReasonListener.class.isAssignableFrom(listener.getClass())) {
					WebSocketCloseCodeReasonListener.class.cast(listener).onClose(this, code, reason);
				}
				listener.onClose(this);
			} catch (Throwable t) {
				listener.onError(t);
			}
		}
	}

	@Override
	public String toString() {
		return "NettyWebSocket{" + "channel=" + channel + '}';
	}
}
