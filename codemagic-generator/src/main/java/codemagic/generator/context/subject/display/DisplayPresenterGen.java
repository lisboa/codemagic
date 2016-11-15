package codemagic.generator.context.subject.display;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.util.ImportUtil;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public class DisplayPresenterGen {

	private static final Logger LOGGER = LoggerFactory.getLogger( DisplayPresenterGen.class ); 
	
	private final DisplayContext displayContext;
	
	private final SharedContext sharedContext;
	
	public DisplayPresenterGen(final IContext ctx) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
	
		Preconditions.checkArgument( ctx.getDisplayContext().isPresent(), "Display context cannot be null" );

		this.sharedContext = ctx.getSharedContext().get();
		
		this.displayContext = ctx.getDisplayContext().get(); 

	}

	public void generate() throws IOException {

		// input
		final String viewJava = ClassLoadUtil.loadFile(DisplayPresenterGen.class, "templates/display/_EntityName_Presenter.java.template");
		
		// pre-processing
		final Engine engine = new Engine();

		displayContext.getModel().put("displayImports", ImportUtil.mergeNotEmpty(displayContext.getImports(), sharedContext.getComponentTypeImports()));
		
		displayContext.getModel().put("focusedField", getFocusedField().get());
		
		final String transformedJava = engine.transform(viewJava, displayContext.getModel());
		
		final String fullName = displayContext.buildArtifactFullFileName( ArtefactyType.PRESENTER );
		
		final File dest = new File(fullName);
		
		System.out.println( dest.getAbsolutePath() );

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}

	/**
	 * 
	 * @return The field that should receive the focus. This is the first not
	 *         read only neither disable field (that is an editable field),
	 *         besides the showOnForm should be true. If none editable field
	 *         exists, return the first field.
	 */
	private Optional<FieldProperty> getFocusedField() {

		if (displayContext.getDisplayFields().isEmpty()) {
			
			LOGGER.warn("The displayFields are empty. None field to be displayed.");
			
			return Optional.absent();
		}
		
		FieldProperty result = displayContext.getDisplayFields().get(0);
		
		for (FieldProperty p : displayContext.getDisplayFields() ) {
			if ( !p.isReadOnly() && p.isEnabled()) {
				result = p;
				break;
			}
		}
		
		return Optional.of(result);
	}
}
