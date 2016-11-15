package codemagic.generator.context.subject.display;

import java.io.File;
import java.io.IOException;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public class DisplayViewJavaGen {

	private final DisplayContext displayContext;
	
	
	public DisplayViewJavaGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
		
		Preconditions.checkArgument( ctx.getDisplayContext().isPresent(), "Display context cannot be null" );
		
		this.displayContext = ctx.getDisplayContext().get(); 

	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(DisplayViewJavaGen.class, "templates/display/_EntityName_View.java.template");
		
		final Engine engine = new Engine();
		
		displayContext.getModel().put("displayImports", displayContext.getImports());
		
		final String transformedJava = engine.transform(viewJava, displayContext.getModel());
		
		final String fullName = displayContext.buildArtifactFullFileName( ArtefactyType.VIEW_JAVA );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
