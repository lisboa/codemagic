package codemagic.generator.context.subject.orchestrator;

import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;

import codemagic.generator.context.subject.common.help.HelpContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class OrchestratorModuleGen {

	private final OrchestratorContext orchContext;
	private final Optional<HelpContext> helpContext;
	
	public OrchestratorModuleGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getOrchestrator().isPresent(), "Orchestrator context cannot be null. Do you called the method beginOrchestrator() ?" );
		
		orchContext = ctx.getOrchestrator().get();
		
		if ( ctx.getNavContext().isPresent() ) {
			 helpContext = ctx.getNavContext().get().getHelpContext();
		} else {
			helpContext = Optional.absent();
		}
	}

	public void generate() throws IOException {

		// input
		final String template = ClassLoadUtil.loadFile(OrchestratorModuleGen.class, "templates/orchestrator/_BaseObjectName_Module.java.template");
		
		final Engine engine = new Engine();
		
		if (helpContext.isPresent()) {
			orchContext.getModel().put("fullHelpModule",  helpContext.get().buildFullClassName(ArtefactyType.MODULE));
			orchContext.getModel().put("helpModule",  helpContext.get().buildClassName(ArtefactyType.MODULE));
		}
		
		final String transformedJava = engine.transform(template, orchContext.getModel());
		
		final String fullName = orchContext.buildArtifactFullFileName( ArtefactyType.MODULE );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
