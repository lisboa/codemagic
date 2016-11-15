package codemagic.generator.context.subject.type;

import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class ContextGen {

	private final TypeContext typeContext;
	
	public ContextGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getTypeContext().isPresent(), "Type context cannot be null" );
	
		this.typeContext = ctx.getTypeContext().get();
	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(ContextGen.class, "templates/type/_BaseObjectName_Context.java.template");
		
		// pre-processing
		final Engine engine = new Engine();
		
		final String transformedJava = engine.transform(viewJava, typeContext.getModel());
		
		final String fullName = typeContext.buildArtifactFullFileName( ArtefactyType.CONTEXT );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
