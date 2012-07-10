package net.ion.radon.aclient.perf;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.config.ConnectorConfig;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.util.AradonTester;

import org.restlet.resource.Get;

public class TestManyRequest extends TestCase {

	public void testManyRequest() throws Exception {
		AradonTester at = AradonTester.create().register("", "/hello/{num}", DummyLet.class);
		at.getAradon().startServer(ConnectorConfig.makeJettyHTTPConfig(9005));

		// AradonClient client = AradonClientFactory.create("http://61.250.201.157:9005");
		NewClient client = NewClient.create() ;

		for (int i : ListUtil.rangeNum(50000)) {
			// IAradonRequest request = client.createRequest("/hello/" + i);
			//  Response res = request.handle(Method.GET);
			Response res = client.prepareGet("http://1270.0.0.1:9005/hello/" + i).execute().get() ;
			// assertEquals(200, res.getStatus().getCode()) ;

			if (res.getStatusCode()  != 200) {
				Debug.line(res.getTextBody());
			}

			if ((i % 100) == 0) {
				System.out.print('.');
			}
		}
		client.close() ;
		at.getAradon().stop();
	}
}

class DummyLet extends AbstractServerResource {

	@Get
	public String hello() {
		return "hello " + getInnerRequest().getAttribute("num");
	}
}