package net.ion.radon.aclient;

import net.ion.nradon.helpers.TestUTF8Output;
import net.ion.radon.aclient.auth.TestAuthentication;
import net.ion.radon.aclient.filter.TestFilter;
import net.ion.radon.aclient.listener.TestTransfer;
import net.ion.radon.aclient.multipart.TestMultiPart;
import net.ion.radon.aclient.util.TestProxyUtils;
import net.ion.radon.aclient.util.TestUTF8UrlCodec;
import net.ion.radon.aclient.webdav.TestWebDav;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestAllClient extends TestCase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TestConfigureClient.class) ;
		suite.addTestSuite(TestCreateRequest.class) ;
		suite.addTestSuite(TestCreateResponse.class) ;

		
		suite.addTestSuite(TestMultiPart.class) ;
		suite.addTestSuite(TestTransfer.class) ;
		suite.addTestSuite(TestFilter.class) ;
		suite.addTestSuite(TestAuthentication.class) ;
		
		suite.addTestSuite(TestProxyUtils.class) ;
		suite.addTestSuite(TestUTF8UrlCodec.class) ;
		
		suite.addTestSuite(TestWebDav.class) ;
		
		return suite;
	}
}
