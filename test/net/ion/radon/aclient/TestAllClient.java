package net.ion.radon.aclient;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.ion.radon.aclient.auth.TestAuthentication;
import net.ion.radon.aclient.filter.TestFilter;
import net.ion.radon.aclient.listener.TestTransfer;
import net.ion.radon.aclient.multipart.TestMultiPart;
import net.ion.radon.aclient.util.TestProxyUtils;
import net.ion.radon.aclient.util.TestUTF8UrlCodec;

public class TestAllClient extends TestCase {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TestConfigureClient.class) ;
		suite.addTestSuite(TestCreateResponse.class) ;

		
		suite.addTestSuite(TestMultiPart.class) ;
		suite.addTestSuite(TestTransfer.class) ;
		suite.addTestSuite(TestFilter.class) ;
		suite.addTestSuite(TestAuthentication.class) ;
		
		suite.addTestSuite(TestProxyUtils.class) ;
		suite.addTestSuite(TestUTF8UrlCodec.class) ;
		
		return suite;
	}
}
