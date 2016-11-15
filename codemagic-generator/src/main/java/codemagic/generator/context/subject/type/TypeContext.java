package codemagic.generator.context.subject.type;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;

public class TypeContext extends AbstractSubjectContext implements IsSubjectContext<TypeContext> {

	public TypeContext(final SharedContext commonContext) {
		// subject is empty because the generated class names will be driver only by ArtefactType
		super("", commonContext); 
	}

	@Override
	public TypeContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	@Override
	public TypeContext setJavaSourceFolder(String javaSourceFolder) {
		internalSetJavaSourceFolder(javaSourceFolder);
		return this;
	}

	@Override
	public TypeContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	} 
}
