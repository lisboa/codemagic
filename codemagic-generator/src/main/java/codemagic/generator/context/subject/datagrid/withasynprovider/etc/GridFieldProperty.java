package codemagic.generator.context.subject.datagrid.withasynprovider.etc;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.FieldProperty.ComponentType;
import codemagic.generator.context.shared.FieldProperty.FieldType;
import codemagic.generator.context.shared.IConversionHolder;
import codemagic.generator.context.shared.IFieldProperty;

/**
 * Add specific grid fields like column width and text positioning.
 *
 */
public class GridFieldProperty implements IFieldProperty {

	private final FieldProperty fieldProperty;
	private final Optional<ColumnProperty<?>> columnProperty;
	
	
	private GridFieldProperty(final FieldProperty fieldProperty, final ColumnProperty<?> columnProperty) {
		Preconditions.checkArgument( fieldProperty != null, "FieldProperty cannot be null");
		this.fieldProperty = fieldProperty;
		this.columnProperty = Optional.<ColumnProperty<?>>fromNullable(columnProperty);
	}
	
	public Optional<ColumnProperty<?>> getColumnProperty() {
		return columnProperty;
	}

	public String getColumnWidth() {
		if (columnProperty.isPresent()) {
			return columnProperty.get().getColumnWidth();
		} else {
			return "";
		}
	}
	
	@Override
	public FieldType getFieldType() {
		return fieldProperty.getFieldType();
	}

	@Override
	public String getInputName() {
		return fieldProperty.getInputName();
	}

	@Override
	public String getHelpText() {
		return fieldProperty.getHelpText();
	}

	@Override
	public String getFieldName() {
		return fieldProperty.getFieldName();
	}

	@Override
	public String getGetter() {
		return fieldProperty.getGetter();
	}

	@Override
	public String getSetter() {
		return fieldProperty.getSetter();
	}

	@Override
	public String getCapitalizedFieldName() {
		return fieldProperty.getCapitalizedFieldName();
	}

	@Override
	public boolean isRequired() {
		return fieldProperty.isRequired();
	}

	@Override
	public String getTitle() {
		return fieldProperty.getTitle();
	}

	@Override
	public ComponentType getComponentType() {
		return fieldProperty.getComponentType();
	}

	@Override
	public String getComponentName() {
		return fieldProperty.getComponentName();
	}

	@Override
	public String getCapitalizedCompName() {
		return fieldProperty.getCapitalizedCompName();
	}

	@Override
	public String getGetterFieldPath() {
		return fieldProperty.getGetterFieldPath();
	}

	@Override
	public String getGetterComponent() {
		return fieldProperty.getGetterComponent();
	}

	@Override
	public boolean isReadOnly() {
		return fieldProperty.isReadOnly();
	}

	@Override
	public int getMaxLength() {
		return fieldProperty.getMaxLength();
	}

	@Override
	public boolean isEnabled() {
		return fieldProperty.isEnabled();
	}

	@Override
	public String getGetterForComponentValue() {
		return fieldProperty.getGetterForComponentValue();
	}

	@Override
	public String getSetterFieldPath() {
		return fieldProperty.getSetterFieldPath();
	}

	public static class Builder {
		private FieldProperty fieldProperty = null;
		private ColumnProperty<?> columnProperty = null;

		public GridFieldProperty build() {
			return new GridFieldProperty(fieldProperty, columnProperty);
		}
		
		public Builder setFieldProperty(final FieldProperty fieldProperty) {
			this.fieldProperty = fieldProperty;
			return this;
		}

		public Builder setColumnProperty(final ColumnProperty<?> columnProperty) {
			this.columnProperty = columnProperty;
			return this;
		}
		
	}

	@Override
	public IConversionHolder getConversionFieldToComp() {
		return fieldProperty.getConversionFieldToComp();
	}

	@Override
	public IConversionHolder getConversionCompToField() {
		return fieldProperty.getConversionCompToField();
	}

	@Override
	public String getSetterForComponentValue() {
		return fieldProperty.getSetterForComponentValue();
	}
}
