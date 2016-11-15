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

public class StateGen {

	private final TypeContext typeContext;
	
	public StateGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getTypeContext().isPresent(), "Type context cannot be null" );
	
		this.typeContext = ctx.getTypeContext().get();
	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(StateGen.class, "templates/type/_BaseObjectName_State.java.template");
		
		// pre-processing
		final Engine engine = new Engine();
		
		final String transformedJava = engine.transform(viewJava, typeContext.getModel());
		
		final String fullName = typeContext.buildArtifactFullFileName( ArtefactyType.STATE );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
