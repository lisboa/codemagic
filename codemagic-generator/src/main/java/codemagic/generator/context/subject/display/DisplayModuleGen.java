package codemagic.generator.context.subject.display;

import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class DisplayModuleGen {

	private final DisplayContext displayContext;
	
	
	public DisplayModuleGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
		
		Preconditions.checkArgument( ctx.getDisplayContext().isPresent(), "Display context cannot be null" );
		
		this.displayContext = ctx.getDisplayContext().get(); 
	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(DisplayModuleGen.class, "templates/display/_EntityName_Module.java.template");
		
		// pre-processing
		final Engine engine = new Engine();
		
		final String transformedJava = engine.transform(viewJava, displayContext.getModel());
		
		final String fullName = displayContext.buildArtifactFullFileName( ArtefactyType.MODULE );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
