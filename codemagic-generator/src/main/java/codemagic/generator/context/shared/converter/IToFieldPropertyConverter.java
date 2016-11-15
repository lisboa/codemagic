package codemagic.generator.context.shared.converter;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */

import codemagic.generator.context.shared.FieldProperty;

import com.google.common.collect.ImmutableList;

public interface IToFieldPropertyConverter<T> {

	/**
	 * Convert a String to a {@link FieldProperty}, because
	 * {@link FieldProperty} has the logic to build field, getters and setters
	 * name.
	 * 
	 * Warning: (1) The resulting {@link FieldProperty} can be used only to
	 * build field name, getter name and setter names, because the input is only
	 * the fieldName. Therefore, the others attributes (required, component,
	 * enable, etc), cannot bet set.
	 * 
	 * @param fieldName
	 *            The String to be converted.
	 * @return
	 */
	FieldProperty convertOne(T fieldName);

	/**
	 * Same as {@link #convertOne(String)}, but the input is a List of String
	 * instead of a single String.
	 * 
	 * @param fieldNames
	 * @return
	 */
	ImmutableList<FieldProperty> convertMultiple(/*@Nullable*/ ImmutableList<T> fieldNames);

}