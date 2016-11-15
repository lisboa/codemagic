package codemagic.generator.context.subject.orchestrator.subcontext;

import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;

import codemagic.generator.context.subject.orchestrator.OrchestratorContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class GridToolbarDriverGen {

	private final OrchestratorContext orchContext;
	
	public GridToolbarDriverGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getOrchestrator().isPresent(), "Orchestrator context cannot be null. Do you called the method beginOrchestrator() ?" );
		
		Preconditions.checkArgument( ctx.getOrchestrator().get().getToolbarDriverCtx() != null, "The subcontext toolbarDriverCtx cannot be null.");
		
		orchContext = ctx.getOrchestrator().get();
	}

	public void generate() throws IOException {

		// input
		final String template = ClassLoadUtil.loadFile(GridToolbarDriverGen.class, "templates/orchestrator/_BaseObjectName_Module.java.template");
		
		final Engine engine = new Engine();
		
		final String transformedJava = engine.transform(template, orchContext.getModel());
		
		final String fullName = orchContext.buildArtifactFullFileName( ArtefactyType.MODULE );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
