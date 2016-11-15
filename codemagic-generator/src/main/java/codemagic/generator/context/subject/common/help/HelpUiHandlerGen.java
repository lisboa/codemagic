package codemagic.generator.context.subject.common.help;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class HelpUiHandlerGen extends HelpViewXmlGen {

	public HelpUiHandlerGen(IContext ctx, HelpContext helpContext) {
		super(ctx, helpContext);
	}
	
	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/help/_BaseObjectName_UiHandlers.java.template";
	}
	
	@Override
	public String buildArtifactFullFileName() {
		return getHelpContext().buildArtifactFullFileName(ArtefactyType.UI_HANDLER);
	}
	
}
