package codemagic.generator.context.subject.type.infra.processor;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.mehod.AbstractMethodParamBuilder;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.subject.type.infra.util.ContextNameBuilder;

/**
 * This class allows construction of the list of arguments or parameters based
 * on a context. This list can be used to build a method declaration or method
 * call.
 * 
 * <pre>
 * Example
 * 
 * For a {@link TypeContext} BlacklistContext and a list of {@link FieldProperty} {'projectName', 'version'},
 * this class returns:
 * 
 *   BlacklistContext.get().getProjectName(), BlacklistContext.get().getVersion()
 * </pre>
 *
 */
public class ContextMethodArgumentBuilder extends AbstractMethodParamBuilder {

	private final ContextNameBuilder nameBuilder;
	
	public ContextMethodArgumentBuilder(final TypeContext typeContext) {
		this.nameBuilder = new ContextNameBuilder(typeContext);
	}

	@Override
	public String buildOneMethodParam(final FieldProperty p) {
		return nameBuilder.buildGetter(p);
	}

}
