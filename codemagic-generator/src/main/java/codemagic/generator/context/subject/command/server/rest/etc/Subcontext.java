package codemagic.generator.context.subject.command.server.rest.etc;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;

public class Subcontext extends AbstractSubjectContext implements IsSubjectContext<Subcontext> {

	public Subcontext(String subject, SharedContext sharedContext) {
		super(subject, sharedContext);
	}

	@Override
	public Subcontext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public Subcontext setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public Subcontext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}
}