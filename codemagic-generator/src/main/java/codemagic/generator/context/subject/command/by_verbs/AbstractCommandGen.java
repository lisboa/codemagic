package codemagic.generator.context.subject.command.by_verbs;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;
import grain.util.ClassLoadUtil;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import codemagic.generator.context.shared.urlparam.UrlParamProcessor.Result;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.subject.type.infra.processor.ContextParamProcessor;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.io.Files;

public abstract class AbstractCommandGen {

	private final TypeContext typeContext;
	private final String templateName; 
	private final ContextParamProcessor paramProcessor;
	private final CommandContext commandContext;
	
	
	public AbstractCommandGen(final IContext ctx, final String templateName, final String contextId) {
		
		Preconditions.checkArgument( ctx != null, "Context cannot be null");

		Preconditions.checkArgument( ctx.getSharedContext().isPresent(),	"Common context cannot be null");

		Preconditions.checkArgument( !ctx.getCommandContexts().isEmpty(), "Datagrid requires a command to fetch and delete items. You called the beginCommand() ?");
		
		Preconditions.checkArgument( ctx.getTypeContext().isPresent(), "Command requires a Type Context to generate the package types. You called the beginType() ?");
		
		this.templateName = sanitize(templateName).trim();
		
		this.commandContext = ctx.getCommandContexts().get(contextId);

		typeContext = ctx.getTypeContext().get();
		
		paramProcessor = new ContextParamProcessor(typeContext);
		
		Verify.verify( !this.templateName.isEmpty(), "[%s] The template name cannot be null", this.getClass());
		
		Verify.verify(this.commandContext != null, "The command context for conetxtId %s cannot be null", contextId);
	}

	/**
	 * Add specific validation code.
	 * @param commandContext2 
	 */
	protected void customValidation(final CommandContext commandContext) {
		/*Do nothing*/
	}
	
	
	/**
	 * Add custom parameter to model of the command being processed.
	 * 
	 * @param model
	 */
	protected void customizeModel(final Map<String, Object> model){
		/*Do nothing*/
	}; 
	
	public void generate() throws IOException {

		customValidation(this.commandContext);
		
		final String template = ClassLoadUtil.loadFile(AbstractCommandGen.class, templateName);
		
		final Engine engine = new Engine();

		final Result params = paramProcessor.process(commandContext.getRestUrl());
		
		commandContext.getModel().put("fullContextClassName", typeContext.buildFullClassName(ArtefactyType.CONTEXT));
		commandContext.getModel().put("fullContextClassName", typeContext.buildFullClassName(ArtefactyType.CONTEXT));
		commandContext.getModel().put("restUrl", params.getInterpolatedUrl());
		commandContext.getModel().put("requestParamsMethodArguments", params.getMethodArguments());
		
		customizeModel(commandContext.getModel());
		
		final String transformedJava = engine.transform(template, commandContext.getModel());

		final String fullName = commandContext.buildArtifactFullFileName(ArtefactyType.COMMAND);

		final File dest = new File(fullName);

		System.out.println(dest.getAbsolutePath());

		Files.write(transformedJava, dest, Charsets.UTF_8);
	}
}
