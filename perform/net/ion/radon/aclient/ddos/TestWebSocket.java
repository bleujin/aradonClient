package net.ion.radon.aclient.ddos;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.ListUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.WebSocketHandler;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.nradon.config.RadonConfigurationBuilder;
import net.ion.nradon.handler.aradon.AradonHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketListener;
import net.ion.radon.core.Aradon;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;

public class TestWebSocket extends TestCase {

	public void xtestNewAradon() throws Exception {
		RadonConfigurationBuilder config = RadonConfiguration.newBuilder(9000) ;
		
		Aradon aradon = AradonTester.create().register("", "/hello", HelloWorldLet.class).getAradon(); 
		config.add("/echo", new WebSocketHandler() {
			
			public void onPong(WebSocketConnection websocketconnection, String s) throws Throwable {
				
			}
			
			public void onOpen(WebSocketConnection websocketconnection) throws Exception {
				Debug.line(websocketconnection) ;
			}
			
			public void onMessage(WebSocketConnection websocketconnection, byte[] abyte0) throws Throwable {
				
			}
			
			public void onMessage(WebSocketConnection websocketconnection, String s) throws Throwable {
				websocketconnection.send(s) ;
			}
			
			public void onClose(WebSocketConnection websocketconnection) throws Exception {
				
			}
		}) ;
		config.add(AradonHandler.create(aradon)) ;
		
		config.startRadon() ;
		new InfinityThread().startNJoin() ;
	}
	
	
	public void testConnect() throws Exception {
		
		WebSocketListener webSocketListener = new WebSocketListener() {
			public void onOpen(WebSocket websocket) {
			}
			public void onError(Throwable t) {
			}
			public void onClose(WebSocket websocket) {
			}
		};
		for (int i : ListUtil.rangeNum(100)) {
			NewClient nc = NewClient.create();
			WebSocket ws = nc.createWebSocket("ws://127.0.0.1:9000/echo", webSocketListener) ;
			ws.sendTextMessage("Hello") ;
			ws.close() ;
			nc.close() ;
		}
	}
	
	
	
}
