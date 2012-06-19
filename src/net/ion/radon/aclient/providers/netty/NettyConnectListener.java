package net.ion.radon.aclient.providers.netty;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HostnameVerifier;

import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.Request;

import org.apache.log4j.spi.LoggerFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.ssl.SslHandler;

/**
 * Non Blocking connect.
 */
final class NettyConnectListener<T> implements ChannelFutureListener {
	private final ClientConfig config;
	private final NettyResponseFuture<T> future;
	private final HttpRequest nettyRequest;
	private final AtomicBoolean handshakeDone = new AtomicBoolean(false);

	private NettyConnectListener(ClientConfig config, NettyResponseFuture<T> future, HttpRequest nettyRequest) {
		this.config = config;
		this.future = future;
		this.nettyRequest = nettyRequest;
	}

	public NettyResponseFuture<T> future() {
		return future;
	}

	public final void operationComplete(ChannelFuture f) throws Exception {
		if (f.isSuccess()) {
			Channel channel = f.getChannel();
			channel.getPipeline().getContext(NettyProvider.class).setAttachment(future);
			SslHandler sslHandler = (SslHandler) channel.getPipeline().get(NettyProvider.SSL_HANDLER);
			if (!handshakeDone.getAndSet(true) && (sslHandler != null)) {
				((SslHandler) channel.getPipeline().get(NettyProvider.SSL_HANDLER)).handshake().addListener(this);
				return;
			}

			HostnameVerifier v = config.getHostnameVerifier();
			if (sslHandler != null) {
				if (!v.verify(future.getURI().getHost(), sslHandler.getEngine().getSession())) {
					ConnectException exception = new ConnectException("HostnameVerifier exception.");
					future.abort(exception);
					throw exception;
				}
			}

			future.provider().writeRequest(f.getChannel(), config, future, nettyRequest);
		} else {
			Throwable cause = f.getCause();

			if (future.canRetry() && cause != null && (NettyProvider.abortOnDisconnectException(cause) || ClosedChannelException.class.isAssignableFrom(cause.getClass()) || future.getState() != NettyResponseFuture.STATE.NEW)) {

				if (future.provider().remotelyClosed(f.getChannel(), future)) {
					return;
				}
			}

			boolean printCause = f.getCause() != null && cause.getMessage() != null;
			ConnectException e = new ConnectException(printCause ? cause.getMessage() + " to " + future.getURI().toString() : future.getURI().toString());
			if (cause != null) {
				e.initCause(cause);
			}
			future.abort(e);
		}
	}

	public static class Builder<T> {
		private final ClientConfig config;

		private final Request request;
		private final AsyncHandler<T> asyncHandler;
		private NettyResponseFuture<T> future;
		private final NettyProvider provider;
		private final ChannelBuffer buffer;

		public Builder(ClientConfig config, Request request, AsyncHandler<T> asyncHandler, NettyProvider provider, ChannelBuffer buffer) {

			this.config = config;
			this.request = request;
			this.asyncHandler = asyncHandler;
			this.future = null;
			this.provider = provider;
			this.buffer = buffer;
		}

		public Builder(ClientConfig config, Request request, AsyncHandler<T> asyncHandler, NettyResponseFuture<T> future, NettyProvider provider, ChannelBuffer buffer) {

			this.config = config;
			this.request = request;
			this.asyncHandler = asyncHandler;
			this.future = future;
			this.provider = provider;
			this.buffer = buffer;
		}

		public NettyConnectListener<T> build(final URI uri) throws IOException {
			HttpRequest nettyRequest = NettyProvider.buildRequest(config, request, uri, true, buffer);
			if (future == null) {
				future = NettyProvider.newFuture(uri, request, asyncHandler, nettyRequest, config, provider);
			} else {
				future.setNettyRequest(nettyRequest);
				future.setRequest(request);
			}
			return new NettyConnectListener<T>(config, future, nettyRequest);
		}
	}
}
