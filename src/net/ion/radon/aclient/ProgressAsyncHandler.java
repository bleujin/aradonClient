package net.ion.radon.aclient;


public interface ProgressAsyncHandler<T> extends AsyncHandler<T> {

	STATE onHeaderWriteCompleted();

	STATE onContentWriteCompleted();

	STATE onContentWriteProgress(long amount, long current, long total);

}
