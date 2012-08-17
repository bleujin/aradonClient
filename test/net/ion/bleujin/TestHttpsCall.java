package net.ion.bleujin;

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
}
