package net.ion.radon.aclient.listenable;

import java.util.concurrent.Executor;

import net.ion.radon.aclient.ListenableFuture;

public abstract class AbstractListenableFuture<V> implements ListenableFuture<V> {

	// The execution list to hold our executors.
	private final ExecutionList executionList = new ExecutionList();

	public ListenableFuture<V> addListener(Runnable listener, Executor exec) {
		executionList.add(listener, exec);
		return this;
	}

	protected void done() {
		executionList.run();
	}
}
