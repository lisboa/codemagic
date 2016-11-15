package codemagic.generator.context.subject.type.infra.processor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.urlparam.UrlParamProcessor;
import codemagic.generator.context.subject.type.TypeContext;

/**
 * For an input list of {@link FieldProperty}, {'projectName', 'version'},
 * returns a method argument based on a context {@link TypeContext}. For
 * example, if the context name is 'Blacklist', returns the String bellow:
 * 
 * <code>
 *   "BlacklistContext.get().getProjectName(), BlacklistContext.get().getVersion()"
 * </code>
 *
 * This returns then can be used to build a target url as showed bellow:
 * 
 * <code>
 * this.targetUrl = StringUtilClient.format( 
 *   	"%s/svc/a/blacklists/%s/%s", 
 *  	AppContextUtil.getBaseUrlForRestServices(), 
 *  	BlacklistContext.get().getProjectName(), 
 *  	BlacklistContext.get().getVersion()
 * );
 * </code>
 */
public class ContextParamProcessor extends UrlParamProcessor {

	private final ContextMethodArgumentBuilder builder;  
	
	public ContextParamProcessor(final TypeContext typeContext) {
		
		Preconditions.checkArgument( typeContext != null , "The RequestParamProcessor requires a non null TypeContext instance.");
		
		builder = new ContextMethodArgumentBuilder(typeContext);
	}

	@Override
	public String buildMethodArguments(final ImmutableList<FieldProperty> params) {
		return builder.buildMethodParams(params);
	}

}