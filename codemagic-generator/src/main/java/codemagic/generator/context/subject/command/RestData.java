package codemagic.generator.context.subject.command;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Verify;

public class RestData {
	
	private final String classTemplateName;
	private final String methodTemplateName;
	private final String methodToBeAdded;
	
	public RestData(final String classTemplateName, final String methodTemplateName, final String methodToBeAdded) {
		this.classTemplateName = sanitize( classTemplateName ).trim();
		this.methodTemplateName = sanitize( methodTemplateName ).trim();
		this.methodToBeAdded = sanitize( methodToBeAdded ).trim();
		
		Verify.verify( !this.classTemplateName.isEmpty(), "The class template name canot be empty" );
		Verify.verify( !this.methodTemplateName.isEmpty(), "The method template name canot be empty" );
		Verify.verify( !this.methodToBeAdded.isEmpty(), "The method to be added canot be empty" );
	}

	public String getClassTemplateName() {
		return classTemplateName;
	}

	public String getMethodTemplateName() {
		return methodTemplateName;
	}

	public String getMethodToBeAdded() {
		return methodToBeAdded;
	}
}
