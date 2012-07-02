package net.ion.radon.aclient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.providers.netty.NettyProvider;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.ILocation;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.core.let.MultiValueMap;
import net.ion.radon.core.security.ChallengeAuthenticator;
import net.ion.radon.core.security.SimpleVerifier;
import net.ion.radon.util.AradonTester;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.restlet.data.Form;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.util.Series;

public class TestBaseClient extends TestCase{

	protected Aradon aradon ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aradon = AradonTester.create()
			.register("", "/", HelloLet.class)
			.register("", "/hello", HelloLet.class)
			.register("", "/echo", EchoLet.class)
			.register("", "/upload", UploadLet.class)
			.register("", "/multipart", MultipartLet.class)
			.register("", "/slow,/slow/{time}", SlowLet.class)
			.mergeSection("secure")
			.addFilter(ILocation.PRE, new ChallengeAuthenticator("secure", new SimpleVerifier()))
			.addLet("/hello", "shello", HelloLet.class).getAradonTester()
			.mergeSection("twitter")
			.addLet("/oauth", "twitter oauth", OAuthLet.class)
			.getAradon() ;
		aradon.startServer(9000) ;
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		aradon.stop() ;
		super.tearDown();
	}


	public String getHelloUri(){
		return "http://127.0.0.1:9000/hello";
	}

	public String getSecureHelloUri(){
		return "http://127.0.0.1:9000/secure/hello";
	}

	public String getEchoUri(){
		return "http://127.0.0.1:9000/echo";
	}

	public String getSlowUri(){
		return "http://127.0.0.1:9000/slow";
	}

	public String getUploadUri(){
		return "http://127.0.0.1:9000/upload";
	}

	public String getMultipartUri(){
		return "http://127.0.0.1:9000/multipart";
	}

	

	public NewClient newClient() {
		return NewClient.create();
	}

	public NewClient newHttpClient(ClientConfig config) {
		return NewClient.create(new NettyProvider(config), config);
	}
}

class MultipartLet extends AbstractServerResource {

	@Post
	public String mulpartUpload() throws IOException{
		MultiValueMap params = getInnerRequest().getFormParameter();
		
		JsonObject json = new JsonObject() ;
		for (Entry<String, Object> entry : params.entrySet()) {
			if (entry.getValue() instanceof FileItem){
				FileItem fitem = (FileItem)entry.getValue();
				InputStream input = fitem.getInputStream() ;
				IOUtil.copyNClose(input, new ByteArrayOutputStream()) ;
				json.put(entry.getKey(), fitem.getName()) ;
			} else {
				json.put(entry.getKey(), ObjectUtil.toString(entry.getValue())) ;
			}
		}
		
		return json.toString() ;
	}
}


class OAuthLet extends AbstractServerResource {
	
	@Get
	public String myGet(){
		return print() ;
	}

	@Post
	public String myPost(){
		return print() ;
	}

	private String print() {
		Debug.line(getInnerRequest().getFormParameter()) ;
		return "getcha";
	}
	
}

class UploadLet extends AbstractServerResource {
	
	@Put
	public String printByte() throws IOException{
		Representation reqEntity = getInnerRequest().getEntity() ;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtil.copyNClose(reqEntity.getStream(), output) ;
		return "" + output.size() ;
	}
}

class SlowLet extends AbstractServerResource {
	@Get
	public String slow() throws InterruptedException{
		int waitMs = Integer.parseInt(StringUtil.defaultIfEmpty(getInnerRequest().getAttribute("time"), "300")) ;
		Thread.sleep(waitMs) ;
		return "hello" ;
	} 
}

class HelloLet extends AbstractServerResource {
	
	@Get
	public String hello(){
		return "hello" ;
	}
}

class EchoLet extends AbstractServerResource {
	
	@Post
	public Representation whenPost(){
		return echoRepresentation();
	}

	@Get
	public Representation whenGet(){
		return echoRepresentation();
	}

	@Delete
	public Representation whenDelete(){
		return echoRepresentation();
	}


	@Put
	public Representation whenPut(){
		return echoRepresentation();
	}

	private Representation echoRepresentation() {
		MultiValueMap params = getInnerRequest().getFormParameter();
		Form form = new Form() ;
		Set<String> keys = params.keySet();
		for (String key : keys) {
			List values = params.getAsList(key) ;
			for (Object value : values) {
				form.add(key, ObjectUtil.toString(value)) ;
			}
		}
		
		
		Series<Header> reqHeaders = getInnerRequest().getHeaders() ;
		Series<Header> resHeaders = getInnerResponse().getHeaders() ;
		for (Header header : reqHeaders) {
			resHeaders.add(header) ;
		}
		
		return form.getWebRepresentation() ;
	}
	
	
	
}