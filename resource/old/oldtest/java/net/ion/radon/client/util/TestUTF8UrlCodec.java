/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package net.ion.radon.client.util;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.client.util.UTF8UrlEncoder;

import org.testng.Assert;
import org.testng.annotations.Test;

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
