package net.ion.radon.aclient.websocket;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.ion.nradon.Radon;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.providers.netty.NettyProvider;

public class TestBaseWebsocket extends TestCase{


	private Radon server ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		server = RadonConfiguration.newBuilder(9000).add("/websocket/echo", new EchoWebSockets()).startRadon() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		server.stop() ;
		super.tearDown();
	}

	public NewClient newHttpClient() {
		return NewClient.create();
	}

	public NewClient newHttpClient(ClientConfig config) {
		return NewClient.create(new NettyProvider(config), config);
	}

	public String getEchoWebSocketUri(){
		return "ws://127.0.0.1:9000/websocket/echo" ;
	}
	
}



class EchoWebSockets implements WebSocketHandler {

	private List<WebSocketConnection> conns = new ArrayList<WebSocketConnection>() ;
	public void onOpen(WebSocketConnection connection) {
		conns.add(connection) ;
	}

	public void onClose(WebSocketConnection connection) {
		conns.remove(connection) ;
	}

	public void onMessage(WebSocketConnection connection, String message) {
		WebSocketConnection[] ds = conns.toArray(new WebSocketConnection[0]) ;
		for (WebSocketConnection conn : ds) {
			conn.send(message.toUpperCase()); // echo back message in upper
		}
	}

	public void onMessage(WebSocketConnection connection, byte[] message) {
	}

	public void onPong(WebSocketConnection connection, byte[] message) {
	}
	public void onPing(WebSocketConnection connection, byte[] message) {
		connection.pong(message) ;
	}

}