package codemagic.generator.context.subject.type.infra.holder;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import codemagic.generator.context.shared.urlparam.UrlParam;
import codemagic.generator.context.shared.urlparam.contract.IUrlParamList;
import codemagic.generator.context.subject.type.TypeContext;

/**
 * This class use the decorator pattern to add a list of {@link ContextUrlParam}
 * to {@link IUrlParamList}.
 * 
 * Example (See GetCommand.java.template):
 *
 * In the generator, put the context list in the model: 
 * 
 * <code>
 * final ContextUrlParamList contextUrlParamList = new ContextUrlParamList( sharedContext().get().getUrlParamList(), typeContext);
 * orchContext.getModel().put("contextUrlParamList", contextUrlParamList);
 * </code>
 * 
 * In the template, add the code generator 
 * <code>
 *  ${foreach contextUrlParamList.contextParams p}${if first_p}
 *	String ${p.param.fieldName} = ${p.contextGetterField};
 *  ${end}${end}
 * </code>
 * 
 * The generated code will be: 
 * 
 * <code>
 *  BlacklistContext.get().getProjectName()
 * </code>
 * 
 */
public class ContextUrlParamList implements IUrlParamList {

	private final IUrlParamList core; 
	private final TypeContext typeContext;
	private final ImmutableList<ContextUrlParam> contextParams;
	
	/**
	 * This class use the decorator pattern to add a list of
	 * {@link ContextUrlParam} to {@link IUrlParamList}. With this, it is
	 * possible to build a special getter to get the url parameters held by the
	 * context. This is used to build the targetUrl of restful services.
	 * Normally, the rest url use values obtained from url. For example, a get
	 * to /svc/a/blacklists/{projectName} returns the blacklist of the project
	 * {projectName}. The {projectName} can be getting from the application url.
	 * 
	 * Example of generated code: <code>
	 *    BlacklistContext.get().getProjectName();
	 * </code>
	 * 
	 */
	public ContextUrlParamList(final IUrlParamList core, final TypeContext typeContext) {
		
		Preconditions.checkArgument( core != null , "The underline UrlParam, named core, cannot be null");
		Preconditions.checkArgument( typeContext != null , "The Type context cannot be null");
		
		this.core = core;
		this.typeContext = typeContext;
		this.contextParams = buildContextParams(core);
	}

	private ImmutableList<ContextUrlParam> buildContextParams(final IUrlParamList core) {
		final Builder<ContextUrlParam> builder = ImmutableList.<ContextUrlParam>builder();
		
		for (final UrlParam param : core.getParams()) {
			builder.add( new ContextUrlParam(param, typeContext) );
		}
		return builder.build();
	}

	@Override
	public ImmutableList<UrlParam> getParams() {
		return core.getParams();
	}

	@Override
	public String getFieldMethodDeclaration() {
		return core.getFieldMethodDeclaration();
	}

	@Override
	public String getFieldMethodArgument() {
		return core.getFieldMethodArgument();
	}

	public ImmutableList<ContextUrlParam> getContextParams() {
		return contextParams;
	}
	
}
