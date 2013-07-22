package net.ion.bleujin;

import java.io.IOException;

import org.restlet.data.Method;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.ClientConfigBean;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;

public class TestHttpsCall extends TestCase{

	public void testCall() throws Exception {
		for (int i = 0; i < 3; i++) {
			NewClient client = NewClient.create();
			Response res = client.prepareGet("https://127.0.0.1:9000/hello").execute().get();
			
			assertEquals("hello", res.getTextBody()) ;
		}
	}
	

	public void testAradonClient() throws Exception {
		NewClient client = NewClient.create();
		for (int i = 0; i < 100; i++) {
			Response response = client.prepareGet("http://www.daum.net").execute().get();
			String body = response.getTextBody();
		}
		client.close() ;
	}

	
	public void testGaia() throws Exception {
		GaiaClient client = new GaiaClient("http://gaia.i-on.net");
		client.login();
		
		Integer result = client.prepareGet("/abc/def").param("key", "value").asceding("keyId").workspaceId("ddd").execute(new ResponseHandler<Integer>(){
			public Integer handle(Response response) throws IOException {
				String body = response.getTextBody();
				return body.length();
			}
		});
		
		client.shutdown() ;
	}
	
	
}
