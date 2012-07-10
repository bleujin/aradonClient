package net.ion.radon.aclient.websocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;

public class TestWebSocketMessage extends TestBaseWebsocket {

	public void testOpen() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<String> text = new AtomicReference<String>("");
		
		WebSocket ws = c.createWebSocket(getEchoWebSocketUri(), new WebSocketListener() {
			public void onOpen(WebSocket websocket) {
				text.set("OnOpen");
				latch.countDown();
			}

			public void onClose(WebSocket websocket) {
			}

			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		}) ;
		
		latch.await();
		ws.sendTextMessage("Hello") ;
		
		assertEquals(text.get(), "OnOpen");
	}

	public void testEmptyListenerTest() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());

		WebSocket websocket = null;
		try {
			websocket = c.prepareGet(getEchoWebSocketUri()).execute(new WebSocketUpgradeHandler.Builder().build()).get();
		} catch (Throwable t) {
			fail();
		}
		assertTrue(websocket != null);
	}

	public void testFailureTest() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		// final AtomicReference<String> text = new AtomicReference<String>("");

		Throwable t = null;
		try {
			WebSocket websocket = c.prepareGet("ws://abcdefg").execute(new WebSocketUpgradeHandler.Builder().build()).get();
		} catch (Throwable t2) {
			t = t2;
		}
		assertTrue(t != null);
	}

	public void testOnClose() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<String> text = new AtomicReference<String>("");

		WebSocket websocket = c.createWebSocket(getEchoWebSocketUri(), new WebSocketListener() {
			public void onOpen(WebSocket websocket) {
			}
			public void onClose(WebSocket websocket) {
				text.set("OnClose");
				latch.countDown();
			}
			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		}) ;

		websocket.close();

		latch.await();
		assertEquals(text.get(), "OnClose");
	}

	public void testEchoText() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<String> text = new AtomicReference<String>("");

		WebSocket websocket = c.createWebSocket(getEchoWebSocketUri(), new WebSocketTextListener() {
			public void onMessage(String message) {
				text.set(message);
				latch.countDown();
			}
			public void onFragment(String fragment, boolean last) {
			}
			public void onOpen(WebSocket websocket) {
			}
			public void onClose(WebSocket websocket) {
				latch.countDown();
			}
			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		}) ;

		websocket.sendTextMessage("ECHO");

		latch.await();
		assertEquals(text.get(), "ECHO");
	}

	public void testEchoDoubleListenerText() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(2);
		final AtomicReference<String> text = new AtomicReference<String>("");

		WebSocket websocket = c.createWebSocket(getEchoWebSocketUri(), new WebSocketTextListener() {
			public void onMessage(String message) {
				text.set(message);
				latch.countDown();
			}

			public void onFragment(String fragment, boolean last) {
			}
			public void onOpen(WebSocket websocket) {
			}
			public void onClose(WebSocket websocket) {
				latch.countDown();
			}
			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		}, new WebSocketTextListener() {

			public void onMessage(String message) {
				text.set(text.get() + message);
				latch.countDown();
			}
			public void onFragment(String fragment, boolean last) {
			}
			public void onOpen(WebSocket websocket) {
			}
			public void onClose(WebSocket websocket) {
				latch.countDown();
			}
			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		});

		websocket.sendTextMessage("ECHO");

		latch.await();
		assertEquals(text.get(), "ECHOECHO");
	}

	public void testEchoTwoMessagesTest() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(2);
		final AtomicReference<String> text = new AtomicReference<String>("");

		c.createWebSocket(getEchoWebSocketUri(), new WebSocketTextListener() {

			public void onMessage(String message) {
				text.set(text.get() + message);
				latch.countDown();
			}

			public void onFragment(String fragment, boolean last) {
			}

			public void onOpen(WebSocket websocket) {
				websocket.sendTextMessage("ECHO").sendTextMessage("ECHO");
			}

			public void onClose(WebSocket websocket) {
				latch.countDown();
			}

			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		});

		latch.await();
		assertEquals(text.get(), "ECHOECHO");
	}


	public void xtestTimeoutCloseTest() throws Throwable {
		NewClient c = newHttpClient(new ClientConfig.Builder().build());
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<String> text = new AtomicReference<String>("");

		c.createWebSocket(getEchoWebSocketUri(), new WebSocketListener() {
			public void onOpen(WebSocket websocket) {
			}
			public void onClose(WebSocket websocket) {
				text.set("OnClose");
				latch.countDown();
			}
			public void onError(Throwable t) {
				t.printStackTrace();
				latch.countDown();
			}
		}) ;

		latch.await();
		assertEquals(text.get(), "OnClose");
	}


}
