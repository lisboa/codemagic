package codemagic.generator.context.shared.util;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Verify;

/**
 * Extract (1) the qualified constant name and (2) full qualified class name from the
 * full qualified constant name (the input).
 * 
 * <pre>
 * For example, for input "db.assist.view.client.place.NameTokens.uHome", I get:
 * 
 * qualified constant name   =>  NameTokens.uHome  (<ClassName>.<constantName>)
 * full qualified class name =>  db.assist.view.client.place.NameTokens
 * </pre>
 * 
 * With this, I can build a more human code. Instead of
 * 
 * <code>
 *    PlaceUtil.gotoPage(db.assist.view.client.place.NameTokens.uHome, placeManager);
 * </code>
 * 
 * I can simply do:
 * 
 * <code>
 *    PlaceUtil.gotoPage(NameTokens.uHome, placeManager);
 * </code>
 * 
 * But I need add the import to class NameToken (full qualified class name) and
 * know the qualified constant name (NameTokens.uHome)
 *
 */
@Immutable
public class ConstantHolder {

	private final String fullQualifiedConstantName;
	private final String qualifiedConstantName;
	private final String fullQualifiedClassName;

	public ConstantHolder(final String fullQualifiedConstantName) {
		this.fullQualifiedConstantName = sanitize(fullQualifiedConstantName).trim();
		Verify.verify( !this.fullQualifiedConstantName.isEmpty(), "The full qualified constant name cannot be null");

		final String[] parts = this.fullQualifiedConstantName.split("\\.");
		Verify.verify( parts.length > 1, "The constant name should has at least two parts: <ClassName>.<constantName>");
		
		this.qualifiedConstantName = buildQualifiedConstantName(parts, this.fullQualifiedConstantName.length());
		this.fullQualifiedClassName = buildFullQualifiedClassName(parts, this.fullQualifiedConstantName.length());
	}

	/**
	 * 
	 * @return For input "db.assist.view.client.place.NameTokens.uHome",
	 *         returns: "db.assist.view.client.place.NameTokens.uHome"
	 */
	public String getFullQualifiedConstantName() {
		return fullQualifiedConstantName;
	}

	/**
	 * 
	 * @return For input "db.assist.view.client.place.NameTokens.uHome",
	 *         returns: "NameTokens.uHome"
	 */
	public String getQualifiedConstantName() {
		return qualifiedConstantName;
	}

	/**
	 * 
	 * @return For input "db.assist.view.client.place.NameTokens.uHome",
	 *         returns: "db.assist.view.client.place.NameTokens"
	 */
	public String getFullQualifiedClassName() {
		return fullQualifiedClassName;
	}
	
	  //~~~~~~~~~~~~//
	 // Utilities  //
	//~~~~~~~~~~~~//
	
	private String buildFullQualifiedClassName(final String[] parts, final int capacity) {
		
		// It is safe because the ctor ensures that the input has at least two parts
		final int beforeLastIndex = parts.length - 2;
		
		final StringBuilder builder = new StringBuilder(capacity);
		
		for (int i =0; i < beforeLastIndex; i++) {
			builder.append(parts[i]).append("."); // need add the dot (".") at the end
		}
		
		builder.append(parts[beforeLastIndex]); // Should not add the dot (".") at the end
		
		return builder.toString();
	}

	private String buildQualifiedConstantName(final String[] parts, final int capacity) {
		
		// It is safe because the ctor ensures that the input has at least two parts
		final int beforeLastIndex = parts.length - 2;
		
		final StringBuilder builder = new StringBuilder(capacity);
		
		builder.append(parts[beforeLastIndex]).append(".").append(parts[beforeLastIndex + 1]);
		
		return builder.toString();
	}
	
}
