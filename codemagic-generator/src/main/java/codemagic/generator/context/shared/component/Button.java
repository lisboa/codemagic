package codemagic.generator.context.shared.component;

import static codemagic.util.shared.common.SanitizerUtil.sanitize;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.Maps;

import codemagic.generator.context.shared.util.ContextUtil;
import codemagic.generator.context.shared.util.Name.EmptyName;

public class Button<P> extends AbstractWidget<P> implements IsWidget<Button<P>> {
	
	public Button(P parent) {
		super(parent, "org.gwtbootstrap3.client.ui.Button", true);
	}

	public static final String PREFIX_NAME_FOR_BUTTONS = "btn";
	
	String text = "";
	private Optional<ContextualType> type = Optional.absent();
	private String dataLoadingText = "";
	private String marginRight = "";
	private String icon = "";
	
	// ~~~ Predefined actions: only one action is allowed at a time
	private final Map<ActionType, Object> actionStore = Maps.newHashMap();
	private Optional<ActionType> actionType = Optional.absent();
	
	public enum ActionType {
		fullNameToken,
		htmlHelp
	}
	
	@Override
	public P end() {
		buildNameIfNecessary();
		return super.end();
	}
	
	public String getText() {
		return text;
	}
	
	/**
	 * @param text
	 *            [Required] The text showed by the button. An exception will be
	 *            raised if this field is empty.
	 * @return
	 */
	public Button<P> setText(final String text) {
		this.text = sanitize(text).trim() ;
		return this;
	}
	
	public Button<P> setType(final ContextualType type) {
		this.type =  Optional.fromNullable(type);
		return this;
	}

	public Optional<ContextualType> getType() {
		return type;
	}
	
	
	
	public String getIcon() {
		return icon;
	}

	public Button<P> setIcon(final String icon) {
		this.icon = sanitize(icon).trim();
		return this;
	}

	public String getDataLoadingText() {
		return dataLoadingText;
	}
	
	public Button<P> setDataLoadingText(final String dataLoadingText) {
		this.dataLoadingText = sanitize(dataLoadingText).trim();
		return this;
	}
	
	public String getMarginRight() {
		return marginRight;
	}
	
	public Button<P> setMarginRight(final String marginRight) {
		this.marginRight =  sanitize(marginRight).trim();
		return this;
	}
	
	public Optional<ActionType> getActionType() {
		return actionType;
	}


	/**
	 * A qualified constant name or a literal to where to go when the button is
	 * clicked.
	 * 
	 * <pre>
	 * Examples:
	 * 1) "db.assist.view.client.place.NameTokens.uHome"  (if using a constant)
	 * 2) "\"/u/home\""  (if using a literal)
	 * </pre>
	 * 
	 * If the string is not quoted, it is assumed that is a constant.
	 * 
	 * @return A qualified constant name or a literal to where to go when the
	 *         button is clicked.
	 */
	public String getFullNameToken() {
		return sanitize( (String) actionStore.get( ActionType.fullNameToken ));
	}

	/**
	 * 
	 * @return A String with html help that should be displayed when the
	 *         button is clicked.
	 */
	public Optional<HelpData> getHtmlHelp() {
	  	return Optional.fromNullable( (HelpData) actionStore.get( ActionType.htmlHelp ) );
	}
	
	
	//~~~~~~~~~~~-//  
	 // Utilities  //
	//~~~~~~~~~~~~//
	void checkText() {
		Verify.verify( !text.isEmpty(), "The button text cannot be null" );
	}

	/**
	 * If {@link #name} is empty, build the button java name from the text.
	 * Otherwise, do nothing.
	 * 
	 * <Pre>
	 * Example: For text = "Try download now", the name will be "btnTryDownloadNow"
	 * 
	 * </pre>
	 */
	void buildNameIfNecessary() {
		
		if ( !(getName() instanceof EmptyName) ) {
			return;
		}
		
		internalSetName( ContextUtil.buildValidJavaName( this.text , PREFIX_NAME_FOR_BUTTONS)  );
	}

	@Override
	public Button<P> setEnable(final boolean enable) {
		internalSetEnable(enable);
		return this;
	}

	@Override
	public Button<P> setTabIndex(final int tabIndex) {
		internalSetTabIndex(tabIndex);
		return this;
	}

	@Override
	public Button<P> setName(final String name) {
		internalSetName(name);
		return this;
	}

	@Override
	public Button<P> setAddInJavaCode(final boolean value) {
		internalSetAddInJavacode(value);
		return this;
	}

	public Button<P> whenClickedNavegateTo(final String fullNameToken) {
		checkShouldHaveOnlyOneAction();
		
		final String _fullNameToken = sanitize(fullNameToken).trim();
		
		actionStore.put(ActionType.fullNameToken, _fullNameToken);
		
		Verify.verify( !_fullNameToken.isEmpty(), "The nameToken to where to go cannot be null. It can be a constant or a literal.");
		
		actionType = Optional.of( ActionType.fullNameToken );
		
		return this;
	}

	/**
	 * @param htmlHelp
	 * @return
	 */
	public Button<P> whenClickedShowHelp(final String htmlHelp, final String javaSourceFolder, final String packageName) {
		checkShouldHaveOnlyOneAction();

		actionStore.put(ActionType.htmlHelp, new HelpData(htmlHelp, javaSourceFolder, packageName));
		
		actionType = Optional.of( ActionType.htmlHelp );
		
		return this;
	}

	
	private void checkShouldHaveOnlyOneAction() {
		Preconditions.checkArgument( actionStore.size() < 2, "The button can do only one action. Do you have called more then one whenClicked* actions ?" );
	}

	public static class HelpData {
		private final String htmlHelp;
		private final String javaSourceFolder;
		private final String packageName;
		
		public HelpData(final String htmlHelp, final String javaSourceFolder, final String packageName) {
			this.htmlHelp = sanitize(htmlHelp).trim();
			this.javaSourceFolder = sanitize(javaSourceFolder).trim();
			this.packageName = sanitize(packageName).trim();
			
			Verify.verify( !this.htmlHelp.isEmpty(), "The help text cannot be empty");
			Verify.verify( !this.javaSourceFolder.isEmpty(), "The help java source folder cannot be empty");
			Verify.verify( !this.packageName.isEmpty(), "The help package cannot be empty");
			
		}

		public String getHtmlHelp() {
			return htmlHelp;
		}

		public String getJavaSourceFolder() {
			return javaSourceFolder;
		}

		public String getPackageName() {
			return packageName;
		}
		
	}
}
