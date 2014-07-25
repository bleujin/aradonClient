package net.ion.radon.aclient.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.config.RadonConfiguration;
import net.ion.radon.aclient.NewClient;
import net.ion.radon.aclient.RequestBuilder;
import net.ion.radon.aclient.Response;
import net.ion.radon.core.let.PathHandler;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

public class TestMultiPart extends TestCase {

	private Radon radon;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.radon = RadonConfiguration.newBuilder(9000).add(new PathHandler(UploadFileService.class)).startRadon();
	}
	
	@Override
	protected void tearDown() throws Exception {
		radon.stop().get() ;
		super.tearDown();
	}
	
	public void testUpload() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9000/file/upload").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value")).addBodyPart(new FilePart("myfile", new File("resource/hello.txt")));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get() ;
		Debug.line(response.getTextBody(), response.getStatus());
//
//		assertEquals("hello.txt", json.asString("myfile"));
//		assertEquals("value", json.asString("name"));
	}

	public void testUpload2() throws Exception {
		RequestBuilder builder = new RequestBuilder().setUrl("http://localhost:9000/file/upload2").setMethod(HttpMethod.POST);
		builder.addBodyPart(new StringPart("name", "value")).addBodyPart(new FilePart("myfile", new File("resource/hello.txt")));

		NewClient client = NewClient.create();
		Response response = client.prepareRequest(builder.build()).execute().get() ;
		Debug.line(response.getTextBody(), response.getStatus());
//
//		assertEquals("hello.txt", json.asString("myfile"));
//		assertEquals("value", json.asString("name"));
	}

}

@Path("/file")
class UploadFileService {

	@POST
	@Path("/upload")
	@Consumes("multipart/form-data")
	public String uploadFile(@MultipartForm FileUploadForm uform) throws IOException {
		Debug.line(uform.name(), IOUtil.toStringWithClose(uform.myFile()));
		return uform.name();
	}

	@POST
	@Path("/upload2")
	@Consumes("multipart/form-data")
	public String uploadFile2(MultipartInput input) throws IOException {
		List<InputPart> parts = input.getParts() ;
		for(InputPart part : parts){
			if (part.getMediaType().isCompatible(MediaType.TEXT_PLAIN_TYPE)){
				Debug.line(part.getBodyAsString());
			} else if (part.getMediaType().isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)){
				MultivaluedMap<String, String> headers = part.getHeaders();
				for(String key : headers.keySet()){
					Debug.line(key, headers.get(key));
				}
				Debug.line(IOUtil.toStringWithClose(part.getBody(InputStream.class, null)));
			}
		}
		return "";
	}


}

class FileUploadForm implements Serializable{

	private static final long serialVersionUID = -902230526836514914L;
	@FormParam("name")
	private String name ;
	@FormParam("myfile")
    private InputStream input;
	

	public String name(){
		return name ;
	}
	
    public InputStream myFile() {
        return input;
    }

}