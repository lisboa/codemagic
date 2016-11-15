package codemagic.generator.context.subject.display;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Verify;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.SharedContext;
import codemagic.generator.context.subject.AbstractSubjectContext;
import codemagic.generator.context.subject.IsSubjectContext;
import codemagic.generator.entrypoint.IContext;

public class DisplayContext extends AbstractSubjectContext implements IsSubjectContext<DisplayContext>{

	private String formName = "";
	private String capitalizedFormName = "";
	
	private String newModeTitle = "Creating a new entity";
	private String editingModeTitle = "Editing a entity";
	
	private final List<FieldProperty> mainFields = Lists.newArrayList();
	private final List<FieldProperty> moreFields = Lists.newArrayList();
	private final List<FieldProperty> displayFields = Lists.newArrayList();

	public DisplayContext (final SharedContext commonContext) {
		super("Display", commonContext);
	}
	
	// Note: The imports are not put in model here. This should be made by the
	// generator, because it can merge imports from different contexts to
	// compute the final import list.
	@Override
	public IContext end() {
		computeDisplayFields();
		computeImports();
		configureFormName();
		putMainFieldsInModel();
		putMoreFieldsInModel();
		putDisplayFieldsInModel();
		return super.end();
	}
	
	private void computeDisplayFields() {
		
		displayFields.clear();
		
		if (mainFields.isEmpty()) {
			// The sharedContext.fields itself
			displayFields.addAll( getSharedContext().getFields() );
		} else {
			// The join between main and more fields
			displayFields.addAll( Lists.newArrayList( Iterables.concat(mainFields, moreFields)) );
		}
		
	}

	/** Should be raun after the {@link #computeDisplayFields()} */
	private void computeImports() {
		for (final FieldProperty p : displayFields) {
			getImports().add( p.getComponentType().getFullQualifiedClassName());
		}
	}

	private void putDisplayFieldsInModel() {
		getModel().put("displayFields", displayFields);
	}

	private void putMoreFieldsInModel() {
		getModel().put("moreFields", moreFields);
	}

	private void putMainFieldsInModel() {
		getModel().put("mainFields", mainFields); 
	}

	public List<FieldProperty> getDisplayFields() {
		return displayFields;
	}

	public List<FieldProperty> getMainFields() {
		return mainFields;
	}

	public List<FieldProperty> getMoreFields() {
		return moreFields;
	}

	private void configureFormName() {
		
		this.formName = buildFormName( getSharedContext().getBaseNameForGeneratedArtifacts()  );
		this.capitalizedFormName = StringUtils.capitalize(this.formName);

		getModel().put("formName", this.formName);
		getModel().put("FormName", this.capitalizedFormName);
		getModel().put("FormType", "Form");
	}

	 private String buildFormName(final String sanitizedEntityName) {
		 return "form" + sanitizedEntityName;
	 }

	@Override
	public DisplayContext setPackageName(final String packageName) {
		internalSetPackageName(packageName);
		return this;
	}

	@Override
	public DisplayContext setJavaSourceFolder(final String targetFolder) {
		internalSetJavaSourceFolder(targetFolder);
		return this;
	}

	@Override
	public DisplayContext setContextId(final String id) {
		internalSetContextId(id);
		return this;
	}
	
	
	/**
	 * 
	 * @return The title to be showed when the display is in new mode (that is,
	 *         creating a new entity)
	 */
	public String getNewModeTitle() {
		return newModeTitle;
	}

	/**
	 * 
	 * @return The title to be showed when the display is in editing mode.
	 */
	public String getEditingModeTitle() {
		return editingModeTitle;
	}

	public DisplayContext setEditingModeTitle(final String editingModelTitle) {
		this.editingModeTitle = sanitize(editingModelTitle).trim();
		return this;
	}

	public DisplayContext setNewModeTitle(final String newTitle) {
		this.newModeTitle = sanitize(newTitle).trim();
		return this;
	}

	public DisplayContext addMainField(final String fieldName) {
		final Optional<FieldProperty> field = getSharedContext().findFieldByName(fieldName) ;
		
		Verify.verify( field.isPresent(), "The field %s is unknown. You called the begin().setFields(fields) ? The fields contain a field with the name %s ? ", fieldName, fieldName );

		mainFields.add(field.get() );
			
		return this;
	}

	public DisplayContext addMoreField(final String fieldName) {
		
		final Optional<FieldProperty> field = getSharedContext().findFieldByName(fieldName) ;
		
		Verify.verify( field.isPresent(), "The field %s is unknown. You called the begin().setFields(fields) ? The fields contain a field with the name %s ? ", fieldName, fieldName );

		moreFields.add(field.get() );
			
		return this;
	}
}
