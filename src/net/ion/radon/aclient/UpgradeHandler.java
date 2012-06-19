package net.ion.radon.aclient;


public interface UpgradeHandler<T> {

	void onSuccess(T t);

	void onFailure(Throwable t);

}
