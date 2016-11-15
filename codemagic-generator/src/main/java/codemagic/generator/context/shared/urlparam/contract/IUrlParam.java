package codemagic.generator.context.shared.urlparam.contract;

import codemagic.generator.context.shared.FieldProperty;

public interface IUrlParam {

	FieldProperty getParam();

	/**
	 * 
	 * @return true if this request param should initialize a new target entity.
	 *         This allow to get a request param from url, then use it to
	 *         initialize an entity. The name of the entity field to be
	 *         initialized is define in the property
	 *         {@link #getTargetEntityFieldName()}
	 */
	boolean isInitTargetEntity();

	String getTargetEntityFieldName();

}