package codemagic.util.shared.common.uri;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import javax.annotation.concurrent.Immutable;

/**
 * This class allow to use spring style uri template for both: Spring and
 * String.format method.
 * 
 * To do this, it builds a "string format style" from "spring style template".
 * That is, replace variables '{xxx}' for '%s'.
 * 
 * <b>Example</b> <br />
 * 
 * <pre>
 *   {@code http://myserver:port/{variable1}?{variable2} => http://myserver:port/%s?%s }
 * </pre>
 */
@Immutable
public class UriTemplate {

	private final String nameTokenFormatStyle;
	private final String nameTokenSpringStyle;
	private final String servicePathPrefix;
	
	/**
	 * 
	 * @param nameTokenSpringStyle
	 *            An uri without the server part, as used in the GWTP name
	 *            token. Example: "/u/builds/modules/{moduleId}/{buildNumber}"
	 */
	public UriTemplate( final String nameTokenSpringStyle, final String servicePathPrefix ) {

		// "/u/builds/modules/{moduleId}/{buildNumber}"
		this.nameTokenSpringStyle = sanitize( nameTokenSpringStyle ).trim();

		// Replaces "{x}" for "%s" =>  "/u/builds/modules/%s/%s"
		this.nameTokenFormatStyle = convert2StringFormat( this.nameTokenSpringStyle );
		
		// Prepend the services uri template. See buildServiceUriTemplateXXX
		this.servicePathPrefix = sanitize(servicePathPrefix);
		
	}

	/**
	 * 
	 * @return  An uri without the server part, as used in the GWTP name
	 *            token. Example: "/u/builds/modules/{moduleId}/{buildNumber}"
	 */
	public String getNameTokenSpringStyle() {
		return nameTokenSpringStyle;
	}

	/**
	 * 
	 * @return Same as {@link #getNameTokenSpringStyle()}, but with "{x}"
	 *         replaced for "%s", to be used with
	 *         {@link String#format(String, Object...)}. Example:
	 *         "/u/builds/modules/%s/%s"
	 */
	public String getNameTokenFormatStyle() {
		return nameTokenFormatStyle;
	}


	/**
	 * 
	 * @param baseUrl
	 *            The {@code http://<server>[:<port>]} part.
	 * @return The service uri template in spring format. Example.
	 *         {@code http://<server>[:<port>]}/svc/u/builds/modules/{moduleId}/{buildNumber}
	 */
	public String buildServiceUriTemplateSpringStyle(final String baseUrl) {
		return baseUrl + this.servicePathPrefix + this.nameTokenSpringStyle;
	}
	
	/**
	 * Same as {@link #buildServiceUriTemplateSpringStyle(String)}, but does not
	 * add the {@link #servicePathPrefix}.
	 * 
	 * <b>Example</b><br />
	 * <pre>
	 * page => /u/script
	 * service => /svc/u/script
	 * </pre> 
	 * 
	 * 
	 * @param baseUrl
	 * @return
	 */
	public String buildPageUriTemplateSpringStyle(final String baseUrl) {
		return baseUrl + this.nameTokenSpringStyle;
	}

	/**
	 * 
	 * @return The service uri template with the variables '{xxx}' replaced for '%s'.
	 *         This allows to use the String.format() or StringUtilClient.format() to
	 *         interpolate the uri template.
	 */
	public String buildServiceUriTemplateFormatStyle(final String baseUrl) {
		return baseUrl + this.servicePathPrefix + this.nameTokenFormatStyle;
	}
	
	/**
	 * Replace '{variable}' for '%s'.<br />
	 * <b>Example</b> <br />
	 * 
	 * <pre>
	 * {@code http://myserver:port/{variable1}?{variable2} => http://myserver:port/%s?%s }
	 * </pre>
	 * 
	 * @param uri
	 *            The uri template in Spring style
	 * @return A uri template in String format style, that is, repalce '{xxx}'
	 *         for '%s'.
	 */
	private static String convert2StringFormat(final String uri) {
		
		if (uri.isEmpty()) {	return "";	}
		
		final StringBuilder builder = new StringBuilder(uri.length());
		
		boolean withinBracket = false;
		
		for (int i = 0; i < uri.length(); i++) {
			
			final char c = uri.charAt(i);
			
			if (c == '{') {
				withinBracket = true;    // 1. Entering a bracket
			} else if (c=='}') {         
				withinBracket = false;   // 2. Exiting a bracked 
				builder.append("%s");    //    replace '{xxx}' for '%s'
			} else if (!withinBracket) { // 3. Out bracket
				builder.append(c);       //    Add the original char
			}
		}
		
		return builder.toString();
	}
}
