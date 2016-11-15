package codemagic.generator.context.subject.type.infra.holder;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.urlparam.UrlParam;
import codemagic.generator.context.shared.urlparam.contract.IUrlParam;
import codemagic.generator.context.subject.type.TypeContext;
import codemagic.generator.context.subject.type.infra.util.ContextNameBuilder;

import com.google.common.base.Preconditions;

/**
 * This class use the decorator pattern to add context getter and setter names.
 * 
 * <pre>
 *  if fieldName is 'projectName' and the context name is Blacklist, 
 *  then this class can generate Blacklist.get().getProjectName(), beyond the normal getProjectName().
 * </pre>
 *
 */
public class ContextUrlParam implements IUrlParam {

	private final UrlParam core; 
	private final ContextNameBuilder contextNameBuilder;
	
	
	public ContextUrlParam(final UrlParam core, final TypeContext typeContext) {
		
		Preconditions.checkArgument( core != null , "The underline UrlParam, named core, cannot be null");
		Preconditions.checkArgument( typeContext != null , "The Type context cannot be null");
		
		this.core = core;
		contextNameBuilder = new ContextNameBuilder(typeContext);
	}


	/**
	 * if fieldName is 'projectName' and the context name is Blacklist, then
	 * returns:
	 * 
	 * <code>
	 *  Blacklist.get().getProjectName(), beyond the normal getProjectName().
	 *  </code>
	 * 
	 * @return
	 */
	public String getContextGetterField() {
		return contextNameBuilder.buildGetter(core.getParam());
	}
	
	@Override
	public FieldProperty getParam() {
		return core.getParam();
	}

	@Override
	public boolean isInitTargetEntity() {
		return core.isInitTargetEntity();
	}

	@Override
	public String getTargetEntityFieldName() {
		return core.getTargetEntityFieldName();
	}

}
