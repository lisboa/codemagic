package codemagic.generator.context.shared.urlparam;

/*>>> import org.checkerframework.checker.nullness.qual.Nullable; */
import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import codemagic.generator.context.shared.FieldProperty;
import codemagic.generator.context.shared.converter.StringToFieldProperty;
import codemagic.generator.context.shared.mehod.FieldMethodArgumentBuilder;
import codemagic.generator.context.shared.mehod.FieldMethodDeclarationBuilder;
import codemagic.generator.context.subject.type.infra.processor.ContextMethodArgumentBuilder;
import grain.util.ParamUtil;

@ThreadSafe
public class UrlParamProcessor {

	// Engine is thread safe
	private static final Engine engine = new Engine();
		
	/**
	 * Build a method argument list that can be used to call methods. This
	 * method can be different implementations, according with the Generator
	 * used. The CommandGen, for example, use the ContextMethodParamBuilder to
	 * implement it, because the command needed to get the values from Context,
	 * using the idiom [Name]Context.get().get[FieldName]()
	 * 
	 * <pre>
	 * For an input {"projectName", "version"}, returns:
	 * 
	 * "BlacklistContext.get().getProjectName(), BlacklistContext.get().getVersion()"
	 * 
	 * This returns can be used in the method call or in the instantiation of an object. 
	 * In the example bellow, the BlacklistState is initialized with the return showed above:
	 * 
	 * new BlacklistState( BlacklistContext.get().getProjectName(), BlacklistContext.get().getVersion() );
	 * </pre>
	 *
	 * Others implementations can returns the concatenated fieldNames:
	 * 'projectName, version'. Therefore, the exactly returns depends on the
	 * specific use case.
	 * 
	 * See codemagic.generator.context.subject.type.infra.
	 * ContextMethodParamBuilder
	 * 
	 * @param params
	 * @return
	 */
	public String buildMethodArguments(final ImmutableList<FieldProperty> params) {
		return new FieldMethodArgumentBuilder().buildMethodParams(params);
	}

	/**
	 * Process a url that contain template variables. This variables should has
	 * the form ${name}. Supposes an url
	 * http://localhost:9090/blacklists/${projectName}/${versionName}, the
	 * {@link Result} has three parts:
	 * <ol>
	 * <li>A list of parameters within the url: {'projectName', 'versionName'}</li>
	 * <li>A interpolated url, that is, a String where the variables are
	 * replaced by '%s'. This allows to use the StringUtilClient to format
	 * string in the generated code.</li>
	 * <li>A String representing the method argument list that can be used to
	 * build a method call. The exactly returns depends on the implementation of
	 * {@link #buildMethodArguments(ImmutableList)}. For a simple
	 * implementation, this can return, for example: 'projectName, version'. For
	 * more complex cases, this returns can be 'Blacklist.get().getProject(),
	 * Blacklist.get().getVersion()' (See {@link ContextMethodArgumentBuilder})</li>
	 * </ol>
	 * 
	 * The result can be used as follow:
	 * 
	 * In the generator, add the interpolated url and the methodArguments
	 * <code>
	 *  commandContext.getModel().put("restUrl", params.getInterpolatedUrl());
		commandContext.getModel().put("requestParamsMethodArguments", params.getMethodArgument());
	 * </code>
	 *  
	 * In the template:
	 * 
	 *  <code>
	 *  return StringUtilClient.format("%s${restUrl}", AppContextUtil.getBaseUrlForRestServices(), ${requestParamsMethodArguments}) ;
	 *  </code>
	 *  
	 * The generated code is:
	 * 
	 *  <code>
	 *  return StringUtilClient.format( "http://localhost:9090/blacklists/%s/%s", AppContextUtil.getBaseUrlForRestServices(), BlacklistContext.get().getProjectName(), BlacklistContext.get().getVersionName(), String.valueOf(start), String.valueOf(pageSize) );
	 *  </code>
	 * 
	 * <pre>
	 * Note that:
	 * 
	 * 1) The ${restUrl} expands to "%s/svc/u/blacklists/%s?page=%s&pageSize=%s"
	 * 2) The ${requestParamsMethodArguments} expands to "BlacklistContext.get().getProjectName()" 
	 * </pre>
	 * 
	 * 
	 * @param urlWithParams A String like http://localhost:9090/blacklists/${projectName}
	 * @return
	 */
	public Result process(final /*@Nullable*/ String urlWithParams) {
	
		if (sanitize(urlWithParams).trim().isEmpty()) {
			return new Result();
		}
		
		// 1. Extract the parameters of the form ${varName} 
		final ImmutableList<String> extractedVariables = ParamUtil.extractParamNames(urlWithParams);
		
		// 2. Replaces ${varName} by %s, since the StringUtilClient.format only understand %s
		final String interpolatedInput = transform(urlWithParams, extractedVariables);

		// 3. Convert to FieldProperty, because it has the logic to build fieldNames, getters, etc.
		final ImmutableList<FieldProperty> urlVariables =  new StringToFieldProperty().convertMultiple(extractedVariables);
				
		// 4. Build methodArguments
		final String methodArgument = buildMethodArguments(urlVariables);  
		
		final String methodDeclarations = new FieldMethodDeclarationBuilder().buildMethodParams(urlVariables);
		
		return new Result( methodArgument, interpolatedInput, methodDeclarations, urlVariables);
	}

	  //
	 // Utilities
	//
	
	private String transform(final String urlWithParams, final ImmutableList<String> extractedParams) {
		
		assert !extractedParams.isEmpty() : "The request params list cannot be empty.";
		
		final Map<String, Object> model = Maps.newHashMap();
		for (final String p : extractedParams) {
			model.put(p, "%s");  
		}
		final String interpolatedUrl = engine.transform(urlWithParams, model);
		return interpolatedUrl;
	}
	
	@Immutable
	public static class Result {
		private final ImmutableList<FieldProperty> extractedVariables;
		private final String methodArguments;
		private final String interpolatedUrl;
		private final String methodDeclarations;

		private Result() {
			this("","", "", null);
		}
		
		private Result(
				final String paramArgumentList, 
				final String interpolatedUrl, 
				final String methodDeclarations,
				final ImmutableList<FieldProperty> extractedVariables) {

			// this.extractedParams = sanitize( extractedVariables, ImmutableList.<String>of() );
			this.methodArguments = sanitize( paramArgumentList ).trim();
			this.interpolatedUrl = sanitize( interpolatedUrl ).trim();
			this.methodDeclarations = sanitize( methodDeclarations ).trim();
			this.extractedVariables = sanitize( extractedVariables, ImmutableList.<FieldProperty>of() );
		}

		/**
		 * 
		 * @return A {@link ImmutableList} with the parameters within a String.
		 *         The parameters has the form ${paramName}
		 */
		public ImmutableList<FieldProperty> getExtractedVariables() {
			return extractedVariables;
		}

		public String getMethodDeclarations() {
			return methodDeclarations;
		}

		/**
		 * 
		 * @return A String that will be used to build method arguments using
		 *         the {@link #extractedParams}. For example. If the
		 *         {@link #extractedParams} is {"abc", xyz}, then the return
		 *         will be "abc,xyz"
		 */
		public String getMethodArguments() {
			return methodArguments;
		}

		/**
		 * 
		 * @return
		 */
		public String getInterpolatedUrl() {
			return interpolatedUrl;
		}
	}
}
