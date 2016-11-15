package codemagic.util.shared.common;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class StringUtil {

	// from http://stackoverflow.com/questions/990904/javascript-remove-accents-in-strings
    static final String STRIP_STRING = 
            "AAAAAAACEEEEIIII" +
            "DNOOOOO.OUUUUY.." +
            "aaaaaaaceeeeiiii" +
            "dnooooo.ouuuuy.y" +
            "AaAaAaCcCcCcCcDd" +
            "DdEeEeEeEeEeGgGg" +
            "GgGgHhHhIiIiIiIi" +
            "IiIiJjKkkLlLlLlL" +
            "lJlNnNnNnnNnOoOo" +
            "OoOoRrRrRrSsSsSs" +
            "SsTtTtTtUuUuUuUu" +
            "UuUuWwYyYZzZzZz.";
    
	/**
	 * In addition to the {@link #normalizeLess(String)} also replace spaces and accept only
	 * letters (a-z), digits (0-9), dot or underline.
	 * 
	 * @param input
	 * @return
	 */
    public static String normalizeMore(final String input) {

		return onlyLowerLetterNumbersAndUnderline(
					replaceSpace(
					   removeAccents( input ).trim().toLowerCase()
					)	
				);
    }
    
    /**
     * Remove accents and change to lower case.
     * @param input
     * @return
     */
    public static String normalizeLess(final /*@Nullable*/ String input) {
    	return removeAccents( input ).trim().toLowerCase();
    }
    
    public static String replaceSpace(final String input) {
    	return sanitize(input).trim().replaceAll(" ", "_");
    }
    
    public static String onlyLowerLetterNumbersAndUnderline(final String input) {
    	return sanitize(input).trim().replaceAll("[^a-z0-9_\\.]", "");
    }

	/**
	 * Ref.:
	 * https://github.com/mkristian/rails-resty-gwt/blob/master/rails-gwt/src/
	 * main/java/de/mkristian/gwt/rails/caches/FilterUtils.java
	 * 
	 * @param input
	 *            The String whose accents will be stripped.
	 * @return The String without accents
	 */
	public static String removeAccents(final String input) {

		final StringBuilder answer = new StringBuilder();
		final String sanitizedInput = sanitize(input).trim(); 
		
		for (int i = 0; i < sanitizedInput.length(); i++) {
			char ch = sanitizedInput.charAt(i);
			final int chindex = ch - 192; // Index of character code in the strip
									// string
			if (chindex >= 0 && chindex < STRIP_STRING.length()) {
				// Character is within our table, so we can strip the accent...
				final char outch = STRIP_STRING.charAt(chindex);
				// ...unless it was shown as a '.'
				if (outch != '.')
					ch = outch;
			}
			answer.append(ch);
		}
		return answer.toString();
	}
}
