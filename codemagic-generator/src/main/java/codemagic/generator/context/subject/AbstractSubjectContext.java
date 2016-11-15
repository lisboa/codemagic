package codemagic.generator.context.subject;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Sets;

import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.types.ArtefactyType;
import codemagic.generator.entrypoint.IContext;

public abstract class AbstractSubjectContext  {

	private static final Logger LOGGER = LoggerFactory.getLogger( AbstractSubjectContext.class );
	
	// imports, declarations, etc
	private final Set<String> imports = Sets.newHashSet();
	private final Set<String> fieldDecls = Sets.newHashSet();
	private final Set<String> methodDecls = Sets.newHashSet();
	
	/**
	 * @return The additional imports required by this context. For
	 *         example, if any buttons specify the nameToken, then is necessary
	 *         to import the NameTokens class. Warning: To prevents imports duplication,
	 *         the client should merge the you own import set with this one.
	 */
	public Set<String> getImports() {
		return imports;
	}

	/**
	 * @return The additional declaration required by this context. For example,
	 *         if any buttons specify the nameToken, then is necessary to
	 *         declare the PlaceManager. Warning: To prevents declaration
	 *         duplications, the client should merge the you own declaration set
	 *         with this one.
	 */
	public Set<String> getFieldDecls() {
		return fieldDecls;
	}

	/**
	 * 
	 * @return The method declaration needed to use this subject. The client template
	 *         should add this declarations. See the Orchestrator presenter
	 *         template, for an example.
	 */
	public Set<String> getMethodDecls() {
		return methodDecls;
	}

	/**
	 * Keep in sync the subject and capitalizedSubject 
	 */
	@Immutable
	public static class SubjectHolder {
		private final String subject;
		private final String capitelizedSubject;
		
		public SubjectHolder() {
			this("");
		}
		
		public SubjectHolder(final String subject) {
			this.subject = sanitize(subject).trim();
			this.capitelizedSubject = StringUtils.capitalize(this.subject);
		}

		public String getCapitalizedSubject() {
			return capitelizedSubject;
		}

		public String getSubject() {
			return subject;
		}
	}
	private final SharedContext sharedContext;

	private Map<String, Object> model = new HashMap<String, Object>();
	
	private SubjectHolder subjectHolder = new SubjectHolder();
	private String packageName = "";
	private String javaSourceFolder = "";
	private String computedTargetFolder = "";
	private String contextId = ""; // contextId as is
	private String capitalizedContextId = "";
	
	public AbstractSubjectContext(final String subject, final SharedContext sharedContext) {

		Preconditions.checkArgument( sharedContext != null, "Common context cannot be null");
		
		this.subjectHolder = new SubjectHolder(subject);
		this.sharedContext = sharedContext;
	}
	
	/**
	 * <pre>
	 *   Introduction
	 *   	1. Go back to {@link IContext}. This allows to navigate between subject context.
	 *   	2. Do the needed initializations  
	 *   
	 *   How to use it
	 *     1. begin[contextName]() => entering the subject context
	 *     2. end() => go back to {@link IContext} that give access to others contexts.
	 *   
	 *   Example:
	 *     new Context() 
	 *     	  .beginDisplay()
	 *           .setPackage(a.b.c)
	 *        .end()
	 *        .beginCommand()
	 *           .setPackage(x.y.z)
	 *           .setRestUrl("/svc/a/blacklists")
	 *        .end() 
	 *      
	 * </pre>
	 * @return
	 */
	public IContext end() {
		
		checkJavaSourceFolder();
		
		checkPacakgeName();
		
		getSharedContext().copyModelTo( model );
		
		computedTargetFolder = javaSourceFolder + File.separator + packageName.replace(".", File.separator);
		
		model.put("BaseObjectName",  buildBaseObjectName());
		
		createFoldeIfNotExists(computedTargetFolder);
		
		return sharedContext.getParent();
	}

	/**
	 * Syntax sugar to get the context parent.
	 * 
	 * @return The context parent.
	 */
	public IContext getParent() {
	    return sharedContext.getParent();	
	}
	
	
	public String getComputedTargetFolder() {
		return computedTargetFolder;
	}

	protected String getJavaSourceFolder() {
		return javaSourceFolder;
	}
	
	public Map<String, Object> getModel() {
		return model;
	}

	public String getPackageName() {
		return packageName;
	}

	public SharedContext getSharedContext() {
		return sharedContext;
	}

	public String getCapitalizedSubject() {
		return subjectHolder.getCapitalizedSubject();
	}

	protected void internalSetContextId(final String id) {
		contextId = sanitize(id).trim();
		capitalizedContextId = StringUtils.capitalize( this.contextId.toLowerCase() );
		checkContextId();
	}

	protected void checkContextId() {
		Preconditions.checkArgument( contextId != null && !contextId.isEmpty(), "The context id cannot be empty. You called the method %s.setContextId() ?", this.getClass().getSimpleName());
	}
	
	public String getContextId() {
		return contextId;
	}
	
	public String getCapitalizedContextId() {
		return capitalizedContextId;
	}

	protected void internalSetPackageName(final String packageName) {
		
		this.packageName = sanitize( packageName ).trim();
		
		Preconditions.checkArgument( !this.packageName.isEmpty() , "Package name cannot be null" );
		
		getModel().put("package", packageName);
	}

	protected void internalSetSubject(final String subject) {
		this.subjectHolder = new SubjectHolder(subject);
	}

	protected void internalSetJavaSourceFolder(final String javaSourceFolder) {
		
		final String removeSlash = StringUtils.removeEnd( sanitize( javaSourceFolder ).trim(), "\\" );
		this.javaSourceFolder =  StringUtils.removeEnd( removeSlash, "/");
		
		Preconditions.checkArgument( !this.javaSourceFolder.isEmpty() , "Java source folder cannot be null" );
	}
	
	  //~~~~~~~~~~~~~~~
	 // Utilities
	//~~~~~~~~~~~~~~~

	protected boolean createFoldeIfNotExists(final String targetFolder) {
		return new File( targetFolder ).mkdirs();
	}

	/**
	 * The base name used to generate the class and artifacts names:
	 * [baseNameForArtifacts] + [subsject].
	 * 
	 * <pre>
	 * Examples: 
	 * 
	 * 1) BlacklistDisplay  = (Blacklist + Display), 
	 * 2) BlacklistDataGrid = (Blacklist + DataGrid)
	 * </pre>
	 * 
	 * @return The base name used to build the class name of generated classes.
	 */
	protected String buildBaseObjectName() {
		return sharedContext.getBaseNameForGeneratedArtifacts() + subjectHolder.getCapitalizedSubject();
	}

	/**
	 * <pre>
	 * Examples:
	 * 1) BlacklistDisplay + View = BlacklistDisplayView
	 * 
	 * Where:
	 *   BlacklistDisplay is the name returned by {@link #buildBaseObjectName()} and
	 *   View is the className suffix for the artifact type. 
	 *  
	 * </pre>
	 * @param type The artifact type to be generated.
	 * @return The name of class name.
	 */
	public String buildClassName( final ArtefactyType type ) {
		return buildBaseObjectName() + type.getClassNameSuffix();
	}
	
	public String buildArtifactFileName( final ArtefactyType type ) {
		return buildClassName(type) + type.getFileSuffix();
	}

	public String buildFullClassName( final ArtefactyType type ) {
		return  getPackageName() + "." + buildBaseObjectName() + type.getClassNameSuffix();
	}
	
	public String buildArtifactFullFileName( final ArtefactyType type ) {
		
		return  computedTargetFolder + File.separatorChar + buildArtifactFileName(type);
	}
	

	/**
	 * 
	 * @param simpleFileName
	 *            The name without path. Is the last part of the full file name.
	 * @param suffix
	 *            The suffix to be added to the resultant file name. Examples:
	 *            '.java', '.xml', etc.
	 * @return If fileName is 'teste' and suffix is ".java", returns
	 *         'teste.java'
	 */
	public String buildFileName(final String simpleFileName, final String suffix) {
		return simpleFileName + suffix;
	}
	
	/**
	 * Use the {@link #computedTargetFolder} to compute the full file name. 
	 * 
	 * @param simpleFileName
	 * @param suffix
	 * @return
	 */
	public String buildFullFileName( final String simpleFileName, final String suffix ) {
		
		return  computedTargetFolder + File.separatorChar + buildFileName(simpleFileName, suffix);
	}
	
	
	private void checkJavaSourceFolder() {
		Verify.verify( !javaSourceFolder.isEmpty(), "[%s] The javaSourceFolder cannot be empty.", this.getClass().getName());
	}

	private void checkPacakgeName() {
		if (packageName.isEmpty()) {
			LOGGER.warn("The packageName is empty.");
		}
	}
}
