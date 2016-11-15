package codemagic.generator.context.subject.datagrid.withasynprovider;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;
import codemagic.generator.context.subject.datagrid.withasynprovider.etc.ColumnProperty;
import codemagic.generator.context.subject.datagrid.withasynprovider.etc.GridFieldProperty;
import codemagic.generator.entrypoint.IContext;

public class DataGridContext extends AbstractSubjectContext implements IsSubjectContext<DataGridContext> {

	private Optional<ColumnProperty<DataGridContext>> checkColumnProperty = Optional.absent(); 
	
	private ImmutableList<GridFieldProperty> gridFields = ImmutableList.of();
	
	private ImmutableList<ColumnProperty<DataGridContext>> columnProperties = ImmutableList.of();
	private final Builder<ColumnProperty<DataGridContext>> columnPropertiesBuilder = ImmutableList.<ColumnProperty<DataGridContext>>builder();
	
	private final SubContext driverSubContext; 
	private final SubContext tableSubContext;
	private final SubContext toolbarSubContext;
	
	public DataGridContext (final SharedContext commonContext) {
		super("Grid", commonContext);
		
		// 1. DataGridDriver
		//    Note: The Driver type already exists in ArtifactType.
		this.driverSubContext = new SubContext( getCapitalizedSubject(), commonContext);
		
		// 2. DataGridTable
		this.tableSubContext = new SubContext( getCapitalizedSubject() + SubContextSubject.TABLE.getCaptalizedSubject(), commonContext);
		
		// 1. DataGridToolbar
		this.toolbarSubContext = new SubContext( getCapitalizedSubject() + SubContextSubject.TOOLBAR.getCaptalizedSubject(), commonContext);
		
	}

	public ColumnProperty<DataGridContext> beginCheckColum() {
		final ColumnProperty<DataGridContext> columnProperty = new ColumnProperty<DataGridContext>(this, false);
		checkColumnProperty = Optional.of(columnProperty);
		return columnProperty;
	}
	
	public ColumnProperty<DataGridContext> addColumn() { 
		final ColumnProperty<DataGridContext> columnProperty = new ColumnProperty<DataGridContext>(this);
		columnPropertiesBuilder.add(columnProperty);
		return columnProperty;
	}
	
	@Override
	public IContext end() {
		buildGridProperties();
		addGridpPropertiesInModel();
		addCheckColumnPropertyInModel();
		driverSubContext.end();
		tableSubContext.end();
		toolbarSubContext.end();
		return super.end();
	}

	@Override
	public DataGridContext setJavaSourceFolder(final String targetFolder) {

		internalSetJavaSourceFolder(targetFolder); // Only the validation is used here.
		
		// All subcontext have the same java source 
		driverSubContext.setJavaSourceFolder(targetFolder);
		tableSubContext.setJavaSourceFolder(targetFolder);
		toolbarSubContext.setJavaSourceFolder(targetFolder);
		
		return this;
	}

	@Override
	public DataGridContext setPackageName(final String packageName) {
		internalSetPackageName(packageName); // Only the validation is used here.
		
		// Each subcontext have a own package 
		driverSubContext.setPackageName( packageName + "." + SubContextSubject.DRIVER.subject );
		tableSubContext.setPackageName( packageName + "." + SubContextSubject.TABLE.subject );
		toolbarSubContext.setPackageName( packageName + "." + SubContextSubject.TOOLBAR.subject );
		
		return this;
	}
	
	
	public SubContext getDriverSubContext() {
		return driverSubContext;
	}

	public SubContext getTableSubContext() {
		return tableSubContext;
	}

	public SubContext getToolbarSubContext() {
		return toolbarSubContext;
	}
	
	public DataGridContext usePagination(final boolean usePagination) {
		tableSubContext.setUsePagination(usePagination);
		return this;
	}

	@Override
	public DataGridContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}

      //
     //  Types
	//
	public static enum SubContextSubject {
		DRIVER ("driver"),
		TABLE  ("table") ,
		TOOLBAR ("toolbar");
		
		private final String subject;

		private SubContextSubject(final String subject) {
			this.subject = subject;
		}

		public String getSubject() {
			return subject;
		}
		
		public String getCaptalizedSubject() {
			return StringUtils.capitalize(subject);
		}
	}
	
	/**
	 * <pre>
	 * 1. Necessary to reuse the artifact name logic for each subcontext. 
	 * 2. The subContext are: Driver, Table and Toolbar
	 * </pre>
	 *
	 */
	public static class SubContext  extends AbstractSubjectContext implements IsSubjectContext<SubContext> {

		private boolean usePagination = true;
		
		public SubContext(final String subject, final SharedContext commonContext) {
			super(subject, commonContext);
		}

		@Override
		public SubContext setJavaSourceFolder(String targetFolder) {
			internalSetJavaSourceFolder(targetFolder);
			return this;
		}

		@Override
		public SubContext setPackageName(final String packageName) {
			internalSetPackageName(packageName);
			return this;
		}

		public boolean isUsePagination() {
			return usePagination;
		}

		public void setUsePagination(final boolean usePagination) {
			this.usePagination = usePagination;
		}

		@Override
		public SubContext setContextId(final String id) {
			return this;
		}
	}

	  //
	 // Utilities
	//
	
	private void addGridpPropertiesInModel() {
		getModel().put("gridFields", gridFields);
		driverSubContext.getModel().put("gridFields", gridFields);
		tableSubContext.getModel().put("gridFields", gridFields);
		toolbarSubContext.getModel().put("gridFields", gridFields);
	}

	private void addCheckColumnPropertyInModel() {
		
		initCheckColumnPropertyIfNecessary();
		
		getModel().put("checkColumProperty", checkColumnProperty.get());
		driverSubContext.getModel().put("checkColumProperty", checkColumnProperty.get());
		tableSubContext.getModel().put("checkColumProperty", checkColumnProperty.get());
		toolbarSubContext.getModel().put("checkColumProperty", checkColumnProperty.get());
	}

	private void initCheckColumnPropertyIfNecessary() {
		if ( !checkColumnProperty.isPresent()) {
			final ColumnProperty<DataGridContext> defaultCheckColumn = new ColumnProperty<DataGridContext>(this);
			defaultCheckColumn.setColumnWidth("20px");
			checkColumnProperty = Optional.of(defaultCheckColumn);
		}
	}
	
	private void buildGridProperties() {
		
		final Builder<GridFieldProperty> builder = ImmutableList.<GridFieldProperty>builder();
		
		this.columnProperties = columnPropertiesBuilder.build();
		
		if (this.columnProperties.isEmpty()) {
			
			buildDefaultGridProperties(builder);
			
		} else {
			
			buildGridPropertiesFromColumn(builder);
		}
		
		this.gridFields = builder.build();
		
	}

	private void buildGridPropertiesFromColumn(final Builder<GridFieldProperty> builder) {
		for (final ColumnProperty<DataGridContext> c : this.columnProperties) {
			
			final Optional<FieldProperty> fieldProperty = getSharedContext().findFieldByName(c.getFieldName());
			
			Verify.verify(fieldProperty.isPresent(), "The fieldName %s is not found", c.getFieldName());
			
			builder.add(	new GridFieldProperty.Builder().setColumnProperty(c).setFieldProperty(fieldProperty.get()).build() );
		}
	}

	private void buildDefaultGridProperties(final Builder<GridFieldProperty> builder) {
		for (final FieldProperty p : getSharedContext().getFields()) {
			builder.add(	new GridFieldProperty.Builder().setFieldProperty(p).build() );
		}
	}

}
