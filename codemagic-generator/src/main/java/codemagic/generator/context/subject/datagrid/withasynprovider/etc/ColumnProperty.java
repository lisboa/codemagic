package codemagic.generator.context.subject.datagrid.withasynprovider.etc;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

public class ColumnProperty<P> {
	private final P parent;
	private final boolean fieldNameRequired;
	
	private String columnWidth = "";
	private String fieldName = "";

	public ColumnProperty(final P parent) {
		this(parent, true);
	}
	
	public ColumnProperty(final P parent, boolean fieldNameRequired) {
		Preconditions.checkArgument( parent != null, "Parent cannot be null" );
		this.parent = parent;
		this.fieldNameRequired = fieldNameRequired;
	}



	/**
	 * 
	 * @return [default=true] true if the fieldName can be empty. The only case
	 *         where this field is false is when it is used in the check box column of data
	 *         grid (to select rows). In this case, the field name is not required.
	 */
	public boolean isFieldNameRequired() {
		return fieldNameRequired;
	}

	public String getFieldName() {
		return fieldName;
	}


	public ColumnProperty<P> setFieldName(final String fieldName) {
		this.fieldName = sanitize(fieldName).trim();
		return this;
	}


	public ColumnProperty<P> setColumnWidth(final String columnWidth) {
		this.columnWidth = sanitize(columnWidth).trim();
		return this;
	}


	public String getColumnWidth() {
		return columnWidth;
	}
	
	public P end() {
		checkFieldName();
		return parent;
	}

	
      //
	 // Utilities
	//
	private void checkFieldName() {
		if (fieldNameRequired) {
			Verify.verify( !this.fieldName.isEmpty(), "FieldName  cannot be null. You called the method setFieldName() ? ");
		}
	}
}
