package net.ion.radon.aclient;

import junit.framework.TestCase;
import net.ion.radon.aclient.ClientConfig.Builder;

public class TestConfigureClient extends TestCase{

	public void testConfig() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.setCompressionEnabled(true)  // Compression
			.setRequestTimeoutInMs(3000)  // Timeout
			.setFollowRedirects(true) ;
		
		NewClient client = NewClient.create(builder.build()) ;
	}
	
	public void testLimitConnection() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.setAllowPoolingConnection(true)  
			.setMaximumConnectionsPerHost(3)
			.setMaximumConnectionsTotal(10)
			.setIdleConnectionInPoolTimeoutInMs(3000) ;
		
		// There is no magic number, so you will need to try it and decide which one gives the best result
	}
}
