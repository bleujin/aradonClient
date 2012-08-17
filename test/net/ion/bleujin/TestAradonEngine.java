package net.ion.bleujin;


import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.TestEngine;
import net.ion.radon.core.config.ConnectorConfiguration;
import net.ion.radon.core.config.PathConfiguration;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.core.server.netty.HttpServerHelper;
import net.ion.radon.impl.let.HelloWorldLet;

import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Server;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class TestAradonEngine extends TestCase {
	
	
	public void testComponent() throws Exception {
		Component component = new Component();
	    component.getDefaultHost().attach("/trace", HelloLet.class);
	    
	    Debug.line(component.getServers()) ;
	    
	    Request request = new Request(Method.GET, "riap://component/trace") ;
    	Response response = component.handle(request) ;
    	Debug.line(response.getEntityAsText()) ;
	}
	
	
	
	public void registerServer() throws Exception {
		Aradon aradon = Aradon.create();
		aradon.getDefaultHost().attach("/trace", HiLet.class);

		Engine.getInstance().getRegisteredServers().clear();
		Server server = new Server(Protocol.HTTP, 9000, aradon);
		HttpServerHelper helper = new HttpServerHelper(server);
		Engine.getInstance().getRegisteredServers().add(helper);

		helper.start();

		for (int i = 0; i < 20; i++) {
			AradonClient ac = AradonClientFactory.create("http://127.0.0.1:9000");
			Debug.line(ac.createRequest("/test").get()) ;
			ac.stop() ;
			
		}

		aradon.stop() ;
		helper.stop();
	}
	
	
	public void testAradon() throws Exception {
		
		Aradon aradon = Aradon.create() ;
		aradon.attach(SectionConfiguration.createBlank("")).attach(PathConfiguration.create("test", "/test", HelloWorldLet.class)) ;
		aradon.startServer(ConnectorConfiguration.makeNettyHTTPConfig(9000)) ;
		
		for (int i = 0; i < 20; i++) {
			AradonClient ac = AradonClientFactory.create("http://127.0.0.1:9000");
			Debug.line(ac.createRequest("/test").get()) ;
			ac.stop() ;
			
		}
		aradon.destorySelf() ;
	}
	
	public void testLoop() throws Exception {
		for (int k = 0; k < 100; k++) {
			new TestAradonEngine().registerServer() ;
		}
	}
}


class HiLet extends ServerResource {
	
	@Get
	public String hi(){
		return "hello" ;
	}
}
