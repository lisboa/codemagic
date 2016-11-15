package codemagic.generator.context.shared.util;

import java.io.File;

public abstract class OtherUtil {

	/**
	 * Escape the parameters delimiters.
	 * 
	 * @return For an input like ${abc}, returns _abc_. This is safe to add
	 *         to uibinder file (xml).
	 */
	public static String escapeParamDelimiters(final String text) {
		return text.replaceAll("\\$", "").replaceAll("\\{", "_").replaceAll("}", "_");
	}
	
	public static boolean shouldGenerateFile(final File dest, final boolean fileOverwrite) {
		if (fileOverwrite) {
			return true;
		} else {
			return !dest.exists();
		}
	}
}
