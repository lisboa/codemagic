package codemagic.generator.context.subject.command.by_verbs;

import com.google.common.base.Verify;

import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.entrypoint.IContext;

public class DeleteCommandGen extends AbstractCommandGen {

	public DeleteCommandGen(final IContext ctx, final String templateName, final String contextId) {
		super(ctx, templateName, contextId);
	}
	
	@Override
	protected void customValidation(final CommandContext commandContext) {
		super.customValidation(commandContext);
		
		Verify.verify( !commandContext.getQualifiedInputClassName().isEmpty(), "qualifiedInputClassName of contextId %s cannot be null. Please, call the method setQualifiedInputClassName(..)", commandContext.getContextId());
	}
}
