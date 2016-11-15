package codemagic.generator.context.shared.mehod;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codemagic.generator.context.shared.FieldProperty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class AbstractMethodParamBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger( AbstractMethodParamBuilder.class ); 
    
	public abstract String buildOneMethodParam(final FieldProperty p);
	
	/**
	 * It allows arbitrary construction of the list of arguments or parameters
	 * that can be used in the method declaration or method call.
	 * 
	 * The custom code is at the method {@link #buildOneMethodParam(FieldProperty)} 
	 * 
	 * Example 1
	 * 
	 * For an input {"projectName", "version"} and an implementation of the
	 * method {@link #buildOneMethodParam(FieldProperty)} as showed bellow
	 * 
	 * <code>
	 * capitalizedContextClassName + ".get()." + p.getGetterField() + "()";
	 * </code>
	 * 
	 * Return
	 * 
	 * BlacklistContext.get().getProjectName(),
	 * BlacklistContext.get().getVersion()
	 * 
	 * @param _params
	 * @return
	 */
	public String buildMethodParams(final ImmutableList<FieldProperty> params) {
		
		final ImmutableList<FieldProperty> _params = sanitize(params, ImmutableList.<FieldProperty>of() );
		
		if (_params.isEmpty()) {
			
			LOGGER.warn("The list of FieldProperty is empty. Returning an empty String ");
			
			return "";
		}
		
		// 1. Estimate the initial capacity;.Assumes that each param has a mean
		// size equals to 12 characters.
		final int capacity = _params.size() * 12;
		
		final StringBuilder builder = new StringBuilder( capacity );
		
		// 2. To prevents the comma to be added after the last param
		final int lastIndex = _params.size() - 1;
		
		// 3a. Add params up to the before last index: The comma is added to the end
		for (int i = 0; i <  lastIndex; i++) {
			
			final FieldProperty p = _params.get(i);
			
			builder.append(buildOneMethodParam(p)).append(",");
		}
		
		// 3b. Add the last param: The comma is _not_ added to the end
		final FieldProperty last = Iterables.getLast(_params);
		builder.append(buildOneMethodParam(last));
		
		return builder.toString();
	}
}
