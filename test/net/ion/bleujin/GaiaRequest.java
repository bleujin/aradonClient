package net.ion.bleujin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.NewClient.BoundRequestBuilder;

public class GaiaRequest {

	private BoundRequestBuilder builder;
	private GaiaClient gclient;

	public GaiaRequest(GaiaClient gclient, BoundRequestBuilder builder) {
		this.gclient = gclient ;
		this.builder = builder ;
	}

	public <T> T execute(ResponseHandler<T> defineHandler) throws IOException, InterruptedException, ExecutionException {
		return gclient.execute(builder.build(), defineHandler);
	}

	public GaiaRequest param(String key, String value) {
		builder.addParameter(key, value);
		return this ;
	}

	public GaiaRequest workspaceId(String value) {
		return param("workspaceId", value);
	}

	public GaiaRequest asceding(String key) {
		return param("order", key);
	}

}
