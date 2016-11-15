package codemagic.generator.context.shared.util;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Verify;

/**
 * For an input name like "enviroments.flgDev", generate:
 * <pre>
 * 1. The capitalized (EnviromentsFlgDev) and uncapitalized (enviromentsFlgDev) names
 * 2. The getter method name
 * 	   a isEnviromentsFlgDev, if flgDev is a boolean 
 *     b getEnviromentsFlgDev, otherwise
 * 3. The setter (getEnviromentsFlgDev) method name
 * 4. The getter field path
 *     a. getEnviroments().isFlgDev, if flgDev is boolean 
 * 	   b. getEnviroments().getFlgDev, otherwise
 * 5. The setter field path
 *     a. getEnviroments().setFlgDev 
 * 6. Keep the original name too (inpuName)
 * </pre> 
 *
 */
public class Name {

	private final boolean isBoolean;
	private final String inputName;
	private final String uncapitalizedName;
	private final String capitalizedName; 
	private final String getter;
	private final String setter;
	private final String getterFieldPath;
	private final String setterFieldPath;
	private final String constantName;

	
	public Name(final String inputName) {
		this(inputName, false);
	}
	
	public Name(final String inputName, final boolean isBoolean) {
		this.inputName = sanitize(inputName).trim();
		
		Verify.verify( !this.inputName.isEmpty(), "The field name cannot be null. You called the begin().setFieldName(..) method?");
		
		this.isBoolean = isBoolean;
		this.uncapitalizedName = buildUncapitalizedName(this.inputName);
		this.capitalizedName = StringUtils.capitalize(this.uncapitalizedName);
		this.getter = buildGetterName(isBoolean, this.capitalizedName);
		this.setter = buildSetterName(this.capitalizedName);
		this.getterFieldPath = buildGetterFieldPath(isBoolean, this.inputName);
		this.setterFieldPath = buildSetterFieldPath(this.inputName);
		this.constantName = buildConstantName(this.capitalizedName);
	}


	//~~~~~~~~~~~~// 
	 // getters    //
	//~~~~~~~~~~~~// 
	
	public boolean isBoolean() {
		return isBoolean;
	}

	/**
	 * 
	 * @return The original name.
	 */
	public String getInputName() {
		return inputName;
	}

	public String getUncapitalizedName() {
		return uncapitalizedName;
	}

	public String getCapitalizedName() {
		return capitalizedName;
	}

	public String getGetter() {
		return getter;
	}

	public String getSetter() {
		return setter;
	}

	public String getConstantName() {
		return constantName;
	}
	
	public String getGetterFieldPath() {
		return getterFieldPath;
	}

	public String getSetterFieldPath() {
		return setterFieldPath;
	}

	
	  //~~~~~~~~~~~~// 
	 // Utilities  //
	//~~~~~~~~~~~~// 


	private String buildSetterName(final String capitalizedName) {
		return "set" + capitalizedName;
	}

	private String buildGetterName(final boolean isBoolean, final String capitalizedName) {
		final String prefix = getGetterPrefix(isBoolean);
		return prefix + capitalizedName;
	}

	private String buildUncapitalizedName(final String inputName) {
		
		final String[] parts = inputName.split("\\.");
		
		final StringBuilder builder = new StringBuilder( inputName.length() );
		
		for (final String s : parts) {
			builder.append( StringUtils.capitalize(s));
		}
		
		return StringUtils.uncapitalize( builder.toString() ) ;
	}

	/**
	 * See {@link #buildPropertyName(String, String)}
	 * 
	 * @param isBoolean
	 *            If true uses the 'is' prefix. Otherwise, uses the 'get'
	 * @param inputName
	 * @return
	 */
	private String buildGetterFieldPath(final boolean isBoolean, final String inputName) {
		final String prefix = getGetterPrefix(isBoolean); 
		return buildPropertyName(inputName, prefix);
	}

	private String getGetterPrefix(final boolean isBoolean) {
		return isBoolean ? "is" : "get";
	}
	
	/**
	 * See {@link #buildPropertyName(String, String)}
	 * @param inputName
	 * @return
	 */
	private String buildSetterFieldPath(final String inputName) {
		return buildPropertyName(inputName, "set"); //setter always has the prefix 'set'
	}

	/**
	 * 
	 * @param inputName
	 *            The name provided by the user. It can be simple as
	 *            "projectName" or a path like "environments.flgdev"
	 * @param prefix
	 *            "set"|"get"|"is"
	 * @return The property name like "getProjectName"|"setProjectName" or
	 *         "getEnvironments().getFlgDev"|getEnvironments().setFlgDev. Note
	 *         that there is no parentheses. Only the name is returned.
	 */
	private String buildPropertyName(final String inputName, final String prefix) {
		
		final String[] parts = inputName.split("\\.");
		
		// Each name receive 5 chars: 'get' and '()'
		final int capacity = inputName.length() + parts.length * 5;
		final StringBuilder builder = new StringBuilder(capacity);
		
		final int lastIndex = parts.length-1;
		for (int i = 0; i < lastIndex; i++) {
			builder.append("get").append(StringUtils.capitalize(parts[i])).append("().");
		}
		
		builder.append(prefix).append(StringUtils.capitalize(parts[lastIndex]));
		
		return builder.toString();
	}

	private String buildConstantName(final String capitalizedName) {

		// This is safe, because the capitalizedName (from inputName) cannot be empty
		final String[] parts = StringUtils.splitByCharacterTypeCamelCase(capitalizedName);
		
		final int lastIndex = parts.length - 1;
		
		final StringBuilder builder = new StringBuilder( capitalizedName.length() + parts.length + 1 );
		
		//Up to before last: Has suffix "_"
		for (int i =0; i <  lastIndex; i++) {
			builder.append(parts[i].toUpperCase()).append("_");
		}
		
		// The last part: Ha no suffix  "_'
		builder.append(parts[lastIndex].toUpperCase());
		
		return builder.toString();
	}

	public static class EmptyName extends Name {
 
		private static final EmptyName INSTANCE = new EmptyName();
		
		private EmptyName() {
			super("<empty>");
		}

		public static EmptyName getInstance() {
			return INSTANCE;
		}
	}
}
