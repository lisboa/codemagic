package codemagic.generator.context.shared;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableTable;

import codemagic.generator.context.shared.util.Name;

/**
 * <pre>
 * 1. The capitalized names are needed for performance, because 
 * 	   prevents to build the same names many times.
 * 2. The capitalizedFieldName is build from fieldName when the object is created. 
 *     Once the object is immutable, this fields are keep in sync. No problems here.
 * </pre>
 *
 */
@Immutable
public class FieldProperty implements IFieldProperty {

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldProperty.class);

	/**
	 * <pre>
	 * Keep a triple of values: 
	 * from => The original type 
	 * to   => The target type
	 * </pre>
	 * 
	 * Can be used, for example, to built a expression to convert the value
	 * returned by a component to a value of the underline field type.
	 * 
	 * Explanation
	 * 
	 * For example. Some databases (as Oracle), has no boolean values. The
	 * boolean should be represented by an integer. If, for example, we assume
	 * that 1 is true and any other values is false then we can use a checkbox
	 * (boolean values) to represent that field. To show the field, we should
	 * convert it to a boolean value. To get the value, we should to do the
	 * inverse. This information is used by the generator to converts from one
	 * type to another.
	 * 
	 * @param getterForComponentValue
	 * @return
	 */
	private static final ImmutableTable<FieldType, FieldType, ConversionHolder> conversionTable2 = ImmutableTable.<FieldProperty.FieldType, FieldProperty
			.FieldType, ConversionHolder> builder()

	.put(FieldType.BOOLEAN, FieldType.INTEGER, new ConversionHolder("ConversionUtil.booleanToInt"))

	.put(FieldType.INTEGER, FieldType.BOOLEAN, new ConversionHolder("ConversionUtil.intToBoolean"))

	.put(FieldType.STRING, FieldType.Double, new ConversionHolder("Double.parseDouble", "")) // has
																								// no
																								// import

	.put(FieldType.Double, FieldType.STRING, new ConversionHolder("String.valueOf", "")) // has
																							// no
																							// import

	.build();

	/**
	 * 
	 * Can be used like ${conversionHolder.left}xxx${conversionHolder.rigth}.
	 * 
	 * <code>
	 * Example:
	 * If 
	 *  	ConversionHolder conversionHolder = new ConversionHolder("String.valueOf");
	 * Then
	 * 		${conversionHolder.left}xxx${conversionHolder.rigth}
	 * Generate
	 *  	String.valueOf(xxx)
	 * </code>
	 * 
	 * Note: The conversion class, {@link #fullQualifiedFNameClass}, should be
	 * static. The default is {@value #DEFAULT_CONVERSION_CLASS_FULL_NAME}
	 *
	 */
	public static class ConversionHolder implements IConversionHolder {

		private static final String DEFAULT_CONVERSION_CLASS_FULL_NAME = "db.assist.view.common.common.codegeneration.support.util.ConversionUtil";

		private final String fullQualifiedFNameClass;
		private final String fName;
		private final String left;
		private final String right = ")";

		public ConversionHolder(final String fName) {
			this(fName, DEFAULT_CONVERSION_CLASS_FULL_NAME);
		}

		public ConversionHolder(final String fName, final String fullQualifiedFNameClass) {
			this.fName = sanitize(fName).trim();

			Verify.verify(!this.fName.isEmpty(), "The function used for conversion cannot be null");

			this.fullQualifiedFNameClass = sanitize(fullQualifiedFNameClass).trim();

			this.left = this.fName + "( ";
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see codemagic.generator.context.shared.IConersionHolder#getLeft()
		 */
		@Override
		public String getLeft() {
			return left;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see codemagic.generator.context.shared.IConersionHolder#getRight()
		 */
		@Override
		public String getRight() {
			return right;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see codemagic.generator.context.shared.IConersionHolder#getfName()
		 */
		@Override
		public String getfName() {
			return fName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see codemagic.generator.context.shared.IConersionHolder#
		 * getFullQualifiedFNameClass()
		 */
		@Override
		public String getFullQualifiedFNameClass() {
			return fullQualifiedFNameClass;
		}
	}

	public static class EmptyConversionHolder implements IConversionHolder {

		private static final EmptyConversionHolder INSTANCE = new EmptyConversionHolder();

		private EmptyConversionHolder() {
			/**/ }

		public static IConversionHolder getInstance() {
			return INSTANCE;
		}

		@Override
		public String getLeft() {
			return "";
		}

		@Override
		public String getRight() {
			return "";
		}

		@Override
		public String getfName() {
			return "";
		}

		@Override
		public String getFullQualifiedFNameClass() {
			return "";
		}
	}

	private final String fieldName;
	private final String capitalizedFieldName;
	private final boolean required;
	private final String title;
	private final ComponentType componentType;
	private final String componentName;
	private final String capitalizedCompName;
	private final String getterFieldPath;
	private final String setterFieldPath;
	private final String getter;
	private final String setter;
	private final FieldType fieldType;
	private final boolean readOnly;
	private final int maxLength;
	private final boolean enabled;
	private final String getterComponent;
	private final String getterForComponentValue;
	private String setterForComponentValue;
	private final IConversionHolder conversionFieldToComp;
	private final IConversionHolder conversionCompToField;
	private final String helpText;
	private final String inputName;

	public static enum FieldType {
		STRING("String", "get", ""), 
		BOOLEAN("boolean", "is", ""), 
		INTEGER("int", "get", ""), 
		LONG("long", "get", ""), 
		Double("double", "get", ""), 
		Date("Date","get", "java.util.Date"),
		CUSTOM ("<?>", "get", ""), // To be used when the real field is not specified here
		UNSET("<FieldProperty.FieldType is unseted>", "<getterPrefix is unseted>", ""),;

		private final String javaType;
		private final String getterPrefix;
		private final String fullTypeName;

		private FieldType(final String javaType, final String getterPrefix, final String fullTypeName) {
			this.javaType = javaType;
			this.getterPrefix = getterPrefix;
			this.fullTypeName = fullTypeName;
		}

		public String getJavaType() {
			return javaType;
		}

		/**
		 * @return The prefix used to build the getter name. for example, for
		 *         boolean fields, the prefix is "is"
		 */
		public String getGetterPrefix() {
			return getterPrefix;
		}

		/**
		 * 
		 * @return The full type name, if the import clause is required.
		 *         Otherwise, returns an empty string.
		 */
		public String getFullTypeName() {
			return fullTypeName;
		}
	}

	/**
	 * TODO: Migrate to AbstractWidget infrastructrure
	 * 
	 * Visual component used to show/input the field content
	 */
	public static enum ComponentType {
		TEXTBOX("TextBox", 
				"txt", 
				"setText(\"\")", 
				"getText()", 
				FieldType.STRING, 
				"setText",
				"org.gwtbootstrap3.client.ui.TextBox"),
		
		CHOSEN("ChosenListBox", 
				"chz", 
				"clear()", 
				"", 
				FieldType.STRING,
				"", 
				"com.arcbees.chosen.client.gwt.ChosenListBox"), 
		
		CHECKBOX("InlineCheckBox", 
				"chk",
				"setValue(false)", 
				"getValue()", 
				FieldType.BOOLEAN, 
				"setValue",
				"org.gwtbootstrap3.client.ui.InlineCheckBox");

		private final String javaType;
		private final String prefixName;
		private final String resetMethod;
		private final String getterForValue;
		private final FieldType heldValueType;
		private final String setterForValue;
		private final String fullQualifiedClassName;

		/**
		 * 
		 * @param javaType
		 *            The java type to be used in the java source code to
		 *            declare a component variable;
		 * @param prefixName
		 *            The prefix used to generate the componentName. For
		 *            example. If the fieldName is abc and prefix is txt, then
		 *            the componentName will be txtAbc.
		 * @param resetMethod
		 *            The java method used to reset the component.
		 * 
		 * @param getterForValue
		 *            The component method that return the current value.
		 * 
		 * @param heldValueType
		 *            The java type of the value held by the component. For
		 *            example, a checkbox held a boolean value. Note that this
		 *            type is the return value of {@link #getGetterForValue()}
		 *            and the input type for {@link #setterForValue}
		 * 
		 * @param setterForValue
		 *            The component method used to set the current value.
		 */
		private ComponentType(final String javaType, final String prefixName, final String resetMethod,
				final String getterForValue, final FieldType heldValueType, final String setterForValue,
				final String fullQualifiedClassName) {
			this.javaType = javaType;
			this.prefixName = prefixName;
			this.resetMethod = resetMethod;
			this.getterForValue = getterForValue;
			this.heldValueType = heldValueType;
			this.setterForValue = setterForValue;
			this.fullQualifiedClassName = fullQualifiedClassName;
		}

		public String getFullQualifiedClassName() {
			return fullQualifiedClassName;
		}

		public String getJavaType() {
			return javaType;
		}

		public String getPrefixName() {
			return prefixName;
		}

		public String getResetMethod() {
			return resetMethod;
		}

		public String getGetterForValue() {
			return getterForValue;
		}

		public FieldType getHeldValueType() {
			return heldValueType;
		}

		public String getSetterForValue() {
			return setterForValue;
		}
	}

	/**
	 * @param helpText
	 * @param fieldName
	 *            The entity field name. Can be simple as "projectName" or a
	 *            path like "environments.flgDev". Note that this field should
	 *            exists in the entity.
	 * @param required
	 * @param title
	 * @param componentType
	 *            [default is {@link ComponentType#TEXTBOX}]
	 *            {@link ComponentType} The type of field. Could be
	 *            {@link ComponentType#TEXTBOX}, {@link ComponentType#CHOSEN}.
	 *            This type defines the generated code.
	 * @param getterFieldPath
	 *            The full getter path for the fieldName. If not provided, it
	 *            will be generated using this rule: For fieldName
	 *            "enviroments.flgDev" returns getEnviroments().isFlgDev, if
	 *            flgDev is boolean or getEnviroments().getFlgDev, otherwise. If
	 *            not provided,
	 * @param fieldType
	 * @param readOnly
	 * @param maxLength
	 * @param enabled
	 * @param getterComponent
	 * @param setterFieldPath
	 *            getEnviroments().isFlgDev, if flgDev is boolean or
	 * @param showOnForm
	 * @param showOnGrid
	 * @param helpText
	 */
	private FieldProperty(final String fieldName, final boolean required, final String title,
			final ComponentType componentType, final String getterFieldPath, final FieldType fieldType,
			final boolean readOnly, final int maxLength, final boolean enabled, final String getterComponent,
			final String setterFieldPath, final String helpText) {

		this.fieldType = fieldType == null ? FieldType.STRING : fieldType;

		final Name name = new Name(fieldName, this.fieldType == FieldType.BOOLEAN);

		this.inputName = name.getInputName();

		this.fieldName = name.getUncapitalizedName();
		this.capitalizedFieldName = name.getCapitalizedName();
		this.required = required;
		this.title = buildTitle(title, this.capitalizedFieldName);
		this.componentType = componentType == null ? ComponentType.TEXTBOX : componentType;
		this.componentName = buildComponentName(this.capitalizedFieldName);
		this.capitalizedCompName = capitalize(this.componentName);
		this.getter = name.getGetter();
		this.setter = name.getSetter();
		this.getterFieldPath = buildGetterField(sanitize(getterFieldPath).trim(), name.getGetterFieldPath());
		this.setterFieldPath = buildSetterField(sanitize(setterFieldPath).trim(), name.getSetterFieldPath());
		this.getterComponent = buildGetterComponent(sanitize(getterComponent).trim());
		this.getterForComponentValue = buildGetterForComponentValue(this.getterComponent, this.componentType);
		this.setterForComponentValue = buildSetterForComponentValue(this.getterComponent, this.componentType);
		this.readOnly = readOnly;
		this.maxLength = maxLength;
		this.enabled = enabled;
		this.helpText = sanitize(helpText).trim();
		this.conversionFieldToComp = buildConversion(fieldType, componentType.heldValueType);
		this.conversionCompToField = buildConversion(componentType.heldValueType, fieldType);

		if (fieldType == FieldType.UNSET) {
			LOGGER.warn("The fieldType of field '{}' is '{}'", this.fieldName, this.fieldType);
		}
	}

	private IConversionHolder buildConversion(final FieldType fieldType, final FieldType compHeldValueType) {
		final ConversionHolder conversor = conversionTable2.get(fieldType, compHeldValueType);
		if (conversor == null) {
			return EmptyConversionHolder.getInstance();
		}
		return conversor;
	}

	// getter - setters

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getFieldType()
	 */
	@Override
	public FieldType getFieldType() {
		return fieldType;
	}

	@Override
	public IConversionHolder getConversionFieldToComp() {
		return conversionFieldToComp;
	}

	@Override
	public IConversionHolder getConversionCompToField() {
		return conversionCompToField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getInputName()
	 */
	@Override
	public String getInputName() {
		return inputName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return helpText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getFieldName()
	 */
	@Override
	public String getFieldName() {
		return fieldName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getGetter()
	 */
	@Override
	public String getGetter() {
		return getter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getSetter()
	 */
	@Override
	public String getSetter() {
		return setter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemagic.generator.context.shared.IFieldProperty#getCapitalizedFieldName
	 * ()
	 */
	@Override
	public String getCapitalizedFieldName() {
		return capitalizedFieldName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getComponentType()
	 */
	@Override
	public ComponentType getComponentType() {
		return componentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return componentName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemagic.generator.context.shared.IFieldProperty#getCapitalizedCompName(
	 * )
	 */
	@Override
	public String getCapitalizedCompName() {
		return capitalizedCompName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemagic.generator.context.shared.IFieldProperty#getGetterFieldPath()
	 */
	@Override
	public String getGetterFieldPath() {
		return getterFieldPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemagic.generator.context.shared.IFieldProperty#getGetterComponent()
	 */
	@Override
	public String getGetterComponent() {
		return getterComponent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#getMaxLength()
	 */
	@Override
	public int getMaxLength() {
		return maxLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see codemagic.generator.context.shared.IFieldProperty#
	 * getGetterForComponentValue()
	 */
	@Override
	public String getGetterForComponentValue() {
		return getterForComponentValue;
	}

	@Override
	public String getSetterForComponentValue() {
		return setterForComponentValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * codemagic.generator.context.shared.IFieldProperty#getSetterFieldPath()
	 */
	@Override
	public String getSetterFieldPath() {
		return setterFieldPath;
	}

	// Utilities

	private String buildComponentName(final String capitalizedFieldName) {
		return componentType.prefixName + capitalizedFieldName;
	}

	private String buildGetterField(final String getterField, final String getterFieldPath) {
		return getterField.isEmpty() ? getterFieldPath : getterField;
	}

	private String buildSetterField(final String setterField, final String setterFieldPath) {
		return setterField.isEmpty() ? setterFieldPath : setterField;
	}

	private String buildGetterComponent(final String getterComponent) {
		return getterComponent.isEmpty() ? buildDefaultGetterComponentName() : getterComponent;
	}

	private String buildDefaultGetterComponentName() {
		return "get" + capitalizedCompName;
	}

	private String buildGetterForComponentValue(final String getterComponent, final ComponentType componentType) {
		return getterComponent + "()." + componentType.getterForValue;
	}

	private String buildSetterForComponentValue(final String getterComponent, final ComponentType componentType) {
		return getterComponent + "()." + componentType.setterForValue;
	}

	private String buildTitle(final String title, final String capitalizedFieldName) {
		final String _title = sanitize(title).trim();

		// If empty, use the field name to generate the title.
		// Example: fieldName => Field Name
		if (_title.isEmpty()) {
			return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(capitalizedFieldName), ' ');
		} else {
			return _title;
		}
	}

	/**
	 * Used in the middle names. For example:
	 * 
	 * <pre>
	 *   1. getTxtName() = get + TxtName()
	 * </pre>
	 * 
	 * @param fieldName
	 * @return
	 */
	private String capitalize(final String fieldName) {
		return StringUtils.capitalize(fieldName);
	}

	public static class Builder {
		private String fieldName = "";
		private boolean required = false;
		private String title = "";
		private ComponentType componentType = ComponentType.TEXTBOX;
		private FieldType fieldType = FieldType.UNSET;
		private String getterField = "";
		private boolean readOnly = false;
		private int maxLength = 80;
		private boolean enabled = true;
		private String getterComponent = "";
		private String setterField = "";
		private String helpText = "";

		public FieldProperty build() {
			return new FieldProperty(fieldName, required, title, componentType, getterField, fieldType, readOnly,
					maxLength, enabled, getterComponent, setterField, helpText); // NOPMD
		}

		public Builder setFieldName(final String fieldName) {
			this.fieldName = fieldName;
			return this;
		}

		public Builder setRequired(final boolean required) {
			this.required = required;
			return this;
		}

		public Builder setTitle(final String title) {
			this.title = title;
			return this;
		}

		/**
		 * [default is {@link ComponentType#TEXTBOX}]
		 * 
		 * @param componentType
		 * @return
		 */
		public Builder setComponentType(final ComponentType componentType) {
			this.componentType = componentType;
			return this;
		}

		public Builder setFieldType(final FieldType fieldType) {
			this.fieldType = fieldType;
			return this;
		}

		/**
		 * Needed only for non standard getter name. That is, for non compliance
		 * java bean names.
		 * 
		 * @param getterField
		 *            The field getter name. If empty, a java bean name from
		 *            fieldName will be builded.
		 */
		public Builder setGetterField(final String getterField) {
			this.getterField = getterField;
			return this;
		}

		public Builder setSetterField(final String setterField) {
			this.setterField = setterField;

			return this;
		}

		public Builder setGetterComponent(String getterComponent) {
			this.getterComponent = getterComponent;
			return this;
		}

		public Builder setReadOnly(final boolean readOnly) {
			this.readOnly = readOnly;

			return this;
		}

		public Builder setMaxLength(final int maxLength) {
			this.maxLength = maxLength;

			return this;
		}

		public Builder setEnabled(final boolean enabled) {
			this.enabled = enabled;

			return this;
		}

		/**
		 * Needed only for non standard getter names. That is, for non
		 * compliance java bean names.
		 * 
		 * @param getterComponent
		 *            The component getter name. If empty, a java bean name from
		 *            {@link #fieldName} will be builded.
		 */
		public Builder setGetterComponentName(final String getterComponent) {
			this.getterComponent = getterComponent;

			return this;
		}

		/**
		 * @see getHelpText()
		 */
		public Builder setHelpText(final String helpText) {
			this.helpText = StringEscapeUtils.escapeXml11(helpText);
			return this;
		}
	}
}
