package codemagic.generator.context.shared.urlparam.contract;

import codemagic.generator.context.shared.urlparam.UrlParam;

import com.google.common.collect.ImmutableList;

public interface IUrlParamList {

	ImmutableList<UrlParam> getParams();

	/**
	 * A method declarations with all requested parameters. Use this return to
	 * build method that have request parameter as method arguments. This method
	 * care about the last comma problems. That is, the last declaration has no
	 * comma.
	 * 
	 * <pre>
	 * 1. Example of return
	 *    "final String projectName, final String version"
	 *    
	 * where "projectName" and "version" are values of {@link #params}.
	 * 
	 * 2. Example of usage
	 * 
	 * In the template, you can create a method as follow:
	 *  
	 * public ${BaseObjectName}State(${requestParamHolder.fieldMethodDeclaration}) {
	 *   ...
	 * } 
	 * 
	 * When interpolated, the template above result in the code bellow:
	 * 
	 * public BlacklistState(final String projectName, final String version) {
	 *    ... 	    
	 * 	}
	 * </pre>
	 * 
	 * @return A method declarations with all url prameters held by this holder. Use this
	 *         return to build method that have url parameter as method
	 *         declaration. For an example of use, see the variable
	 *         ${requestParamHolder.fieldMethodDeclaration} in the template
	 *         "templates/type/_BaseObjectName_State.java.template"
	 */
	String getFieldMethodDeclaration();

	String getFieldMethodArgument();

}