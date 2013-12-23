/*
 * Copyright 2010 Bruno de Carvalho
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ion.radon.aclient.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroupFuture;

public class CleanupChannelGroup extends DefaultChannelGroup {

	// internal vars --------------------------------------------------------------------------------------------------

	private final AtomicBoolean closed;
	private final ReentrantReadWriteLock lock;

	// constructors ---------------------------------------------------------------------------------------------------

	public CleanupChannelGroup() {
		this.closed = new AtomicBoolean(false);
		this.lock = new ReentrantReadWriteLock();
	}

	public CleanupChannelGroup(String name) {
		super(name);
		this.closed = new AtomicBoolean(false);
		this.lock = new ReentrantReadWriteLock();
	}

	// DefaultChannelGroup --------------------------------------------------------------------------------------------

	@Override
	public ChannelGroupFuture close() {
		this.lock.writeLock().lock();
		try {
			if (!this.closed.getAndSet(true)) {
				// First time close() is called.
				return super.close();
			} else {
				Collection<ChannelFuture> futures = new ArrayList<ChannelFuture>();
				return new DefaultChannelGroupFuture(ChannelGroup.class.cast(this), futures);
			}
		} finally {
			this.lock.writeLock().unlock();
		}
	}

	@Override
	public boolean add(Channel channel) {
		// Synchronization must occur to avoid add() and close() overlap (thus potentially leaving one channel open).
		// This could also be done by synchronizing the method itself but using a read lock here (rather than a
		// synchronized() block) allows multiple concurrent calls to add().
		this.lock.readLock().lock();
		try {
			if (this.closed.get()) {
				// Immediately close channel, as close() was already called.
				channel.close();
				return false;
			}

			return super.add(channel);
		} finally {
			this.lock.readLock().unlock();
		}
	}
}
