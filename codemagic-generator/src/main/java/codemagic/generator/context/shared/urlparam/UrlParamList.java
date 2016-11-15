package codemagic.generator.context.shared.urlparam;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.converter.UrlParamToFieldProperty;
import codemagic.generator.context.shared.mehod.FieldMethodArgumentBuilder;
import codemagic.generator.context.shared.mehod.FieldMethodDeclarationBuilder;
import codemagic.generator.context.shared.urlparam.contract.IUrlParamList;

/**
 * Keep a List of all {@link UrlParam} defined in the begin() section.
 */
@Immutable
public class UrlParamList implements IUrlParamList {
	private final ImmutableList<UrlParam> params;
	private final String fieldMethodDeclaration; //To create a method
	private final String fieldMethodArgument;    // To call the method

	
	private UrlParamList(final ImmutableList<UrlParam> params) {
		
		Preconditions.checkArgument( params != null, "The list of RequestParam cannot be null" );
		
		this.params = params;
		
		final ImmutableList<FieldProperty> fieldParams = new UrlParamToFieldProperty().convertMultiple( params );
		
		this.fieldMethodDeclaration = buildMethodDeclaration( fieldParams );
		this.fieldMethodArgument = buildMethodArgument(fieldParams);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImmutableList<UrlParam> getParams() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFieldMethodDeclaration() {
		return fieldMethodDeclaration;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFieldMethodArgument() {
		return fieldMethodArgument;
	}

	  //~~~~~~~~~~~
	 // Utilities
	//~~~~~~~~~~~~
	
	private String buildMethodDeclaration(final ImmutableList<FieldProperty> fieldParams) {
		
		return new FieldMethodDeclarationBuilder().buildMethodParams(fieldParams);
	}
	
	private String buildMethodArgument(final ImmutableList<FieldProperty> fieldParams) {
		
		return new FieldMethodArgumentBuilder().buildMethodParams(fieldParams);
	}
	
	
	public static class Builder {
		private final com.google.common.collect.ImmutableList.Builder<UrlParam> builder = com.google.common.collect.ImmutableList.builder();
		
		public IUrlParamList build() {
			return new UrlParamList( builder.build() ); //NOPMD
		}
		
		public Builder addRequestParam(final UrlParam param) {
			builder.add(param); //The ImmutableList already no accept null values. No check is needed here 
			return this;
		}
	}
}