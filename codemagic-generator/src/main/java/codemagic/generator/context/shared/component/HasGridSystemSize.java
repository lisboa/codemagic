package codemagic.generator.context.shared.component;

public interface HasGridSystemSize {

	/**
	 * The column size, used to specify how many cells should be used by this
	 * breadcrumb. Values: [MD|LG]_1, [MD|LG]_2, .. [MD|LG]_12
	 * 
	 * @return
	 */
	String getGridSystemSize();
}
