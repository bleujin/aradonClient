package net.ion.radon.client.async;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import net.ion.radon.client.AsyncHttpClient;
import net.ion.radon.client.AsyncHttpClientConfig;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;
import junit.framework.TestCase;

public class ClientBaseTest extends TestCase{
	private Aradon aradon ;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		aradon = AradonTester.create().register("", "/echo", EchoLet.class).getAradon() ;
		aradon.startServer(9000) ;
	}
	
	@Override
	public void tearDown() throws Exception {
		aradon.stop() ;
		super.tearDown();
	}
	
	protected AsyncHttpClient getAsyncHttpClient(AsyncHttpClientConfig config) {
		return new AsyncHttpClient(config);
	}

	
	protected String getTargetUrl() {
		return "http://127.0.0.1:9000/echo";
	}



}

class EchoLet extends AbstractServerResource {
	
	@Get
	public Representation echoGet(){
		return getInnerRequest().getEntity() ;
	}
	
	
	@Post
	public Representation echoPost(){
		return getInnerRequest().getEntity() ;
	}
	
}
