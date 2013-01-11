package net.ion.radon.aclient;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.aclient.ClientConfig.Builder;

import org.apache.http.HttpStatus;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class TestCreateRequest extends TestBaseClient{


	public void testSimple() throws Exception {
		Response response = newClient().prepareGet(getEchoUri()).addQueryParameter("name", "value").execute().get() ;
		Representation resEntity = new InputRepresentation(response.getBodyAsStream()) ;
		Form resForm = new Form(resEntity) ;
		
		assertEquals("value", resForm.getFirstValue("name")) ; 
	}
	
	public void testRequestBuilder() throws Exception {
		RequestBuilder builder = new RequestBuilder(Method.POST) ;
		builder.setBodyEncoding("UTF-8") ;
		Request request = builder.setUrl(getEchoUri())
			.addHeader("name", "value")
			.addParameter("p1", "한글1")
			.addParameter("p2", "한글2")
			.addParameter("p2", "한글2")
			.build() ;

		Debug.line(request.getBodyEncoding()) ;
		Response response = newClient().prepareRequest(request).execute().get() ;
		Representation resEntity = new InputRepresentation(response.getBodyAsStream()) ;
		Form resForm = new Form(resEntity) ;
		
		assertEquals("한글1", resForm.getFirstValue("p1")) ; 
		assertEquals("한글1", StringUtil.join(resForm.getValuesArray("p1"))) ; 
		assertEquals("한글2,한글2", StringUtil.join(resForm.getValuesArray("p2"), ",")) ;
		
		assertEquals("value", response.getHeader("name")) ;
	}
	
	public void testRequestConfig() throws Exception {
		Builder builder = new ClientConfig.Builder() ;
		builder.setMaxRequestRetry(3).setRequestTimeoutInMs(3 * 1000) ;
		
		Response res = newHttpClient(builder.build()).prepareGet(getEchoUri()).execute().get() ;
		assertEquals(HttpStatus.SC_NO_CONTENT, res.getStatusCode()) ;
	}
	
	
	
}
