package codemagic.generator.context.subject.type.infra.util;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.types.ArtefactyType;

import com.google.common.base.Preconditions;


/**
 * Build the getters and setters for context fields.
 * <pre>
 * Example:
 * 
 * For the context BlacklistContext, this class can build the getters bellow, that return the 
 * getProjectName() of BlacklistContext (The context is an Optional): 
 * 
 * BlacklistContext.get().getProjectName()
 * 
 *</pre>
 *@see The method 'saveEntity' in template _BaseObjectName_Presenter.java.template as an example.
 */
public class ContextNameBuilder {
	private final String capitalizedContextClassName;

	public ContextNameBuilder(final TypeContext typeContext) {
		Preconditions.checkArgument( typeContext != null, "The Type Context cannot be null." );
		this.capitalizedContextClassName = typeContext.buildClassName(ArtefactyType.CONTEXT);
	}
	
	public String buildGetter(final FieldProperty p) {
		return capitalizedContextClassName + ".get()." + p.getGetterFieldPath() + "()";
	}
	
}
