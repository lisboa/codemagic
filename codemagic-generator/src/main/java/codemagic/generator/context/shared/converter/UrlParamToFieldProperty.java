package codemagic.generator.context.shared.converter;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */

import static codemagic.util.shared.common.SanitizerUtil.sanitize;
import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.urlparam.UrlParam;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class UrlParamToFieldProperty implements IToFieldPropertyConverter<UrlParam> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FieldProperty convertOne(final UrlParam input) {
		
		Preconditions.checkArgument( input != null, "The request param to be converted cannot be null" );
		
		return input.getParam();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImmutableList<FieldProperty> convertMultiple(/*@Nullable*/ final ImmutableList<UrlParam> input) {

		final ImmutableList<UrlParam> _fieldNames = sanitize( input, ImmutableList.<UrlParam>of() );
		
		if (_fieldNames.isEmpty()) {
			return ImmutableList.<FieldProperty>of();
		}
		
		final Builder<FieldProperty> builder = ImmutableList.<FieldProperty>builder();
		
		for (final UrlParam p : input) {
			builder.add(p.getParam());
		}
		
		return builder.build();
	}

}
