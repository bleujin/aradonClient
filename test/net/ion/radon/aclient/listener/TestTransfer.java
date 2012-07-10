package net.ion.radon.aclient.listener;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.aclient.FluentCaseInsensitiveStringsMap;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.TestBaseClient;

public class TestTransfer extends TestBaseClient{

	public void testUpload() throws Exception {
		NewClient client = newClient() ;
		File file = new File("resource/hello.txt") ;
		
		Future<Response> f = client.preparePut(getUploadUri()).setBody(file).execute() ;
		assertEquals(file.length(), Long.valueOf(f.get().getTextBody()).longValue() ) ;
	}
	
	
	public void testTransferOrder() throws Exception {
		NewClient client = newClient() ;
		TransferCompletionHandler handler = new TransferCompletionHandler() ;
		File file = new File("resource/hello.txt") ;

		final List<String> orders = ListUtil.newList() ;
		handler.addTransferListener(new TransferListener() {
			public void onThrowable(Throwable t) {
				Debug.line(t) ;
			}
			
			public void onResponseHeadersReceived(FluentCaseInsensitiveStringsMap headers) {
				orders.add("onResponseHeadersReceived") ;
			}
			
			public void onRequestResponseCompleted() {
				orders.add("onRequestResponseCompleted") ;
			}
			
			public void onRequestHeadersSent(FluentCaseInsensitiveStringsMap headers) {
				orders.add("onRequestHeadersSend") ;
			}
			
			public void onBytesSent(ByteBuffer buffer) {
				orders.add("onBytesSend") ;
			}
			
			public void onBytesReceived(ByteBuffer buffer) throws IOException {
				orders.add("onBytesReceived") ;
			}
		}) ;
		
		Response res = client.preparePut(getUploadUri()).setBody(file).execute(handler).get() ;
		res.getTextBody() ;
		
		assertEquals(true, Arrays.equals(new String[]{"onRequestHeadersSend", "onResponseHeadersReceived", "onBytesReceived", "onBytesSend", "onRequestResponseCompleted"}, orders.toArray(new String[0]))) ;
	}
	
	
}
