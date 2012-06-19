package net.ion.radon.aclient;

public interface ConnectionsPool<U, V> {

	public boolean offer(U uri, V connection);

	public V poll(U uri);

	public boolean removeAll(V connection);

	public boolean canCacheConnection();

	public void destroy();
}
