package net.ion.radon.aclient;

import org.restlet.data.Method;

public interface ISerialAsyncRequest {

	public <V> ListenableFuture<V> get(Class<? extends V> clz) ;
	public <T, V> ListenableFuture<V> put(T arg, Class<? extends V> clz) ;
	public <T, V> ListenableFuture<V> post(T arg, Class<? extends V> clz) ;
	public <V> ListenableFuture<V> delete(Class<? extends V> clz) ;
	public ISerialAsyncRequest addHeader(String string, String string2);

	public <T, V> ListenableFuture<V> handle(Method method, T arg, Class<? extends V> resultClass);
	public RequestBuilder builder() ;

}
