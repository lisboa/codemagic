package codemagic.generator.context.subject.template;

import java.util.Map;

import codemagic.generator.context.subject.AbstractGen;
import codemagic.generator.entrypoint.IContext;

public class _Template_Gen extends AbstractGen {

	public _Template_Gen(final IContext ctx) {
		super(ctx);
	}

	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		throw new RuntimeException("Need add the template name");
	}

	@Override
	public Map<String, Object> getModel() {
		throw new RuntimeException("Need return a model");
	}

	@Override
	public void populeModel() {
		throw new RuntimeException("Need populate the model");
	}

	@Override
	public String buildArtifactFullFileName() {
		throw new RuntimeException("Need return the name of the artifact file name");
	}

}
