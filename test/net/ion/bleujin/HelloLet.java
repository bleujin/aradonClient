package net.ion.bleujin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloLet{
	
	@GET
	public String hi(){
		return "hello" ;
	}
}
