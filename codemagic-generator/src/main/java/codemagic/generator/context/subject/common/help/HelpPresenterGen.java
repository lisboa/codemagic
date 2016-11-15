package codemagic.generator.context.subject.common.help;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class HelpPresenterGen extends HelpViewXmlGen {

	public HelpPresenterGen(IContext ctx, HelpContext helpContext) {
		super(ctx, helpContext);
	}
	
	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/help/_BaseObjectName_View.java.template";
	}
	
	@Override
	public String buildArtifactFullFileName() {
		return getHelpContext().buildArtifactFullFileName(ArtefactyType.VIEW_JAVA);
	}
	
}
