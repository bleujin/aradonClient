package net.ion.radon.aclient.multipart;

import java.io.File;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.aclient.AsyncCompletionHandler;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.aclient.TestBaseClient;

import org.restlet.data.Method;

public class TestMultiPart extends TestBaseClient {

	public void testUpload() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl(getMultipartUri()).setMethod(Method.POST) ;
		builder.addBodyPart(new StringPart("name", "value"))
				.addBodyPart(new FilePart("myfile", new File("resource/hello.txt")));
		
		NewClient client = newClient() ;
		JsonObject json = client.prepareRequest(builder.build()).execute(new AsyncCompletionHandler<JsonObject>() {
			@Override
			public JsonObject onCompleted(Response response) throws Exception {
				return JsonObject.fromString(response.getUTF8Body());
			}
		}).get() ;
		
		assertEquals("hello.txt", json.asString("myfile")) ;
		assertEquals("value", json.asString("name")) ;
		
	}
}
