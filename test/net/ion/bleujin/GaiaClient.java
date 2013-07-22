package net.ion.bleujin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.AsyncHandler;
import net.ion.radon.aclient.HttpResponseBodyPart;
import net.ion.radon.aclient.HttpResponseHeaders;
import net.ion.radon.aclient.HttpResponseStatus;
import net.ion.radon.aclient.ListenableFuture;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.NewClient.BoundRequestBuilder;

public class GaiaClient {

	private String homePath;
	public GaiaClient(String homePath) {
		this.homePath = homePath;
	}

	private NewClient hclient = NewClient.create() ;

	public void login() {
	}

	public void shutdown() {
		hclient.close() ;
	}

	public GaiaRequest prepareGet(String url) {
		return new GaiaRequest(this, hclient.prepareGet(homePath + url));
	}

	<T> T execute(Request request, final ResponseHandler<T> defineHandler) throws IOException, InterruptedException, ExecutionException {
		ListenableFuture<T> result = hclient.prepareRequest(request).execute(new AsyncCompletionHandler<T>(){
			@Override
			public T onCompleted(Response response) throws Exception {
				try {
					final T result = defineHandler.handle(response);
					return result;
				} finally {
					IOUtil.closeQuietly(response.getBodyAsStream()) ;
				}
			}
		});
		return result.get() ;
	}
}
