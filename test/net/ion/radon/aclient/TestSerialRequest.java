package net.ion.radon.aclient;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.ion.framework.db.bean.test.Emp;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.radon.client.ISerialRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.util.AradonTester;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.restlet.data.Method;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import junit.framework.TestCase;

public class TestSerialRequest extends TestCase {


	private Aradon aradon ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aradon = AradonTester.create().register("", "/serial", SerialLet.class).getAradon() ;
		aradon.startServer(9000) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		aradon.stop() ;
		super.tearDown();
	}
	
	public void testCreateRequest() throws Exception {
		NewClient nc = NewClient.create() ;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(bout) ; 
		output.writeObject(Employee.create()) ;
		output.close() ;
		byte[] data = bout.toByteArray() ;
		Request req = nc.requestBuilder(Method.POST, "http://localhost:9000/serial")
			.setBody(data)
			.addHeader("Content-Type", "application/x-java-serialized-object")
			.build() ;
		
		Response res = nc.prepareRequest(req).execute().get() ;
		
		assertEquals(200, res.getStatusCode()) ;
		byte[] bodyBuf = IOUtil.toByteArray(res.getBodyAsStream()) ;
		ObjectInputStream oinput = new ObjectInputStream(new ByteArrayInputStream(bodyBuf)) ;
		Employee emp = Employee.class.cast(oinput.readObject()) ;
		
		assertEquals(21, emp.getAge()) ;
	}
	
	public void testSerialRequest() throws Exception {
		NewClient nc = NewClient.create() ;
		
		ISerialAsyncRequest request = nc.createSerialRequest("http://localhost:9000/serial") ;
		Employee emp = request.post(Employee.create(), Employee.class).get() ;
		
		assertEquals(21, emp.getAge()) ;
	}
	
	public void testGet() throws Exception {
		NewClient nc = NewClient.create() ;
		
		ISerialAsyncRequest request = nc.createSerialRequest("http://localhost:9000/serial?name=bleujin") ;
		Employee emp = request.get(Employee.class).get() ;
		
		assertEquals(20, emp.getAge()) ;
	}
	
}

class SerialLet extends AbstractServerResource {
	
	@Post
	public Employee readEmp(Employee emp){
		return emp.addAge() ;
	}
	
	
	@Get
	public Employee getEmp(){
		return Employee.create() ;
	}
}

class Employee implements Serializable {
	private static final long serialVersionUID = 2217465030602933108L;
	private String name ;
	private int age ;
	
	private Employee(String name, int age){
		this.name = name ;
		this.age = age ;
	}
	
	public Employee addAge() {
		age++ ;
		return this;
	}

	public static Employee create(){
		return new Employee("bleujin", 20) ;
	} 
	
	public int getAge(){
		return age ;
	}
}
