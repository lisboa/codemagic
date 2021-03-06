package codemagic.generator.context.subject.nav;

import java.io.File;
import java.io.IOException;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public class NavViewJavaGen {

	private final NavContext navContext;
	
	
	public NavViewJavaGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
		
		Preconditions.checkArgument( ctx.getNavContext().isPresent(), "Nav context cannot be null. You called the beginNav() ?" );
		
		this.navContext = ctx.getNavContext().get(); 

	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(NavViewJavaGen.class, "templates/nav/_BaseObjectName_NavView.java.template");
		
		final Engine engine = new Engine();
		
		final String transformedJava = engine.transform(viewJava, navContext.getModel());
		
		final String fullName = navContext.buildArtifactFullFileName( ArtefactyType.VIEW_JAVA );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
