package net.ion.radon.aclient.filter;

import org.restlet.resource.Get;

import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.TestBaseClient;
import net.ion.radon.aclient.ClientConfig.Builder;
import net.ion.radon.core.EnumClass.ILocation;
import net.ion.radon.core.config.ConnectorConfig;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.core.let.MultipartLet;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.radon.core.security.SimpleVerifier;
import net.ion.radon.util.AradonTester;

public class TestPerf extends TestBaseClient {

	@Override
	protected void setUp() throws Exception {
		aradon = AradonTester.create()
			.register("", "/hello", TestLet.class)
			.getAradon() ;
		aradon.startServer(ConnectorConfig.makeSimpleHTTPConfig(9000)) ;
		
	}

	
	public void testManyRequest() throws Exception {
		Thread.sleep(1000) ;
		Builder builder = new ClientConfig.Builder() ;
		builder.addRequestFilter(new ThrottleFilter(200)) ;
		
		NewClient c = NewClient.create(builder.build()) ;
		
		for (int i = 0; i < 2000; i++) {
			c.prepareGet(getHelloUri()).execute() ;
		}
		c.close();
	}
}


class TestLet extends AbstractServerResource {
	
	@Get
	public String hello(){
		return "hello" ;
	}
}