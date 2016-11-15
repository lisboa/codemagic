package codemagic.generator.context.shared;

import codemagic.generator.context.shared.FieldProperty.ComponentType;
import codemagic.generator.context.shared.FieldProperty.FieldType;
import codemagic.generator.context.shared.util.Name;

public interface IFieldProperty {

	IConversionHolder getConversionFieldToComp();

	IConversionHolder getConversionCompToField();
	
	/**
	 * 
	 * @return An enumeration indicating the java type of the field. The
	 *         corresponding java type is {@link FieldType#javaType}
	 */
	FieldType getFieldType();

	/**
	 * @See {@link Name#getInputName()}
	 * @return 
	 */
	String getInputName();

	/**
	 * <pre>
	 * There are two templates for each component: 
	 * 
	 * 1) [templateName].ui.xml (when there is not help, that is, this getter returns an empty string)
	 * 2) [templateName]WithHelp.ui.xml  (otherwise)
	 * </pre>
	 * 
	 * Important: The xml invalid chars are skip. For example, " is mapped to
	 * &quote;, etc. Therefore, is safe to use any chars.
	 * 
	 * @return [Optional] The text to be showed when the user pass the mouse
	 *         over a question icon put at right side of the component.
	 */
	String getHelpText();

	/**
	 * 
	 * @return The field name of java entity. This name is used to build getter,
	 *         setters and entities.
	 */
	String getFieldName();

	/**
	 * @See {@link Name#getGetter()}
	 * 
	 * @return
	 */
	String getGetter();

	/**
	 * @See {@link Name#getSetter()}
	 * @return
	 */
	String getSetter();

	/**
	 * @see Name#getCapitalizedName()
	 * @return
	 */
	String getCapitalizedFieldName();

	boolean isRequired();

	String getTitle();

	ComponentType getComponentType();

	String getComponentName();

	String getCapitalizedCompName();

	/**
	 * @See {@link Name#getGetterFieldPath()}
	 * @return
	 */
	String getGetterFieldPath();

	/**
	 * This getter is used to generate the MyView interface members, MyView
	 * implementation and presenter uses.
	 * 
	 * @return The getter to get a reference to the component. For example. For
	 *         the component txtCompName, return getTxtCompName().
	 */
	String getGetterComponent();

	boolean isReadOnly();

	int getMaxLength();

	boolean isEnabled();

	String getGetterForComponentValue();
	
	String getSetterForComponentValue();

	/**
	 * @See {@link Name#getSetterFieldPath()}
	 * @return
	 */
	String getSetterFieldPath();
}