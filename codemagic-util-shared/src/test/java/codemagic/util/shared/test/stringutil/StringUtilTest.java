package codemagic.util.shared.test.stringutil;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import codemagic.util.shared.common.StringUtil;

public class StringUtilTest {
	@Test
	public void testNormalize() {
		// fail("Not yet implemented");
	}

	@Test
	public void testRemoveAccents2() {
		 assertThat( StringUtil.removeAccents("ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÑÒÓÔÕÖÙÚÛÜÝ"), is(equalTo("AAAAAACEEEEIIIINOOOOOUUUUY")));   
	     assertThat( StringUtil.removeAccents("àáâãäåçèéêëìíîïñòóôõöùúûüýÿ"), is(equalTo("aaaaaaceeeeiiiinooooouuuuyy")));
	}
	
	@Test
	public void testRemovePunctSingleChars() {
		assertThat(StringUtil.onlyLowerLetterNumbersAndUnderline("!&{}[]+-=\\/@#%*)(-|<>,;:?") , is(""));
	}
	
	/**
	 * Only lower letter, digits (0 up to 9), dot and underline are allowed.
	 */
	@Test
	public void testRemovePunctWithinInWord() {
		assertThat(StringUtil.onlyLowerLetterNumbersAndUnderline("a.b}c{A") , is("a.bc"));
	}
	
	@Test
	public void testReplaceSpace() {
		assertThat(StringUtil.replaceSpace("a b c") , is("a_b_c"));
	}

}
