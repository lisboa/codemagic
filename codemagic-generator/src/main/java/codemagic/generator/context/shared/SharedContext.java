package codemagic.generator.context.shared;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import codemagic.generator.context.shared.FieldProperty.FieldType;
import codemagic.generator.context.shared.urlparam.UrlParam;
import codemagic.generator.context.shared.urlparam.UrlParamList;
import codemagic.generator.context.shared.urlparam.contract.IUrlParamList;
import codemagic.generator.context.shared.util.ClassNameHolder;
import codemagic.generator.entrypoint.IContext;


public class SharedContext {
	
	private final IContext parent;
	private String baseNameForGeneratedArtifacts = "";
	private Optional<ClassNameHolder> targetEntity = Optional.absent();
	private ImmutableList<FieldProperty> fields = ImmutableList.of();
	private IUrlParamList urlParamLlist;
	private final UrlParamList.Builder urlParamListBuilder = new UrlParamList.Builder();
	private String singularItemLabel = "";
	private String plutalItemLabel = "";
	private final Set<String> componentTypeImports = Sets.newHashSet();
	private boolean fileOverwrite =true;
	
	
	public SharedContext(final IContext parent) {
		
		Preconditions.checkArgument( parent != null, "The parent context cannot be null" );
		
		this.parent = parent;
	}

	public IContext end() {
		checkBaseName();
		initUrlParamList();
		initItemLabel();
		buildComponentTypeImports();
		return parent;
	}


	private void buildComponentTypeImports() {
		componentTypeImports.clear();
		for (final FieldProperty p : fields) {
			componentTypeImports.add(p.getConversionCompToField().getFullQualifiedFNameClass());
			componentTypeImports.add(p.getConversionFieldToComp().getFullQualifiedFNameClass());
		}
		
	}

	
	public boolean isFileOverwrite() {
		return fileOverwrite;
	}

	public Set<String> getComponentTypeImports() {
		return componentTypeImports;
	}

	/**
	 * @see #getSingularItemLabel()
	 */
	private void initItemLabel() {
		if (singularItemLabel.isEmpty()) {
			singularItemLabel = baseNameForGeneratedArtifacts + " item"; 
		}
		
		// Init with navy plural
		if (plutalItemLabel.isEmpty()) {
			plutalItemLabel = singularItemLabel + "s"; 
		}
	}

	private IUrlParamList initUrlParamList() {
		return urlParamLlist = urlParamListBuilder.build();
	}
	
	public IContext getParent() {
		return parent;
	}
	
	/**
	 * 
	 * @return A name used in messages showed to the user. For example, if
	 *         itemName=='rule': "A new rule was successful created". If none
	 *         provided, the default is
	 *         {@link #getBaseNameForGeneratedArtifacts()} + " item". In this
	 *         case, for a "Blacklist" base name, the message would be
	 *         "A new blacklist item was successful created"
	 */
	public String getSingularItemLabel() {
		return singularItemLabel;
	}

	
	/**
	 * A plural for {@link #getSingularItemLabel()}
	 * @return
	 */
	public String getItemLabels() {
		return plutalItemLabel;
	}

	/**
	 * Copy the SharedContext data to destination model.
	 * Should be called by any AbstractContext.init()
	 * @param destModel
	 */
	public void copyModelTo( Map<String, Object> destModel  ) {
		destModel.put("fields", fields);
		destModel.put("NL",'\n'); // New Line
		
		if (targetEntity.isPresent()) { 
			destModel.put("TargetEntityName",  targetEntity.get().getSimpleName());
			destModel.put("fullTargetEntityName",  targetEntity.get().getName());
			destModel.put("requestParamHolder",  urlParamLlist);
			destModel.put("baseNameForGeneratedArtifacts",  baseNameForGeneratedArtifacts);
			destModel.put("itemLabel", singularItemLabel);
			destModel.put("itemLabels", plutalItemLabel);
		} else {
			destModel.put("TargetEntityName",  "/*Add <TargetEntity> class into SharedContext*/");
			destModel.put("fullTargetEntityName",  "/*Add <TargetEntity> class into SharedContext*/");
		}
	}
	
	public SharedContext defineFromRequest(final String paramName) {
			return defineFromRequest(paramName, true, "");
	}
	
	public SharedContext defineFromRequest(final String paramName, final boolean initTargetEntity) {
		return defineFromRequest(paramName, initTargetEntity, "");
	}
	
	public SharedContext defineFromRequest(final String paramName, final boolean initTargetEntity, final String targetEntityField) {
		final FieldProperty prop = new FieldProperty.Builder().setFieldName(paramName).setFieldType(FieldType.STRING).setRequired(true).build();
		urlParamListBuilder.addRequestParam( new UrlParam(prop, initTargetEntity, targetEntityField) );
		return this;
	}
	  //~~~~~~~~~~~~~~~
	 // getters / setters
	//~~~~~~~~~~~~~~~

	
	public ImmutableList<FieldProperty> getFields() {
		return fields;
	}

	public SharedContext setFields(final ImmutableList<FieldProperty> fields) {
		
		this.fields = fields == null ? ImmutableList.<FieldProperty>of() : fields;
		
		Preconditions.checkArgument( !this.fields.isEmpty(), "At least one field should exists");
		
		return this;
	}
	
	/**
	 * @return The base name used to build the names of generated artifacts:
	 *         [BaseName]Commands, [BaseName]Drivers, [BaseName]Displays, [BaseName]DataGrids, etc.
	 */
	public String getBaseNameForGeneratedArtifacts() {
		return baseNameForGeneratedArtifacts;
	}
	
	public SharedContext setBaseNameForGeneratedArtifacts(final String baseNameOfGeneratedCode) {
		this.baseNameForGeneratedArtifacts = sanitize(baseNameOfGeneratedCode).trim();
		
		Verify.verify( !this.baseNameForGeneratedArtifacts.isEmpty() , "Base name cannot be null. It will be used to generate the names of the artifacts: Command, Driver, Display, etc." );
		
		return this;
	}
	
	public SharedContext setSingularItemLabel(final String singularItemLabel) {
		this.singularItemLabel = sanitize(singularItemLabel).trim();
		return this;
	}

	public SharedContext setPlutalItemLabel(String pluralItemLabel) {
		this.plutalItemLabel = sanitize(pluralItemLabel).trim();
		return this;
	}
	
	
	public Optional<ClassNameHolder> getTargetEntity() {
		return targetEntity;
	}

	public SharedContext setTargetEntity(final String fullQualifiedClassName) {
		this.targetEntity =  Optional.of(new ClassNameHolder(fullQualifiedClassName));
		
		return this;
	}

	public SharedContext setFileOverwrite(final boolean fileOverwrite) {
		this.fileOverwrite = fileOverwrite;
		return this;
	}

	
	
	/**
	 * 
	 * @return All url parameter defined in the begin() section. Note that a
	 *         service can be use none or a sub-set of this parameters,
	 *         according with the restUrl. Therefore, use this list to build a
	 *         full list, for example, in the prepareFromRequest method I need
	 *         to process all parameter (See OrchestratorPresenter template).
	 *         Otherwise, use the parameters from restUrl.
	 */
	public IUrlParamList getUrlParamList() {
		return urlParamLlist;
	}
	
	  //
	 // Utilities
	//
	
	private void checkBaseName() {
		Verify.verify( !baseNameForGeneratedArtifacts.isEmpty(), "The base name used to generate the artifact names cannot be empy. You called the method begin().setBaseNameForGeneratedArtifacts(..) ?");
		
	}
	
	public Optional<FieldProperty> findFieldByName(final String fieldName) {
        for (final FieldProperty p : fields) {
        	if (p.getInputName().equals(fieldName)) {
        		return Optional.of(p);
        	}
        }
		return Optional.absent();
	}

}
