package codemagic.generator.context.shared.util;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Verify;

import codemagic.generator.context.shared.component.AbstractWidget;
import codemagic.util.shared.common.StringUtil;

public abstract class ContextUtil {
	
	/**
	 * 
	 * @param button
	 *            The widget whose happened name should be built.
	 * @param suffix
	 *            A String to be added to end of the built name. Example:
	 *            "_CLICKED", "_HOVERED", etc.
	 * @return A name like "BTN_SAVE_CLICKED", "TXT_COMMENT_PRESSED"
	 */
	public static String buildHappenedName(final AbstractWidget<?> widget, final String suffix) {
		return widget.getName().getConstantName() + suffix;
	}
	
	/**
	 *  
	 * @param fullQualifiedClassName The full class name: [package].[className]
	 * @return The unqualified class name. This is the last part of qualified class name: [className]
	 */
	public static String getSimpleClassName(final String fullQualifiedClassName) {
		final String[] parts = fullQualifiedClassName.split("\\.");
		
		final int length = parts.length;
		
		if (length == 0) {
			return "";
		}
		
		return parts[length - 1];
	}
	
	/**
	 * 
	 * @param input
	 *            A String to be used to generate a valid java name
	 * @param prefix
	 *            A prefix to be added at the beginning of the generated name
	 * @return A valid java name
	 */
	public static String buildValidJavaName( String input, final String prefix ) {
		
		input = sanitize(input).trim(); 
		
		Verify.verify( !input.isEmpty(), "The name used to generate the java name cannot be null" );
				
		final String[] parts = StringUtil.normalizeMore(input).split("\\s");
		
		final StringBuilder builder = new StringBuilder(input.length() + prefix.length());
		
		builder.append(prefix);
		
		for (final String s : parts) {
			builder.append( StringUtils.capitalize(s));
		}
		
		return StringUtils.uncapitalize(builder.toString());

	}
}
