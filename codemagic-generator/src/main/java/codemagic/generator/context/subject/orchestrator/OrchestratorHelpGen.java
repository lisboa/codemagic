package codemagic.generator.context.subject.orchestrator;

import java.io.IOException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import codemagic.generator.context.subject.common.help.HelpContext;
import codemagic.generator.context.subject.common.help.HelpModuleGen;
import codemagic.generator.context.subject.common.help.HelpPresenterGen;
import codemagic.generator.context.subject.common.help.HelpUiHandlerGen;
import codemagic.generator.context.subject.common.help.HelpViewJavaGen;
import codemagic.generator.context.subject.common.help.HelpViewXmlGen;
import codemagic.generator.entrypoint.IContext;

public class OrchestratorHelpGen {

	private final Optional<HelpContext> helpContext;
	private final IContext ctx;

	public OrchestratorHelpGen(final IContext ctx) {

		Preconditions.checkArgument(ctx != null, "Context cannot be null");
		Preconditions.checkArgument(ctx.getOrchestrator().isPresent() , "The orckestrator presenter cannot be null. Do you called beginOrchestrator() ?");
		
		this.ctx = ctx;
		
		if ( ctx.getNavContext().isPresent() ) {
			helpContext = ctx.getNavContext().get().getHelpContext();
		} else {
			helpContext = Optional.absent();
		}
	}
	
	public void generate() throws IOException {
		if (helpContext.isPresent()) {
			new HelpViewXmlGen(ctx, helpContext.get()).generate();
			new HelpViewJavaGen(ctx, helpContext.get()).generate();
			new HelpPresenterGen(ctx, helpContext.get()).generate();
			new HelpUiHandlerGen(ctx, helpContext.get()).generate();
			new HelpModuleGen(ctx, helpContext.get()).generate();
		}
	}
}
