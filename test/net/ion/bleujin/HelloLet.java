package net.ion.bleujin;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class HelloLet extends ServerResource{
	
	@Get
	public String hi(){
		return "hello" ;
	}
}
