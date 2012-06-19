package net.ion.radon.aclient;


public class AsyncCompletionHandlerBase extends AsyncCompletionHandler<Response> {
	@Override
	public Response onCompleted(Response response) throws Exception {
		return response;
	}

	public void onThrowable(Throwable ex) {
		ex.printStackTrace() ;
	}
}
