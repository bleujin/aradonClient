package net.ion.radon.aclient.ddos;

import java.io.Serializable;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.config.RadonConfigurationBuilder;
import net.ion.nradon.handler.aradon.AradonHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.ConnectorConfiguration;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;

import org.restlet.data.Method;
import org.restlet.resource.Post;

public class TestCommandRunner extends TestCase{


	public void testRunAradon() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/run", CommandRunLet.class).getAradon(); 
		aradon.startServer(ConnectorConfiguration.makeJettyHTTPConfig(9000)) ;
	
		Integer r = NewClient.create().createSerialRequest("http://127.0.0.1:9000/run").handle(Method.POST, new CountCommand(), Integer.class).get() ;
		
		Debug.line(r) ;
		aradon.stop() ;
	}
	
	
	public void xtestHello() throws Exception {
		
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon(); 
		aradon.startServer(ConnectorConfiguration.makeNettyHTTPConfig(9000)) ;
		
		new InfinityThread().startNJoin() ;
	}
	
	
	public void xtestNewAradon() throws Exception {
		RadonConfigurationBuilder config = RadonConfiguration.newBuilder(9000) ;
		
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon(); 
		config.add(AradonHandler.create(aradon)) ;
		
		config.startRadon() ;
		new InfinityThread().startNJoin() ;
	}
	
}


interface CommandRunner<T> extends Serializable {
	public T execute() ;
}

class CommandRunLet extends AbstractServerResource {
	
	@Post
	public <T> T postMethod(CommandRunner<T> runner){
		return runner.execute() ;
	}
}

class CountCommand implements CommandRunner<Integer> {
	public Integer execute() {
		for (int i = 0; i < 100; i++) {
			System.out.print('.') ;
		}
		return 100;
	}	
}

