package net.ion.radon.aclient.webdav;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.framework.util.Debug;
import net.ion.radon.aclient.Request;
import net.ion.radon.aclient.RequestBuilder;

import org.restlet.data.Method;
import org.restlet.data.Status;

public class TestWebDav extends TestBaseWebDav {
	
	public void testProfind() throws Exception {
		WebDavResponse response = callResponse(getRootUri(), Method.PROPFIND) ;
		
		assertEquals(207, response.getStatusCode()) ;
		Debug.line(response.getUTF8Body(), response.getStatusCode()) ;
	}

	public void testProfindNotExist() throws Exception {
		WebDavResponse response = callResponse("http://127.0.0.1:9000/webdav/notfound", Method.PROPFIND) ; 
		assertEquals(404, response.getStatusCode()) ;
		Debug.line(response.getUTF8Body(), response.getStatusCode()) ;
	}

	
	public void testMkDir() throws Exception {
		execute(new RequestBuilder(Method.MKCOL).setUrl("http://127.0.0.1:9000/webdav/newdir").build()) ;

		WebDavResponse response = execute(new RequestBuilder(Method.PROPFIND).setUrl(getRootUri())
			.addHeader("Depth", "1")
			.build()) ;
		
		assertEquals(207, response.getStatusCode()) ;
		Debug.line(response.getUTF8Body(), response.getStatusCode()) ;
	}
	
	
	public void testOptions() throws Exception {
		WebDavResponse response = callResponse(getRootUri(), Method.OPTIONS);
		Debug.line(response.getHeaders(), response.getStatusCode()) ;
		
	}
	public void testHead() throws Exception {
		WebDavResponse response = callResponse(getRootUri(), Method.HEAD);
		Debug.line(response.getHeaders(), response.getStatusCode()) ;
	}
	
	public void testPut() throws Exception {
		WebDavResponse response = execute(new RequestBuilder(Method.PUT).setUrl(getHelloUri())
				.setBody(new File("resource/hello.txt"))
				.build()) ;

		assertEquals(Status.SUCCESS_CREATED.getCode(), response.getStatusCode()) ;
	}
	
	
	public void testGet() throws Exception {
		testPut() ;

		WebDavResponse response = callResponse(getHelloUri(), Method.GET);
		assertTrue(response.getUTF8Body().startsWith("Hello")) ;
	}
	
	public void testDelete() throws Exception {
		testPut() ;

		WebDavResponse response = callResponse(getHelloUri(), Method.DELETE);
		assertEquals(204, response.getStatusCode()) ;
		
		assertEquals(404, callResponse(getHelloUri(), Method.GET).getStatusCode()) ;
	}


	private String getHelloUri() {
		return getRootUri() + "hello.txt";
	}
	
	public void testCopy() throws Exception {
		testPut() ;
		
		Request request = new RequestBuilder(Method.COPY).setUrl(getHelloUri())
			.addHeader("Destination", getHelloUri() + ".copy")
			.build() ;
		assertEquals(201, execute(request).getStatusCode()) ;
		assertEquals(201, execute(request).getStatusCode()) ; // overwrite
		
		WebDavResponse response = callResponse(getHelloUri() + ".copy", Method.GET);
		assertTrue(response.getUTF8Body().startsWith("Hello")) ;
	}
	

	
	
	private WebDavResponse execute(Request request) throws InterruptedException, ExecutionException, IOException {
		WebDavResponse response = newClient().prepareRequest(request).execute(new WebDavCompletionHandlerBase<WebDavResponse>() {
			public WebDavResponse onCompleted(WebDavResponse response) throws Exception {
				return response;
			}
		}).get() ;
		return response ;
	}

	private WebDavResponse callResponse(String uri, Method method) throws InterruptedException, ExecutionException, IOException {
		Request request = new RequestBuilder(method).setUrl(uri).build() ;
		return execute(request) ;
	}


	private String getRootUri() {
		return "http://127.0.0.1:9000/webdav/";
	}
	

		
//		final Request request = new Request(Method.PROPFIND, "riap://component/webdav/afield/Updates");
//		Form form = (Form) request.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		if (form == null) form = new Form() ;
//		form.add("Depth", "1") ;
//		
//		String text = "" + 
//		"<?xml version=\"1.0\" ?>\n" + 
//		"<D:propfind xmlns:D=\"DAV:\">\n" + 
//		"        <D:allprop/>\n" + 
//		"</D:propfind>\n" ;
//		
//		StringRepresentation re = new StringRepresentation(text) ;
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntityAsText()) ;

//	public void testOptions() throws Exception {
//		final Request request = new Request(Method.OPTIONS, "riap://component/webdav/afield");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//	}
//
//	public void testHead() throws Exception {
//		final Request request = new Request(Method.HEAD, "riap://component/webdav/afield/setup.exe");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//	}
//	
//	public void testMove() throws Exception {
//		final Request request = new Request(Method.GET, "riap://component/webdav/afield/setup.xml");
//	}
//
//	public void testGet() throws Exception {
//		final Request request = new Request(Method.GET, "riap://component/webdav/afield/setup.xml");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntityAsText()) ;
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//	}
//
//	public void testGetDir() throws Exception {
//		final Request request = new Request(Method.GET, "riap://component/webdav/afield/abcd/");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntityAsText()) ;
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//	}
//
//	public void testDelete() throws Exception {
//		final Request request = new Request(Method.DELETE, "riap://component/webdav/afield/1.test");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntityAsText()) ;
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//	}
//
//	public void testMkCreate() throws Exception {
//		final Request request = new Request(Method.MKCOL, "riap://component/webdav/afield/mydir/newdir");
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntityAsText()) ;
//		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
//		Debug.debug(headers) ;
//		assertEquals(Status.SUCCESS_CREATED.getCode(), response.getStatus().getCode()) ;
//	}
//
//	public void testPut() throws Exception {
//		final Request request = new Request(Method.PUT, "riap://component/webdav/afield/dd.xml");
//		request.setEntity(new FileRepresentation(new File("resource/bak/dd.xml"), MediaType.APPLICATION_XML)) ;
//		
//		Response response = handle("resource/config/plugin-system-vfs.xml", request);
//		Debug.debug(response.getEntity()) ;
//		Debug.line(response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS)) ;
//		
//	}
//	
//	public void testCalendar() throws Exception {
//		Debug.debug(DateUtil.toHTTPDateFormat(new Date())) ;
//		Debug.debug(DateUtil.dateToString(new Date(), "EEE, dd MMM yyyy HH:mm:ss zzz")) ;
//	}
}

