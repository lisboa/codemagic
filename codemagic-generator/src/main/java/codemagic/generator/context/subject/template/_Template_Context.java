package codemagic.generator.context.subject.template;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;

/**
 * <pre>
 *   1. Rename _Template_ to your business
 *   2. Rename _Subject_ to your subject
 *  </pre> 
 *
 */
public class _Template_Context extends AbstractSubjectContext implements IsSubjectContext<_Template_Context> {

	public _Template_Context(final SharedContext commonContext) {
		super("_Subject_", commonContext);
	}

	@Override
	public _Template_Context setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public _Template_Context setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public _Template_Context setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}
}
