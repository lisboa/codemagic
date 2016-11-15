package codemagic.util.shared.test.stringutil;

import org.junit.Ignore;

/**
 * 
 * Utility to build the {@link codemagic.util.shared.common.StringUtil#DIACRITICS_REMOVAL_MAP} 
 */
@Ignore
public class GenerateMaps {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String input = "\u0152";
		final String output = "OE";
		
		System.out.print(String.format("/*%-2s*/ ", output));
		for (int i = 0; i < input.length(); i++) {
			final String sHex = hex( (int)input.charAt(i));
			System.out.print(String.format(".put('%s',\"%s\")", sHex, output));
		}
		
	}
	
	// from http://stackoverflow.com/a/14671663
	/**
	 * @param n
	 * @return
	 */
	public static String hex(int n) {
	    // call toUpperCase() if that's required
	    return String.format("\\u%4s", Integer.toHexString(n).toUpperCase()).replace(' ', '0');
	}
}
