package codemagic.generator.context.subject.common.help;

import java.util.Map;

import com.google.common.base.Preconditions;

import codemagic.generator.context.subject.AbstractGen;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class HelpViewXmlGen extends AbstractGen {

	private final HelpContext helpContext;

	public HelpViewXmlGen(final IContext ctx, final HelpContext helpContext) {
		super(ctx);

		Preconditions.checkArgument(helpContext != null,
				"The help context acnnot be null. Do you add a help in some begin() ? For example: 'beginNav().addButton().whenClickedShowHelp()'");

		this.helpContext = helpContext;

	}

	
	public HelpContext getHelpContext() {
		return helpContext;
	}

	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/help/_BaseObjectName_View.ui.xml.template";
	}

	@Override
	public Map<String, Object> getModel() {
		return helpContext.getModel();
	}

	@Override
	public void populeModel() {
		/*Do nothing*/
	}

	@Override
	public String buildArtifactFullFileName() {
		return helpContext.buildArtifactFullFileName(ArtefactyType.VIEW_XML);
	}
}
