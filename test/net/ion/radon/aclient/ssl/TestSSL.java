package net.ion.radon.aclient.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.restlet.data.Method;
import org.restlet.resource.Get;

import net.ion.framework.util.Debug;
import net.ion.radon.aclient.ClientConfig;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.ClientConfig.Builder;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.ConnectorConfig;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.util.AradonTester;
import junit.framework.TestCase;

public class TestSSL extends TestCase {

	private Aradon aradon;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aradon = AradonTester.create().register("", "/", HelloLet.class).getAradon();
		String configStr = "<connector-config port='9000' protocol='https' engine='jetty'>"
				+ "<parameter name='keystorePath' description=''>./resource/keystore/keystore</parameter>\n"
				+ "<parameter name='keystorePassword' description=''>password</parameter>\n"
				+ "<parameter name='keystoreType' description=''>JKS</parameter>\n"
				+ "<parameter name='keyPassword' description=''>password</parameter>\n"
				+ "</connector-config>";

		XMLConfig config = XMLConfig.load(configStr);

		AradonTester at = AradonTester.create().register("", "/hello", HelloLet.class);
		at.getAradon().startServer(ConnectorConfig.create(config, 9000));
	}

	@Override
	protected void tearDown() throws Exception {
		aradon.stop();
		super.tearDown();
	}

	public void testBlankConnect() throws Exception {
		Request req = new RequestBuilder(Method.GET).setUrl("https://127.0.0.1:9000/hello").build();
		NewClient client = NewClient.create();
		Response res = client.executeRequest(req).get();

		client.close();
		assertEquals("Hello, World", res.getTextBody());
	}

	public void testConfigSSL() throws Exception {

		InputStream keyStoreStream = new FileInputStream(new File("resource/keystore/keystore.jks"));
		char[] keyStorePassword = "changeit".toCharArray();

		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(keyStoreStream, keyStorePassword);

		char[] certiPassword = "changeit".toCharArray();

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, certiPassword);

		KeyManager[] km = kmf.getKeyManagers();

		TrustManager[] DUMMY_TRUST_MANAGER = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
				Debug.line(certs, authType) ;
			}
		} };

		SecureRandom random = new SecureRandom();
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(km, DUMMY_TRUST_MANAGER, random);
		Builder builder = ClientConfig.newBuilder().setSSLContext(sslContext);

		Request req = new RequestBuilder(Method.GET).setUrl("https://127.0.0.1:9000/hello").build();
		NewClient client = NewClient.create(builder.build());
		Response res = client.executeRequest(req).get();

		client.close();
		assertEquals("Hello, World", res.getTextBody());
	}

}

class HelloLet extends AbstractServerResource {

	@Get
	public String hello() {
		return "Hello, World";
	}

}
