package net.ion.radon.aclient.util;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;


public class TestUTF8UrlCodec extends TestCase{

	public void testBasics() {
		assertEquals(UTF8UrlEncoder.encode("foobar"), "foobar");
		assertEquals(UTF8UrlEncoder.encode("a&b"), "a%26b");
		assertEquals(UTF8UrlEncoder.encode("a+b"), "a%2Bb");
	}
	
	public void testHangul() throws Exception {
		Debug.line("f%3D%ED%95%9C%EA%B8%80", UTF8UrlEncoder.encode("f=ÇÑ±Û"));
		
	}
}
