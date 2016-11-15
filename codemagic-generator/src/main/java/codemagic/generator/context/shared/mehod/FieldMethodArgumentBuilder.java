package codemagic.generator.context.shared.mehod;

import codemagic.generator.context.shared.FieldProperty;

public class FieldMethodArgumentBuilder extends AbstractMethodParamBuilder {

	@Override
	public String buildOneMethodParam(final FieldProperty p) {
		return p.getFieldName();
	}

}
