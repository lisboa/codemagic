package codemagic.generator.context.subject.common.help;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class HelpViewJavaGen extends HelpViewXmlGen {

	public HelpViewJavaGen(IContext ctx, HelpContext helpContext) {
		super(ctx, helpContext);
	}
	
	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/help/_BaseObjectName_Presenter.java.template";
	}
	
	@Override
	public String buildArtifactFullFileName() {
		return getHelpContext().buildArtifactFullFileName(ArtefactyType.PRESENTER);
	}
	
}
