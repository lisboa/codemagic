package codemagic.generator.context.shared.mehod;

import com.google.common.base.Preconditions;

import codemagic.generator.context.shared.FieldProperty;

public class FieldMethodDeclarationBuilder extends AbstractMethodParamBuilder {

	@Override
	public String buildOneMethodParam(final FieldProperty p) {
		
		Preconditions.checkArgument( p!= null , "The FieldProperty used to build the method declaration cannot be null");
		
		final StringBuilder builder = new StringBuilder(p.getFieldName().length());
		
		return builder
				.append("final ")
				.append(p.getFieldType().getJavaType()).append(" ")
				.append(p.getFieldName())
				.toString();
	}
}
