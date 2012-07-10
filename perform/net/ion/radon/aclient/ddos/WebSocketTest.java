package net.ion.radon.aclient.ddos;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.websocket.WebSocket;
import net.ion.radon.aclient.websocket.WebSocketListener;
import net.ion.radon.aclient.websocket.WebSocketTextListener;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class WebSocketTest extends AbstractJavaSamplerClient implements Serializable {

	private static final long serialVersionUID = -1127358016003610289L;
	private static final String TARGET_URI = "TargetURI";
	private static final String ECHO_MESSAGE_TEXT = "EchoMessage";
	private static final String DURATION_MS_IN_MESSAGE = "DurationMs";

	public static final String TARAGET_URI_DEFAULT = "ws://127.0.0.1:9000/echo";
	public static final String ECHO_MESSAGE_TEXT_DEFAULT = "Hello World";
	private static final long DURATION_DEFAULT = 10 * 1000 ;
	
	private WebSocket ws ;
	private final AtomicReference<String> text = new AtomicReference<String>("");
	public WebSocketTest() {
	}
 
	public void setupTest(JavaSamplerContext context) {
		this.ws = createWebSocketClient(context) ;
		Debug.debug("Create WebSocketClient : " + whoAmI());
	}

	private WebSocket createWebSocketClient(JavaSamplerContext context) {
		String targetURI = context.getParameter(TARGET_URI, TARAGET_URI_DEFAULT);
		try {
			return NewClient.create().createWebSocket(targetURI, new WebSocketTextListener(){
				public void onClose(WebSocket websocket) {
				}

				public void onError(Throwable t) {
				}

				public void onOpen(WebSocket websocket) {
				}

				public void onFragment(String fragment, boolean last) {
				}

				public void onMessage(String message) {
					text.set(message) ;
				}
			});
		} catch (Throwable e) {
			e.printStackTrace() ;
			throw new IllegalStateException(e) ;
		}
	}

	public Arguments getDefaultParameters() {
		Arguments params = new Arguments();
		params.addArgument(TARGET_URI, TARAGET_URI_DEFAULT);
		params.addArgument(ECHO_MESSAGE_TEXT, ECHO_MESSAGE_TEXT_DEFAULT);
		params.addArgument(DURATION_MS_IN_MESSAGE, String.valueOf(DURATION_DEFAULT));
		return params;
	}

	public SampleResult runTest(JavaSamplerContext context) {

		SampleResult results = new SampleResult();

		String messageText = context.getParameter(ECHO_MESSAGE_TEXT, ECHO_MESSAGE_TEXT_DEFAULT);

		results.setSamplerData(messageText);
		results.sampleStart();

		try {
			ws.sendTextMessage(messageText) ;

			results.setSuccessful(true);
			results.setResponseMessage(text.get());
			results.setResponseCode("200");

			results.setResponseData(messageText.getBytes());
			results.setDataType(SampleResult.TEXT);
		} catch (Exception e) {
			Debug.error("JavaTest: error during sample", e);
			results.setSuccessful(false);
		} finally {
			results.sampleEnd();
		}

		System.out.println(whoAmI() + "\tsendMessageTest()" + "\tTime:\t" + results.getTime());
		
		sleepDuration(context);
		return results;
	}

	private void sleepDuration(JavaSamplerContext context) {
		try {
			long durationSec = context.getLongParameter(DURATION_MS_IN_MESSAGE, DURATION_DEFAULT);
			Thread.sleep(durationSec) ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void teardownTest(JavaSamplerContext context) {
		Debug.debug("close WebSocekt : " + whoAmI());
		IOUtil.closeQuietly(ws) ;
	}

	private String whoAmI() {
		return Thread.currentThread().toString() + "@" + Integer.toHexString(hashCode());
	}
}
