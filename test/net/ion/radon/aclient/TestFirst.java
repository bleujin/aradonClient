package net.ion.radon.aclient;

import java.io.File;
import java.io.FileOutputStream;

import org.restlet.representation.Representation;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.NewClient.BoundRequestBuilder;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;

public class TestFirst extends TestCase{

	
	public void testSync() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon() ;
		aradon.startServer(9000) ;
		
		NewClient client = NewClient.create() ;
		Response response = client.prepareGet("http://127.0.0.1:9000/hello").execute().get() ;
		
		assertEquals(200, response.getStatusCode() ) ;
		aradon.stop() ;
	}
	
	public void testAsync() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon() ;
		aradon.startServer(9000) ;
		
		NewClient client = NewClient.create() ;
		ListenableFuture<String> future = client.prepareGet("http://127.0.0.1:9000/hello").execute(new AsyncHandler<String>(){
			
			private StringBuilder builder = new StringBuilder() ;
			public net.ion.radon.aclient.AsyncHandler.STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
				builder.append(new String(bodyPart.getBodyPartBytes(), "UTF-8")) ;
				return STATE.CONTINUE;
			}

			public String onCompleted() throws Exception {
				return builder.toString();
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
				return STATE.CONTINUE;
			}

			public net.ion.radon.aclient.AsyncHandler.STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
				return STATE.CONTINUE;
			}

			public void onThrowable(Throwable t) {
				t.printStackTrace() ;
			}}) ;

		Debug.line(future.get()) ;
		aradon.stop() ;
	}
	
	public void xtestGoogle() throws Exception {
		NewClient client = NewClient.create();
		BoundRequestBuilder ir = client.prepareGet("https://chart.googleapis.com/chart");
		ir.addParameter("cht", "p3") ;
		ir.addParameter("chs", "500x250") ;
		String[] result = new String[]{"60","40"};
		ir.addParameter("chd", "s:" + StringUtil.join(result,"")) ;
		String label = "Hello|World";
		ir.addParameter("chl", label) ;
		
		Response repr =  ir.execute().get() ;
		File file = File.createTempFile("ddddd", "ccdddd") ;
		IOUtil.copyNClose(repr.getBodyAsStream(), new FileOutputStream(file)) ;
		Debug.line(file.getCanonicalPath()) ;
	}
	
}
