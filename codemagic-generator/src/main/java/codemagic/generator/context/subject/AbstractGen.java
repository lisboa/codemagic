package codemagic.generator.context.subject;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.io.Files;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.util.OtherUtil;
import codemagic.generator.entrypoint.IContext;
import grain.util.ClassLoadUtil;

public abstract class AbstractGen {

	private static final Logger LOGGER = LoggerFactory.getLogger( AbstractGen.class );
	
	private final IContext ctx;
	
	private final SharedContext sharedContext;
	
	private final boolean fileOverwrite;


	public AbstractGen(final IContext ctx) {
		this(ctx, true);
	}
	
	public AbstractGen(final IContext ctx, final boolean fileOverwrite) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "Common context cannot be null" );
		
		this.ctx = ctx;
		
		sharedContext = ctx.getSharedContext().get();
	
		this.fileOverwrite = fileOverwrite;
	}

	public IContext getCtx() {
		return ctx;
	}

	public SharedContext getSharedContext() {
		return sharedContext;
	}

	protected boolean shouldGenerateFile(final File dest) {
		return OtherUtil.shouldGenerateFile(dest, fileOverwrite);
	}

	/**
	 * 
	 * @return Example:
	 *         templates/datagrid/withasynprovider/table/_BaseObjectName_View.ui
	 *         .xml.template
	 */
	public abstract String getTemplateNameRelativeToResourceFolder();
	
	public abstract Map<String, Object> getModel();

	public abstract void populeModel();
	
	public abstract String buildArtifactFullFileName();
	
	/**
	 * delete the previous generated file. This is useful to cleanup the older
	 * files before generate the news.
	 */
	public void delete() {
		
		final FileNameHolder fileNameHolder = new FileNameHolder( buildArtifactFullFileName() );
		
		if (shouldGenerateFile(fileNameHolder.getFile())) {
			fileNameHolder.getFile().delete();
			
		} else {
			
			LOGGER.info("[Skipped] The deletion of file {} was skipped, because fileOverwrite is {}", fileNameHolder.getFullFileName(), fileOverwrite);
		}
	}
	
	public Result generate() throws IOException {

		// 1. The full file name
		final FileNameHolder fileNameHolder = new FileNameHolder( buildArtifactFullFileName() );
		
		if (! shouldGenerateFile(fileNameHolder.getFile())) {

			LOGGER.info("<<< The file '{}' already exists. It is not overwritable, because the flag fileOverwrite is true.", fileNameHolder.getFile().getAbsolutePath());
			
			return new Result(fileNameHolder.getFullFileName(), false);
			
		}

		// 2. Tempalte -> content
		final String templateContent = ClassLoadUtil.loadFile(AbstractGen.class, getTemplateNameRelativeToResourceFolder());
		
		final Engine engine = new Engine();

		populeModel();
		
		final String fileContent = engine.transform(templateContent, getModel());
		
		// 3. 
		writeToFile(fileContent, fileNameHolder.getFile());
		
		return new Result(fileNameHolder.getFullFileName(), true);
	}

	
	  //
	 //  Result
	//
	
	public static class Result {
		private final String fullDestFileName;
		private final boolean generated;

		public Result(final String fullDestFileName, final boolean generated) {
			this.fullDestFileName = sanitize(fullDestFileName).trim();
			this.generated = generated;
		}

		
		public boolean isGenerated() {
			return generated;
		}

		public String getFullJavaFileName() {
			return fullDestFileName;
		}
	}
	
	public static class FileNameHolder {
		
		private final String fullFileName;
		private final File file;
		
		public FileNameHolder(final String fullFileName) {
			
			this.fullFileName = fullFileName;
			
			checkFullName();
			
			this.file = new File(this.fullFileName);
		}
		
		public String getFullFileName() {
			return fullFileName;
		}

		public File getFile() {
			return file;
		}

		private void checkFullName() {
			Verify.verify( !fullFileName.trim().isEmpty(), "The artifact file name cannot be empty");
			Verify.verify( fullFileName.split(File.separator).length > 1, "The artifact filename is very short. It should has at least a sub folder: %s", fullFileName);
		}
		
		
	}
	
	
	  //~~~~~~~~~~~~//
	 // Utilities  //
	//~~~~~~~~~~~~// 
	
	protected void writeToFile(final String transformedJava, final File dest) throws IOException {
		
		Files.write(transformedJava, dest, Charsets.UTF_8);
		
		LOGGER.info(">>> The file '{}' successful created", dest.getAbsolutePath());
		
		return;
	}

	protected void checkFullName(final String fullName) {
		Verify.verify(fullName != null, "The artifact file name cannot be null");
		Verify.verify( !fullName.trim().isEmpty(), "The artifact file name cannot be empty");
		Verify.verify( fullName.split(File.separator).length > 1, "The artifact filename is very short. It should has at least a sub folder: %s", fullName);
	}
}
