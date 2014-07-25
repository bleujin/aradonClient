package net.ion.radon.aclient;

import junit.framework.TestCase;

public class TestBaseClient extends TestCase {

	
	public NewClient newClient(){
		return NewClient.create() ;
	}
	
	public String getHelloUri(){
		return "" ;
	}

	public String getUploadUri(){
		return "" ;
	}

}
