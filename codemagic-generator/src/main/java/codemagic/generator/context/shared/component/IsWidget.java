package codemagic.generator.context.shared.component;

/**
 * 
 * @param <R>
 *            The Widget type, used as return. This allows to use a fluent API.
 */
public interface IsWidget<R> {
	
	/**
	 * 
	 * @param enable
	 * @return
	 */
	R setEnable(final boolean enable);
	
	R setTabIndex(final int tabIndex);
	
	R setName(final String name);
	
	boolean isAddInJavaCode();
	
	R setAddInJavaCode(boolean addInJavacode);

}
