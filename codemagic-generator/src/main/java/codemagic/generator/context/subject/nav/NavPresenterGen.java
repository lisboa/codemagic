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

public class NavPresenterGen {

	private final NavContext navContext;
	
	public NavPresenterGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getNavContext().isPresent(), "Nav context cannot be null. You called the beginNav() ?" );

		this.navContext = ctx.getNavContext().get(); 

	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(NavPresenterGen.class, "templates/nav/_BaseObjectName_NavPresenter.java.template");
		
		// pre-processing
		final Engine engine = new Engine();
		
		navContext.getModel().put("NavEventClassName",  navContext.buildClassName( ArtefactyType.EVENT) );
		
		final String transformedJava = engine.transform(viewJava, navContext.getModel());
		
		final String fullName = navContext.buildArtifactFullFileName( ArtefactyType.PRESENTER );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
