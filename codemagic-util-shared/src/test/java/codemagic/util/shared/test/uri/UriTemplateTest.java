package codemagic.util.shared.test.uri;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import codemagic.util.shared.common.uri.UriTemplate;

public class UriTemplateTest {

	private static final String SERVICE_PATH_PREFIX = "/svc";
	
	@Test
	public void testNull() {
		final String input = null; 
		final String formatExpected = SERVICE_PATH_PREFIX;
		runTest(input, formatExpected);
	}
	
	@Test
	public void test0() {
		final String input = "/{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s?%s";
		runTest(input, formatExpected);
	}
	
	@Test
	public void test1() {
		final String input = "/%s/{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s/%s?%s";
		runTest(input, formatExpected);
	}
	
	/**
	 * Preserve spaces within the uri 
	 */
	@Test
	public void test2() {
		final String input = "/{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s?%s";
		runTest(input, formatExpected);
	}
	
	@Test
	public void test3() {
		final String input = "/{variable1}/{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s/%s?%s";
		runTest(input, formatExpected);
	}
	
	
	@Test
	public void test4() {
		final String input = "/{variable1}{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s%s?%s";
		runTest(input, formatExpected);
	}

	@Test
	public void test5() {
		final String input = "/%s/{variable1}?{variable2}"; 
		final String formatExpected = SERVICE_PATH_PREFIX + "/%s/%s?%s";
		runTest(input, formatExpected);
	}
	
	  //~~~~~~~// 
	 // Core  //
	//~~~~~~~//
	
	protected void runTest(final String input, final String formatExpected) {
		final UriTemplate uriTemplate = new UriTemplate( input, SERVICE_PATH_PREFIX );
		assertThat(uriTemplate.buildServiceUriTemplateSpringStyle(""), is( SERVICE_PATH_PREFIX + sanitize(input).trim() ));
		assertThat(uriTemplate.buildServiceUriTemplateFormatStyle(""), is( formatExpected ));
	}

}
