package net.ion.radon.aclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;

public class TestTransform extends TestCase{

	public void testExtract() throws Exception {
		String str = "/** abcd\nefg */abc/** \n *dd\n*/ def" ;
		String pattern = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)";
		String trans = str.replaceAll(pattern, "") ;
		
		File file = new File("src/net/ion/radon/aclient/FluentStringsMap.java") ;
		String readString = IOUtil.toString(new FileInputStream(file)) ;
		
		Debug.line(trans) ;
//		IOUtil.write(readString.replaceAll(pattern, ""), new FileOutputStream(file)) ;
		
	}
}
