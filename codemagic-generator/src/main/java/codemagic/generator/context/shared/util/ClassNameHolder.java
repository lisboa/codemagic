package codemagic.generator.context.shared.util;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Verify;

public class ClassNameHolder {

	private final String fullQualifiedClassName;
	private final String simpleClassName;
	private final String packageName;
	
	public ClassNameHolder(final String fullQualifiedClassName) {
		this.fullQualifiedClassName = sanitize(fullQualifiedClassName).trim();
		Verify.verify( !this.fullQualifiedClassName.isEmpty(), "The full qualified class name cannot be empty" );
		
		this.simpleClassName = ContextUtil.getSimpleClassName(fullQualifiedClassName);
		this.packageName = buildPackageName(this.fullQualifiedClassName);
	}

	public String getName() {
		return fullQualifiedClassName;
	}

	public String getSimpleName() {
		return simpleClassName;
	}
	
      //~~~~~~~~~~~~~//
	 //  Utilities  //
	//~~~~~~~~~~~~~//
	
	public String getPackageName() {
		return packageName;
	}

	private String buildPackageName(final String fullQualifiedClassName) {
		final String[] parts = fullQualifiedClassName.split("\\.");
		
		// default class name
		if (parts.length < 2) {
			return "";
		}
		
		final int beforeLastIndex = parts.length - 2;
		
		final int capacity = fullQualifiedClassName.length();
		final StringBuilder builder = new StringBuilder(capacity);
		
		for (int i = 0; i < beforeLastIndex ; i++) {
			builder.append(parts[i]).append(".");
		}
		
		builder.append(parts[beforeLastIndex]);
		
		return builder.toString();
	}

	
}
