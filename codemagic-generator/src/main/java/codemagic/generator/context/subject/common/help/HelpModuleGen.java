package codemagic.generator.context.subject.common.help;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class HelpModuleGen extends HelpViewXmlGen {

	public HelpModuleGen(IContext ctx, HelpContext helpContext) {
		super(ctx, helpContext);
	}
	
	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/help/_BaseObjectName_Module.java.template";
	}
	
	@Override
	public String buildArtifactFullFileName() {
		return getHelpContext().buildArtifactFullFileName(ArtefactyType.MODULE);
	}
	
}
