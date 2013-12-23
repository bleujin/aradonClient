package net.ion.radon.aclient.providers.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.ion.framework.util.Debug;
import net.ion.radon.aclient.ConnectionsPool;

import org.jboss.netty.channel.Channel;

/**
 * A simple implementation of {@link net.ion.radon.aclient.ConnectionsPool} based on a {@link java.util.concurrent.ConcurrentHashMap}
 */
public class NettyConnectionsPool implements ConnectionsPool<String, Channel> {

	private final ConcurrentHashMap<String, ConcurrentLinkedQueue<IdleChannel>> connectionsPool = new ConcurrentHashMap<String, ConcurrentLinkedQueue<IdleChannel>>();
	private final ConcurrentHashMap<Channel, IdleChannel> channel2IdleChannel = new ConcurrentHashMap<Channel, IdleChannel>();
	private final AtomicBoolean isClosed = new AtomicBoolean(false);
	private final Timer idleConnectionDetector = new Timer(true);
	private final boolean sslConnectionPoolEnabled;
	private final int maxTotalConnections;
	private final int maxConnectionPerHost;
	private final long maxIdleTime;

	public NettyConnectionsPool(NettyProvider provider) {
		this.maxTotalConnections = provider.getConfig().getMaxTotalConnections();
		this.maxConnectionPerHost = provider.getConfig().getMaxConnectionPerHost();
		this.sslConnectionPoolEnabled = provider.getConfig().isSslConnectionPoolEnabled();
		this.maxIdleTime = provider.getConfig().getIdleConnectionInPoolTimeoutInMs();
		this.idleConnectionDetector.schedule(new IdleChannelDetector(), maxIdleTime, maxIdleTime);
	}

	private static class IdleChannel {
		final String uri;
		final Channel channel;
		final long start;

		IdleChannel(String uri, Channel channel) {
			this.uri = uri;
			this.channel = channel;
			this.start = System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof IdleChannel))
				return false;

			IdleChannel that = (IdleChannel) o;

			if (channel != null ? !channel.equals(that.channel) : that.channel != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return channel != null ? channel.hashCode() : 0;
		}
	}

	private class IdleChannelDetector extends TimerTask {
		@Override
		public void run() {
			try {
				if (isClosed.get())
					return;

				List<IdleChannel> channelsInTimeout = new ArrayList<IdleChannel>();
				long currentTime = System.currentTimeMillis();

				for (IdleChannel idleChannel : channel2IdleChannel.values()) {
					long age = currentTime - idleChannel.start;
					if (age > maxIdleTime) {

						// store in an unsynchronized list to minimize the impact on the ConcurrentHashMap.
						channelsInTimeout.add(idleChannel);
					}
				}
				long endConcurrentLoop = System.currentTimeMillis();

				for (IdleChannel idleChannel : channelsInTimeout) {
					Object attachment = idleChannel.channel.getPipeline().getContext(NettyProvider.class).getAttachment();
					if (attachment != null) {
						if (NettyResponseFuture.class.isAssignableFrom(attachment.getClass())) {
							NettyResponseFuture<?> future = (NettyResponseFuture<?>) attachment;

							if (!future.isDone() && !future.isCancelled()) {
								continue;
							}
						}
					}

					if (remove(idleChannel)) {
						close(idleChannel.channel);
					}
				}

			} catch (Throwable t) {
				t.printStackTrace() ;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean offer(String uri, Channel channel) {
		if (isClosed.get())
			return false;

		if (!sslConnectionPoolEnabled && uri.startsWith("https")) {
			return false;
		}

		channel.getPipeline().getContext(NettyProvider.class).setAttachment(new NettyProvider.DiscardEvent());

		ConcurrentLinkedQueue<IdleChannel> idleConnectionForHost = connectionsPool.get(uri);
		if (idleConnectionForHost == null) {
			ConcurrentLinkedQueue<IdleChannel> newPool = new ConcurrentLinkedQueue<IdleChannel>();
			idleConnectionForHost = connectionsPool.putIfAbsent(uri, newPool);
			if (idleConnectionForHost == null)
				idleConnectionForHost = newPool;
		}

		boolean added;
		int size = idleConnectionForHost.size();
		if (maxConnectionPerHost == -1 || size < maxConnectionPerHost) {
			IdleChannel idleChannel = new IdleChannel(uri, channel);
			synchronized (idleConnectionForHost) {
				added = idleConnectionForHost.add(idleChannel);

				if (channel2IdleChannel.put(channel, idleChannel) != null) {
					Debug.error("Channel {} already exists in the connections pool!", channel);
				}
			}
		} else {
			added = false;
		}
		return added;
	}

	/**
	 * {@inheritDoc}
	 */
	public Channel poll(String uri) {
		if (!sslConnectionPoolEnabled && uri.startsWith("https")) {
			return null;
		}

		IdleChannel idleChannel = null;
		ConcurrentLinkedQueue<IdleChannel> idleConnectionForHost = connectionsPool.get(uri);
		if (idleConnectionForHost != null) {
			boolean poolEmpty = false;
			while (!poolEmpty && idleChannel == null) {
				if (idleConnectionForHost.size() > 0) {
					synchronized (idleConnectionForHost) {
						idleChannel = idleConnectionForHost.poll();
						if (idleChannel != null) {
							channel2IdleChannel.remove(idleChannel.channel);
						}
					}
				}

				if (idleChannel == null) {
					poolEmpty = true;
				} else if (!idleChannel.channel.isConnected() || !idleChannel.channel.isOpen()) {
					idleChannel = null;
				}
			}
		}
		return idleChannel != null ? idleChannel.channel : null;
	}

	private boolean remove(IdleChannel pooledChannel) {
		if (pooledChannel == null || isClosed.get())
			return false;

		boolean isRemoved = false;
		ConcurrentLinkedQueue<IdleChannel> pooledConnectionForHost = connectionsPool.get(pooledChannel.uri);
		if (pooledConnectionForHost != null) {
			isRemoved = pooledConnectionForHost.remove(pooledChannel);
		}
		isRemoved |= channel2IdleChannel.remove(pooledChannel.channel) != null;
		return isRemoved;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(Channel channel) {
		return !isClosed.get() && remove(channel2IdleChannel.get(channel));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canCacheConnection() {
		if (!isClosed.get() && maxTotalConnections != -1 && channel2IdleChannel.size() >= maxTotalConnections) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() {
		if (isClosed.getAndSet(true))
			return;

		// stop timer
		idleConnectionDetector.cancel();
		idleConnectionDetector.purge();

		for (Channel channel : channel2IdleChannel.keySet()) {
			close(channel);
		}
		connectionsPool.clear();
		channel2IdleChannel.clear();
	}

	private void close(Channel channel) {
		try {
			channel.getPipeline().getContext(NettyProvider.class).setAttachment(new NettyProvider.DiscardEvent());
			channel.close();
		} catch (Throwable t) {
			// noop
		}
	}

	public final String toString() {
		return String.format("NettyConnectionPool: {pool-size: %d}", channel2IdleChannel.size());
	}
}
