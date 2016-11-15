package codemagic.generator.context.subject.common.help;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;

/**
 * this context can not depend on other contexts because it is used by other
 * contexts. Normally used as sub-context for others contexts.
 */
public class HelpContext extends AbstractSubjectContext implements IsSubjectContext<HelpContext> {

	public HelpContext(final SharedContext commonContext) {
		super("Help", commonContext);
	}

	@Override
	public HelpContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public HelpContext setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public HelpContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}
}
