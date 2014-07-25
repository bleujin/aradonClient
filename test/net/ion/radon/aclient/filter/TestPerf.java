package net.ion.radon.aclient.filter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.ClientConfig.Builder;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.let.PathHandler;

public class TestPerf extends TestCase {

	private Radon radon;
	protected void setUp() throws Exception {
		this.radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(TestLet.class)).start().get() ;
	}
	
	protected void tearDown() throws Exception {
		radon.stop().get() ;
		super.tearDown();
	}

	
	public void testManyRequest() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.addRequestFilter(new ThrottleFilter(200)) ;
		
		NewClient c = NewClient.create(builder.build()) ;
		for (int i = 0; i < 2000; i++) {
			c.prepareGet("http://localhost:9000/hello").execute() ;
		}
		c.close();
	}
}


@Path("/hello")
class TestLet {
	
	@GET
	public String hello(){
		return "hello" ;
	}
}