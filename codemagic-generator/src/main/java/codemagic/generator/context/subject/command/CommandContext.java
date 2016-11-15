package codemagic.generator.context.subject.command;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.shared.util.ClassNameHolder;
import codemagic.generator.context.shared.util.ContextUtil;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;
import codemagic.generator.context.subject.command.server.rest.etc.IServiceHolder;
import codemagic.generator.context.subject.command.server.rest.etc.ServiceHolder;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public class CommandContext extends AbstractSubjectContext implements IsSubjectContext<CommandContext> {

	private Optional<ServiceHolder<CommandContext>> serverHolder = Optional.absent();
	private String restUrl = "";
	private Optional<Verb> restVerb = Optional.absent();
	private String commandName = "";
	private String capitalizedCommandName = "";
	private Optional<ClassNameHolder> targetResult = Optional.absent();
	private String qualifiedInputClassName = "";
	private String inputClassName = ""; // unqualified input class name. Builded from qualifiedInputClassName

	public static enum Verb {
		GET ("get"), 
		PUT ("put"),
		POST ("post"),
		DELETE ("put"); // delete verbs _does_ not allows request payload.
		
		private final String verbAsStr;

		private Verb(String commandPrefix) {
			this.verbAsStr = commandPrefix;
		}

		public String getVerbAsStr() {
			return verbAsStr;
		}
	}
	
	public CommandContext( final SharedContext commonContext ) {
		super("Command", commonContext);
	}

	@Override
	public IContext end() {
		checkContextId();
		addThisToContextMap();
		checkRestUrl();
		checkRestVerb();
		addCommandNameInModel();
		addVerbInModel();
		addClassNameInModel();
		// Note: The restUrl is added to model by the commandGenerator, 
		// because it requires extra processing.
		return super.end();
	}

	protected void addThisToContextMap() {
		
		Preconditions.checkArgument( ! getParent().getCommandContexts().containsKey(getContextId()), "There is aready a command with the id %s. Please, choose anther contextId", getContextId());
		
		getParent().getCommandContexts().put(getContextId(), this);
	}

	
	  //
	 // getters - setters
	//
	
	
	public String getRestUrl() {
		return restUrl;
	}

	/**
	 * The generator use the ${variableName} to represents a template variable.
	 * But the spring use {variable}, without the $ (dollar) prefix.
	 * 
	 * @return
	 */
	public String getSpringRestUrl() {
		return restUrl.replaceAll("\\$", "");
	}
	
	public Optional<ServiceHolder<CommandContext>> getServerHolder() {
		return serverHolder;
	}

	public CommandContext setRestUrl(final String restUrl) {
		this.restUrl = sanitize(restUrl).trim();
		return this;
	}
	
	public Optional<Verb> getRestVerb() {
		return restVerb;
	}

	public CommandContext setRestVerb(final Verb restVerb) {
		this.restVerb = Optional.fromNullable(restVerb);
		return this;
	}

	@Override
	public CommandContext setJavaSourceFolder(final String targetFolder) {
		internalSetJavaSourceFolder(targetFolder);
		return this;
	}

	@Override
	public CommandContext setPackageName(String packageName) {
		internalSetPackageName(packageName);
		return this;
	}
	
	public String getQualifiedInputClassName() {
		return qualifiedInputClassName;
	}

	public CommandContext setFullQualifiedInputClassName(final String targetQualifiedInputClassName) {
		this.qualifiedInputClassName = sanitize( targetQualifiedInputClassName ).trim();
		this.inputClassName = ContextUtil.getSimpleClassName(this.qualifiedInputClassName);
		
		return this;
	}

	public String getInputClassName() {
		return inputClassName;
	}

	public Optional<ClassNameHolder> getTargetResult() {
		return targetResult;
	}

	public CommandContext setFullQualifiedResultClassName(final String fullQualifiedClassName) {
		this.targetResult = Optional.of(new ClassNameHolder(fullQualifiedClassName));
		return this; 
	}
	
	public String getCommandName() {
		return this.commandName;
	}
	
	public String getFullCommandName() {
		return  this.getPackageName() + "." + this.capitalizedCommandName;
	}

	public String getPrefix() {
		return StringUtils.capitalize( restVerb.get().getVerbAsStr() );
	}

	public String getCapitalizedCommandName() {
		return this.capitalizedCommandName;
	}

	/**
	 * Add the prefix Get, Save or Delete to generated class name
	 */
	@Override
	protected String buildBaseObjectName() {
		return  getCapitalizedContextId() + super.buildBaseObjectName();
	}
	
	@Override
	public CommandContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

	public IServiceHolder<CommandContext> beginServerRest() {
		
		Preconditions.checkArgument( !serverHolder.isPresent() , "There is already a Server for this context. Maybe you called the beginServer() two times for some beginCommand(), right ?");
		
		serverHolder = Optional.of( new ServiceHolder<CommandContext>(this, "", getSharedContext()));
		
		return serverHolder.get();
	}

	  //
	 // Utilities
	//

	private void addVerbInModel() {
		getModel().put("httpVerb", restVerb.get().verbAsStr);
	}
	
	private void addClassNameInModel() {
		getModel().put("InputClassName", inputClassName);
		getModel().put("fullInputClassName", qualifiedInputClassName);
	}

    // should be called after the checkVerb
	private void addCommandNameInModel() {
		this.capitalizedCommandName = buildClassName(ArtefactyType.COMMAND);
		this.commandName = StringUtils.uncapitalize(this.capitalizedCommandName);
		getModel().put("commandName", this.commandName);
		getModel().put("CommandName", this.capitalizedCommandName);
	}

	private void checkRestUrl() {
		Preconditions.checkArgument( !this.restUrl.isEmpty(), "RestUrl is required. Call the method CommandContext.setRestUrl(..)");
	}
	
	private void checkRestVerb() {
		Verify.verify( restVerb.isPresent(), "The command verb cannot be null. Should be %s, %s, %s or %s. Call the method CommandContext.setVerb(..)", Verb.GET, Verb.DELETE, Verb.POST, Verb.PUT);
	}

}
