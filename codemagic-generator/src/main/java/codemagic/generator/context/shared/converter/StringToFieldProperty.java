package codemagic.generator.context.shared.converter;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import javax.annotation.concurrent.ThreadSafe;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.FieldProperty.FieldType;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * This class is thread safe because there is no shared property and the return
 * types are immutables.
 *
 */
@ThreadSafe
public class StringToFieldProperty implements IToFieldPropertyConverter<String> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FieldProperty convertOne(final String fieldName) {
		
		final String _fieldName = sanitize(fieldName).trim();
		
		Verify.verify( !_fieldName.isEmpty(), "The input fieldName cannot be null." ); 
		
		return new FieldProperty.Builder().setFieldName(_fieldName).setFieldType(FieldType.STRING).build();
	}
	
	/**
	 * {@inheritDoc}
	 */

	@Override
	public ImmutableList<FieldProperty> convertMultiple(final /*@Nullable*/ ImmutableList<String> fieldNames) {
		
		final ImmutableList<String> _fieldNames = sanitize( fieldNames, ImmutableList.<String>of() );
		
		if (_fieldNames.isEmpty()) {
			return ImmutableList.<FieldProperty>of();
		}
			
		final Builder<FieldProperty> builder = ImmutableList.<FieldProperty>builder();
		
		for (final String fieldName : fieldNames) {
			builder.add( convertOne(fieldName) );
		}
		return builder.build();
	}
}
