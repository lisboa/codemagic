package codemagic.generator.context.shared.component;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import com.google.common.base.Verify;

import codemagic.generator.context.shared.util.ContextUtil;
import codemagic.generator.context.shared.util.Name;
import codemagic.generator.context.shared.util.Name.EmptyName;

/**
 * 
 * @param
 * 			<P>
 *            The type of the parent of this widget.
 */
public class AbstractWidget<P> {

	public static final int EMPTY_TABINDEX = -100;
	
	private final P parent;
	private final String qualifiedJavaType;
	private final String javaType;
	private final boolean clickable;
	
	private boolean addInJavaCode = true;
	
	private boolean enable = true;
	private int tabIndex = EMPTY_TABINDEX;
	private Name name = EmptyName.getInstance();
	
	public AbstractWidget(final P parent, final String qualifiedJavaType, final boolean clickable) {
		this.parent = parent;
		this.qualifiedJavaType = sanitize(qualifiedJavaType).trim();
		this.javaType = ContextUtil.getSimpleClassName( this.qualifiedJavaType ); 
		this.clickable = clickable;
				
		Verify.verify( parent != null, "[%s] The parent cannot be null", this.getClass().getName());
		Verify.verify( !this.qualifiedJavaType.isEmpty(), "[%s] The javaType cannot be null. It will used to generate the type of this widget.", this.getClass().getName());
	}

	public P end() {
		checksJavaName();
		return parent;
	}
	
	private void checksJavaName() {
		if (addInJavaCode) {
			Verify.verify( !(name instanceof EmptyName), "[%s] This widget will be added to java code. Therefore, it should has a name. Are you called the setName() method ?", this.getClass() );
		}
		
	}

	/**
	 * 
	 * @return true is this widget was clickable. This information is used to
	 *         generate click events for clickable widgets.
	 */
	public boolean isClickable() {
		return clickable;
	}

	/**
	 * 
	 * @return The fullqualified classe name for this widget. For example: the bootstrap
	 *         button has the type <code>org.gwtbootstrap3.client.ui.Button</code>
	 */
	public String getQualifiedJavaType() {
		return qualifiedJavaType;
	}

	public String getJavaType() {
		return javaType;
	}

	public boolean isEnable() {
		return enable;
	}

	protected void internalSetEnable(final boolean enable) {
		this.enable = enable;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	/**
	 * Some components don't need the java code. For example, Breadcrumb.
	 * 
	 * @return [default=true] true if the java code, thus uibinder, for this
	 *         widget should be generated. Otherwise, no java code is generated,
	 *         only the uibinder code.
	 */
	public boolean isAddInJavaCode() {
		return addInJavaCode;
	}

	protected void internalSetAddInJavacode(final boolean addInJavacode) {
		this.addInJavaCode = addInJavacode;
	}

	/**
	 * 
	 * @param tabIndex
	 *            [Optional] The tabIndex to used in thie button. If not set,
	 *            the generator will be compute a tabIndex. For example, if a
	 *            page has two components, then the tabIndex will be 1 ad 2.
	 * @see codemagic.generator.context.types.TabdIndexHolder.TabdIndexHolder
	 * @return
	 * 
	 */
	protected void internalSetTabIndex(final int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * 
	 * @param name
	 *            [Optional] The java name for this button. If empty, a name will
	 *            be generated from the {@link #text} field.
	 * @return
	 */
	protected void internalSetName(final String name) {
		this.name =  new Name(name);
	}

	public Name getName() {
		return name;
	}
}
