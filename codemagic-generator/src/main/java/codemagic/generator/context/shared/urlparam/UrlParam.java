package codemagic.generator.context.shared.urlparam;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import javax.annotation.concurrent.Immutable;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.urlparam.contract.IUrlParam;

import com.google.common.base.Preconditions;

@Immutable
public class UrlParam implements IUrlParam {
	
	// FieldProperty is used here because it contains all logic to create field, getter and setter names.
	private final FieldProperty param;
	private final boolean initTargetEntity;
	private final String targetEntityFieldName;
	
	// TODO: split the FieldProperty in Field, Component and FieldComponentPair
	// 1 - The actual FieldProperty will be replaced by FieldComponentPair
	// 2 - In this class, only Field is used, because no visual information 
	// (Component part) is needed.
	public UrlParam(final FieldProperty param, final boolean initTargetEntity,
			final String targetEntityField) {
		
		Preconditions.checkArgument( param != null, "The request param cannot be null");
		
		this.param = param;
		this.initTargetEntity = initTargetEntity;
		this.targetEntityFieldName = buildTargetEntityField( sanitize(targetEntityField).trim(), this.param.getFieldName() );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FieldProperty getParam() {
		return param;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInitTargetEntity() {
		return initTargetEntity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTargetEntityFieldName() {
		return targetEntityFieldName;
	}

	// utilities
	
	/**
	 * @param targetEntityField
	 * @param paramName
	 * @return The entity field to be initialized. This is the
	 *         {@link #targetEntityFieldName}, if not empty. Otherwise, is the
	 *         {@link #paramName}
	 */
	private String buildTargetEntityField(final String targetEntityField, final String paramName) {
		
		return targetEntityField.isEmpty() ? paramName : targetEntityField ;
	}

}