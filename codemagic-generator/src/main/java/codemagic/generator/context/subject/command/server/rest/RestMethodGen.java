package codemagic.generator.context.subject.command.server.rest;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Preconditions;

import codemagic.generator.context.shared.parser.JavaParserWrapper;
import codemagic.generator.context.shared.urlparam.UrlParamProcessor;
import codemagic.generator.context.subject.AbstractGen;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.command.server.rest.etc.ServiceHolder;
import codemagic.generator.context.subject.command.server.rest.etc.Subcontext;
import codemagic.generator.entrypoint.IContext;

public class RestMethodGen extends AbstractGen {

	private static final Logger LOGGER = LoggerFactory.getLogger( RestMethodGen.class ); 
	
	private final CommandContext commandContext;
	private final ServiceHolder<?> serviceHolder;
	private final Subcontext serviceContext;
	private final UrlParamProcessor paramProcessor;
	
	private final Result classGenerationResult;
	private final String methodTemplateName;
	private final String methodToBeAddedName;

	public RestMethodGen(
			final IContext ctx, 
			final CommandContext commandContext, 
			final Result classGenerationResult,
			final String methodTemplateName,
			final String methodNameToBeAdded) {
		
		super(ctx, true);
		
		Preconditions.checkArgument(commandContext != null, "The CommandContext cannot be null");
		
		Preconditions.checkArgument(commandContext.getServerHolder().isPresent(),"The ServerHolder cannot be null. You called the beginCommand().beginServerRest() ?");
		
		

		this.commandContext = commandContext;
		this.serviceHolder = commandContext.getServerHolder().get();
		this.serviceContext = serviceHolder.getSubcontext();
		this.classGenerationResult = classGenerationResult;
		this.methodTemplateName = methodTemplateName;
		this.methodToBeAddedName = methodNameToBeAdded;
		this.paramProcessor = new UrlParamProcessor();
		
	}

	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Map<String, Object> getModel() {
		return serviceContext.getModel();
	}

	@Override
	public void populeModel() {
		Preconditions.checkArgument(serviceContext.getSharedContext().getTargetEntity().isPresent(),
				"The target Entity cannot be null. You called the method begin().setTargetEntity() before ?");

		final codemagic.generator.context.shared.urlparam.UrlParamProcessor.Result params = paramProcessor.process(commandContext.getRestUrl());
		
		serviceContext.getModel().put("fullEntityClassName", serviceContext.getSharedContext().getTargetEntity().get().getName());
		serviceContext.getModel().put("EntityClassName", serviceContext.getSharedContext().getTargetEntity().get().getSimpleName());
		serviceContext.getModel().put("restUrl", commandContext.getRestUrl());
		serviceContext.getModel().put("InputClassName", commandContext.getInputClassName());
		serviceContext.getModel().put("fullInputClassName", commandContext.getQualifiedInputClassName());
		serviceContext.getModel().put("requestParamsMethodArguments", params.getMethodArguments());
		serviceContext.getModel().put("requestParamsMethodDeclarations", params.getMethodDeclarations());
		serviceContext.getModel().put("requestParams", params.getExtractedVariables());
		
	}

	@Override
	public String buildArtifactFullFileName() {
		return serviceHolder.getFullArtifactFileName();
	}

	@Override
	public Result generate() throws IOException {

		final FileNameHolder fileNameHolder = new FileNameHolder( buildArtifactFullFileName() );
		
		if (! shouldGenerateFile(fileNameHolder.getFile())) {

			LOGGER.info("<<< The file '{}' already exists. It is not overwritable, because the flag fileOverwrite is true.", fileNameHolder.getFile().getAbsolutePath());
			
			return new Result(fileNameHolder.getFullFileName(), false);
			
		}
		
		final Engine engine = new Engine();
		
		populeModel();

		// 1. Parsers
		final JavaParserWrapper classParser = new JavaParserWrapper(classGenerationResult.getFullJavaFileName(), this.getClass());
		final JavaParserWrapper methodParser = new JavaParserWrapper(methodTemplateName, this.getClass(), getModel(), engine);

		// 2. Add imports into class
		classParser.addImport(methodParser);
		
		// 3. Add the method
		classParser.addMethod(methodParser, methodToBeAddedName);

		// 4. Write to file
		writeToFile(classParser.asString(), fileNameHolder.getFile());
		
		return new Result(fileNameHolder.getFullFileName(), true);
	}
}
