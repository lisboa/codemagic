package codemagic.generator.context.subject.orchestrator.subcontext;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;

/**
 * Used to generate class and artifact names.
 *
 */
public class OrchestratorSubContext  extends AbstractSubjectContext implements IsSubjectContext<OrchestratorSubContext> {

	public OrchestratorSubContext(final String subject, final SharedContext commonContext) {
		super(subject, commonContext);
	}

	@Override
	public OrchestratorSubContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public OrchestratorSubContext setJavaSourceFolder(final String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public OrchestratorSubContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}

}
