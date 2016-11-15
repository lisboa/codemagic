package codemagic.generator.context.subject.command;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableMap;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractGen.Result;
import codemagic.generator.context.subject.command.by_verbs.AbstractCommandGen;
import codemagic.generator.context.subject.command.by_verbs.DeleteCommandGen;
import codemagic.generator.context.subject.command.by_verbs.GetCommandGen;
import codemagic.generator.context.subject.command.by_verbs.PutCommandGen;
import codemagic.generator.context.subject.command.server.rest.RestMethodGen;
import codemagic.generator.context.subject.command.server.rest.RestServerClassGen;
import codemagic.generator.entrypoint.IContext;

/**
 * Orquestrator
 *
 */
public class CommandGen {

	private final IContext ctx;
	private final Map<String, CommandContext> commandContexts;
	private final  SharedContext sharedContext;
	
	private static final ImmutableMap<String, RestData> restMap = ImmutableMap.<String, RestData>of(
			"get", new RestData("/templates/command/server/rest/restService.java.template","/templates/command/server/rest/getEntities.java.template", "getEntities"),
			"save", new RestData("/templates/command/server/rest/restService.java.template","/templates/command/server/rest/saveOrUpdate.java.template", "saveOrUpdate"),
			"delete", new RestData("/templates/command/server/rest/restService.java.template","/templates/command/server/rest/delete.java.template", "delete")
			
	); 
	
	private static final ImmutableMap<CommandContext.Verb, MyData> commandMap = ImmutableMap
			.<CommandContext.Verb, MyData> of(
					CommandContext.Verb.GET, new MyData("/templates/command/get/GetCommand.java.template", GetCommandGen.class), 
					CommandContext.Verb.POST, new MyData("/templates/command/put_or_post/PutOrPostCommand.java.template", PutCommandGen.class), 
					CommandContext.Verb.PUT, new MyData("/templates/command/put_or_post/PutOrPostCommand.java.template", PutCommandGen.class), 
					CommandContext.Verb.DELETE, new MyData("/templates/command/delete/DeleteCommand.java.template", DeleteCommandGen.class) 
			);
	

	
	/**
	 * @param ctx
	 */
	public CommandGen(final IContext ctx) {

		Preconditions.checkArgument( ctx != null, "Context cannot be null");
		Preconditions.checkArgument( ctx.getSharedContext().isPresent(), "The shared context cannot be null. You called the method begin() ? ");
		Preconditions.checkArgument( !ctx.getCommandContexts().isEmpty(), "Command contexts cannot be empty. You called the beginCommand() ?");
		
		this.ctx = ctx;
		this.commandContexts = ctx.getCommandContexts();
		sharedContext = ctx.getSharedContext().get();
	}

	public void generate() throws IOException {

		// remove older files, if fileOverwrite is true
		for (final CommandContext commandContext : commandContexts.values()) {
			if (commandContext.getServerHolder().isPresent()) {
				new RestServerClassGen(ctx, commandContext, sharedContext.isFileOverwrite()).delete();
			}
		}
		
		// Instantiate a specific Generator for each context
		for (final CommandContext commandContext : commandContexts.values()) {
			
			generateClientRestService(commandContext);
			
			generateRestServerService(commandContext);
			
			
		}
	}

	private void generateClientRestService(final CommandContext commandContext) throws IOException {
		
		final AbstractCommandGen generator = buildGenerator(commandContext);
		
		generator.generate();
	}

	private void generateRestServerService(final CommandContext commandContext) throws IOException {
		final String contextId = commandContext.getContextId();
		
		final Optional<RestData> restData = Optional.fromNullable( restMap.get(contextId) );
		
		if (restData.isPresent() && commandContext.getServerHolder().isPresent()) {
			
			final Result result = new RestServerClassGen(ctx, commandContext, false).generate();
			
			new RestMethodGen(ctx, commandContext, result, restData.get().getMethodTemplateName(), restData.get().getMethodToBeAdded() ).generate();
		}
	}

	/**
	 * 
	 * @param commandContext
	 * @return GetCommandGen|PutCommandGen|DeleteCommandGen
	 */
	private final AbstractCommandGen buildGenerator(final CommandContext commandContext) {
		try {
			return unsafeBuildGenerator(commandContext);
		} catch(final Throwable t) {
			throw Throwables.propagate(t);
		}
	}
	
	private final AbstractCommandGen unsafeBuildGenerator(final CommandContext commandContext)
			throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		
		final MyData data = commandMap.get(commandContext.getRestVerb().get());
		
		final Constructor<? extends AbstractCommandGen> ctor = data.commandClazz.getConstructor(IContext.class, String.class, String.class);
		
		return ctor.newInstance( this.ctx, data.templateName, commandContext.getContextId() );
	}

	
	static class MyData {
		private final String templateName;
		private final Class<? extends AbstractCommandGen> commandClazz;
		
		public MyData(String templateName, Class<? extends AbstractCommandGen> commandClazz) {
			this.templateName = sanitize( templateName ).trim();
			this.commandClazz = commandClazz;
			
			Verify.verify(this.commandClazz != null, "The command class cannot be null");
			Verify.verify( !this.templateName.isEmpty(), "The template name cannot be null");
		}
	}
}
