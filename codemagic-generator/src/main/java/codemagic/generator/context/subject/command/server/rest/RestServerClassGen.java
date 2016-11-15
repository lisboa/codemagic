package codemagic.generator.context.subject.command.server.rest;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import codemagic.generator.context.subject.AbstractGen;
import codemagic.generator.context.subject.command.CommandContext;
import codemagic.generator.context.subject.command.server.rest.etc.ServiceHolder;
import codemagic.generator.context.subject.command.server.rest.etc.Subcontext;
import codemagic.generator.entrypoint.IContext;

public class RestServerClassGen extends AbstractGen {

	private final CommandContext commandContext;
	private final ServiceHolder<?> serviceHolder;
	private final Subcontext serviceContext;
	
	public RestServerClassGen(final IContext ctx, final CommandContext commandContext) {
		this(ctx, commandContext, true);
		
	}
	
	public RestServerClassGen(final IContext ctx, final CommandContext commandContext, final boolean fileOverwrite) {
		super(ctx, fileOverwrite);
		Preconditions.checkArgument( commandContext != null, "The CommandContext cannot be null" );
		Preconditions.checkArgument( commandContext.getServerHolder().isPresent(), "The ServerHolder cannot be null. You called the beginCommand().beginServerRest() ?" );

		this.commandContext = commandContext;
		this.serviceHolder =  commandContext.getServerHolder().get();
		this.serviceContext = serviceHolder.getSubcontext();
		
		Verify.verify( serviceHolder != null, "The service context cannot be null" );
	}

	@Override
	public String getTemplateNameRelativeToResourceFolder() {
		return "templates/command/server/rest/restService.java.template";
	}

	@Override
	public Map<String, Object> getModel() {
		return serviceContext.getModel();
	}

	@Override
	public void populeModel() {
		Preconditions.checkArgument( serviceContext.getSharedContext().getTargetEntity().isPresent() ,  "The target Entity cannot be null. You called the method begin().setTargetEntity() before ?" );
		
		serviceContext.getModel().put("fullEntityClassName", serviceContext.getSharedContext().getTargetEntity().get().getName());
		serviceContext.getModel().put("EntityClassName", serviceContext.getSharedContext().getTargetEntity().get().getSimpleName());
		serviceContext.getModel().put("springRestUrl", commandContext.getSpringRestUrl());
	}

	@Override
	public String buildArtifactFullFileName() {
		return serviceHolder.getFullArtifactFileName();
	}
}
